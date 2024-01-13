import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;

public class FarmerPage3Servlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Retrieve the existing session (if any)

        if (session != null) {
            String username = (String) session.getAttribute("username"); // Get username from session
            String userType = (String) session.getAttribute("userType"); // Get userType from session

            if (userType != null && userType.equals("farmer")) {
                // Get data from the farmerpage3.html form
                String produceTrackingNumber = request.getParameter("produceTrackingNumber");
                String produceName = request.getParameter("produceName");
                String pesticide = request.getParameter("pesticideUsed"); // Correct parameter name for pesticides in the HTML form
                
                // Database connection parameters
                String url = "jdbc:mysql://localhost:3306/farm2fork";
                String dbUsername = "root";
                String dbPassword = "";

                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);

                    // Prepare and execute SQL INSERT statement
                    String query = "INSERT INTO farmerdb (produce_tracking_number, username, produce_name, pesticides) VALUES (?, ?, ?, ?)";
                    PreparedStatement pstmt = connection.prepareStatement(query);
                    pstmt.setString(1, produceTrackingNumber);
                    pstmt.setString(2, username);
                    pstmt.setString(3, produceName);
                    pstmt.setString(4, pesticide);

                    int rowsAffected = pstmt.executeUpdate();
                    
                    pstmt.close();
                    connection.close();

                    if (rowsAffected > 0) {
                        response.setContentType("text/html");
                        PrintWriter out = response.getWriter();
                        out.println("<html><head>");
                        out.println("<style>");
                        out.println("body { background: linear-gradient(to bottom, #ffcc00, #ff6600); }");
                        out.println("form { text-align: center; margin-top: 50px; }");
                        out.println("</style>");
                        out.println("</head><body>");
                        out.println("<h2>Data successfully stored for farmer: " + username + "</h2>");

                        // Offer to enter more data
                        out.println("<form action='farm.html' method='post'>");
                        out.println("<p>Do you want to enter more data?</p>");
                        out.println("<input type='submit' value='Yes'>");
                        out.println("<input type='button' value='No' onclick='window.location.href=\"si.html\";'>");
                        out.println("</form>");

                        out.println("</body></html>");
                    } else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        response.getWriter().println("Data insertion failed");
                    }
                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().println("Exception occurred: " + e.getMessage());
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Unauthorized user
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // No session found
        }
    }
}
