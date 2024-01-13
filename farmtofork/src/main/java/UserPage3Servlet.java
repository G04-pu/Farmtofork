import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserPage3Servlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String batchTrackingNumber = request.getParameter("batchTrackingNumber");

        // Database connection parameters
        String url = "jdbc:mysql://localhost:3306/farm2fork";
        String dbUsername = "root";
        String dbPassword = "";

        // SQL query to fetch all specified fields from both farmerdb and distributerdb
        String query = "SELECT f.username AS farmer_username, f.produce_name, f.pesticides, " +
                "d.company_name AS distributor_name, d.batch_tracking_number " +
                "FROM farmerdb f JOIN distributerdb d ON f.produce_tracking_number = d.produce_tracking_number " +
                "WHERE d.batch_tracking_number = ?";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            
            // Prepare and execute the query
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, batchTrackingNumber);
            ResultSet rs = pstmt.executeQuery();

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");

            if (rs.next()) {
                // Display retrieved data from both farmerdb and distributerdb
                out.println("<h3>Batch Details</h3>");
                out.println("<p>Farmer Username: " + rs.getString("farmer_username") + "</p>");
                out.println("<p>Produce Name: " + rs.getString("produce_name") + "</p>");
                out.println("<p>Pesticides: " + rs.getString("pesticides") + "</p>");
                out.println("<p>Distributor Name: " + rs.getString("distributor_name") + "</p>");
                out.println("<p>Batch Tracking Number: " + rs.getString("batch_tracking_number") + "</p>");
                // Include other retrieved details as needed
            } else {
                // No data found for the given batch tracking number
                out.println("<h3>No Details Found</h3>");
                out.println("<p>Please check the batch tracking number and try again.</p>");
            }

            out.println("</body></html>");

            // Close resources
            rs.close();
            pstmt.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
