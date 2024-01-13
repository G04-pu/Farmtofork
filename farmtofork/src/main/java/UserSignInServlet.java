import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;

public class UserSignInServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username"); // Get username from login form
        String password = request.getParameter("password"); // Get password from login form

        // Database connection parameters
        String url = "jdbc:mysql://localhost:3306/farm2fork";
        String dbUsername = "root";
        String dbPassword = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);

            // Validate user credentials from the database
            String userType = validateUser(connection, username, password);

            if (userType != null) {
                // Create a session or retrieve the existing session
                HttpSession session = request.getSession();
                session.setAttribute("username", username); // Set the username attribute in the session
                session.setAttribute("userType", userType); // Set the userType attribute in the session

                // Redirect to the corresponding page based on user type
                if (userType.equals("user")) {
                    response.sendRedirect("hs.html");
                } else if (userType.equals("farmer")) {
                    response.sendRedirect("farm.html");
                } else if (userType.equals("distributor")) {
                    response.sendRedirect("distributerpage3.html");
                } else if (userType.equals("admin")) {
                    response.sendRedirect("adminpage3.html");
                }
            } else {
                // Invalid credentials, redirect back to login page or show an error
                response.sendRedirect("si.html"); // Redirect to login page
            }

            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Validate user credentials against the database and return the user type
    private String validateUser(Connection connection, String username, String password) throws SQLException {
        String query = "SELECT user_type FROM accounts WHERE username = ? AND password = ?";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, username);
        pstmt.setString(2, password);
        ResultSet rs = pstmt.executeQuery();

        String userType = null;
        if (rs.next()) {
            userType = rs.getString("user_type");
        }

        rs.close();
        pstmt.close();

        return userType;
    }
}
