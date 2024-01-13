import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

public class ss extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Establish database connection
            String url = "jdbc:mysql://localhost:3306/farm2fork";
            String dbUsername = "root";
            String dbPassword = "";
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);

            // Get parameter from the request
            String batchTrackingNumber = request.getParameter("batchTrackingNumber");

            // Retrieve data from distributerdb
            String distributerQuery = "SELECT * FROM distributerdb WHERE batch_tracking_number = ?";
            PreparedStatement distributerPstmt = connection.prepareStatement(distributerQuery);
            distributerPstmt.setString(1, batchTrackingNumber);
            ResultSet distributerResultSet = distributerPstmt.executeQuery();

            // Process distributerdb data
            while (distributerResultSet.next()) {
                String companyName = distributerResultSet.getString("company_name");
                String produceTrackingNumber = distributerResultSet.getString("produce_tracking_number");
                double weight = distributerResultSet.getDouble("weight");
                out.println("<html>");
                out.println("<head>\r\n"
                		+ "    <title>User Page 3</title>\r\n"
                		+ "      <style>\r\n"
                		+ "        body {\r\n"
                		+ "            font-family: Arial, sans-serif;\r\n"
                		+ "            margin: 0;\r\n"
                		+ "            padding: 0;\r\n"
                		+ "            background: linear-gradient(to bottom right, #ffcc00, #ff6699); /* Gradient Background */\r\n"
                		+ "            display: flex;\r\n"
                		+ "            justify-content: center;\r\n"
                		+ "            align-items: center;\r\n"
                		+ "            height: 100vh;\r\n"
                		+ "        }\r\n"
                		+ "\r\n"
                		+ "        div {\r\n"
                		+ "            text-align: center;\r\n"
                		+ "            background-color: white;\r\n"
                		+ "            padding: 20px;\r\n"
                		+ "            border-radius: 8px;\r\n"
                		+ "            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);\r\n"
                		+ "        }\r\n"
                		+ "\r\n"
                		+ "        h2 {\r\n"
                		+ "            color: #333;\r\n"
                		+ "            text-align: center;\r\n"
                		+ "        }\r\n"
                		+ "\r\n"
                		+ "    </style>\r\n"
                		+ "</head>");
                out.println("<body><div>");
                out.println("<h2>Distributer's Information:</h2>");
                out.println("<p>Company Name: " + companyName + "</p>");
                out.println("<p>Produce Tracking Number: " + produceTrackingNumber + "</p>");
                out.println("<p>Weight: " + weight + "</p>");

                // Retrieve data from farmerdb using produce_tracking_number from distributerdb
                String farmerQuery = "SELECT * FROM farmerdb WHERE produce_tracking_number = ?";
                PreparedStatement farmerPstmt = connection.prepareStatement(farmerQuery);
                farmerPstmt.setString(1, produceTrackingNumber);
                ResultSet farmerResultSet = farmerPstmt.executeQuery();

                // Process farmerdb data
                while (farmerResultSet.next()) {
                    String farmerUsername = farmerResultSet.getString("username");
                    String produceName = farmerResultSet.getString("produce_name");
                    String pesticides = farmerResultSet.getString("pesticides");

                    out.println("<h2>Farmer's Informationnjh:</h2>");
                    out.println("<p>Username: " + farmerUsername + "</p>");
                    out.println("<p>Produce Name: " + produceName + "</p>");
                    out.println("<p>Pesticides: " + pesticides + "</p>");

                    // Retrieve data from accountsdb using username from farmerdb
                    String accountsQuery = "SELECT location_or_batch FROM accounts WHERE username = ?";
                    PreparedStatement accountsPstmt = connection.prepareStatement(accountsQuery);
                    accountsPstmt.setString(1, farmerUsername);
                    ResultSet accountsResultSet = accountsPstmt.executeQuery();

                    // Process accountsdb data
                    while (accountsResultSet.next()) {
                        String locationOrBatch = accountsResultSet.getString("location_or_batch");
                        out.println("<h2>Location information:</h2>");
                        out.println("<p>Location of farmer: " + locationOrBatch + "</p>");
                    }
                    accountsResultSet.close();
                    accountsPstmt.close();
                }
                farmerResultSet.close();
                farmerPstmt.close();
                out.println("</body></html></div>");
            }

            // Close resources
            distributerResultSet.close();
            distributerPstmt.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Exception occurred: " + e.getMessage());
        }
    }
}
