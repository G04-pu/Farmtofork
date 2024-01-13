import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserSignupServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Database connection parameters
        String url = "jdbc:mysql://localhost:3306/farm2fork";
        String dbUsername = "root";
        String dbPassword = "";

        // SQL query to insert new user into the database
        String query = "INSERT INTO accounts (username, password, user_type) VALUES (?, ?, 'user')";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            
            // Prepare and execute the query
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // Sign-up successful
                // You may redirect to a success page or perform other actions
                response.sendRedirect("hs.html"); // Assuming userpage3 is HTML
            } else {
                // Sign-up failed
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.println("<html><body>");
                out.println("<h3>Sign-Up Failed</h3>");
                out.println("<p>Please try again.</p>");
                out.println("</body></html>");
            }

            // Close resources
            pstmt.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
