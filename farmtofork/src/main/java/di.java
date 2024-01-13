import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.Enumeration;

public class di extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String companyName = (String) session.getAttribute("username");

        int numberOfBatches = Integer.parseInt(request.getParameter("numberOfBatches"));

        String url = "jdbc:mysql://localhost:3306/farm2fork"; // Change to your database name
        String dbUsername = "root";
        String dbPassword = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);

            String query = "INSERT INTO distributerdb (company_name, produce_tracking_number, batch_tracking_number, weight) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);

            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h2>Debug Information</h2>");

            // Log received parameters
            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                String[] paramValues = request.getParameterValues(paramName);
                for (String paramValue : paramValues) {
                    out.println("<p>Parameter - " + paramName + ", Value - " + paramValue + "</p>");
                }
            }

            for (int i = 1; i <= numberOfBatches; i++) {
                String produceTrackingNumber = request.getParameter("produceTrackingNumber" + i);
                String batchTrackingNumber = request.getParameter("batchTrackingNumber" + i);
                double weight = Double.parseDouble(request.getParameter("weight" + i));

                pstmt.setString(1, companyName);
                pstmt.setString(2, produceTrackingNumber);
                pstmt.setString(3, batchTrackingNumber);
                pstmt.setDouble(4, weight);

                int rowsAffected = pstmt.executeUpdate();
                out.println("<p>Rows affected for Batch " + i + ": " + rowsAffected + "</p>");
            }

            pstmt.close();
            connection.close();

            out.println("<h2>Data successfully stored for distributor</h2>");
            out.println("</body></html>");
        } catch (ClassNotFoundException | SQLException | NumberFormatException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Exception occurred: " + e.getMessage());
        }
    }
}
