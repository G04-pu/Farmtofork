import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

public class AdminPage3Servlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String userType = request.getParameter("userType");
        String loc = request.getParameter("loc");

        String url = "jdbc:mysql://localhost:3306/farm2fork";
        String dbUsername = "root";
        String dbPassword = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);

            String query = "INSERT INTO accounts (username, password, user_type, location_or_batch) VALUES (?, ?, ?,?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, userType);
            pstmt.setString(4, loc);

            int rowsAffected = pstmt.executeUpdate();

            pstmt.close();
            connection.close();

            if (rowsAffected > 0) {
                out.println("<html><body>");
                out.println("<h2>Account created successfully!</h2>");
                out.println("<form action='adminpage3.html' method='post'>");
                out.println("<p>Do you want to enter more data?:</p>");
                out.println("<input type='submit' value='Yes'>");
                out.println("<input type='button' value='No' onclick='window.location.href=\"si.html\";'>");
                out.println("</form>");

                out.println("</body></html>");
            } else {
                out.println("<html><body>");
                out.println("<h2>Failed to create account. Please try again.</h2>");
                out.println("</body></html>");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Exception occurred: " + e.getMessage());
        }
    }
}
