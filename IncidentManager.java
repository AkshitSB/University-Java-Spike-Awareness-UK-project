// (SID: 2506288) / (Team: Kafka)
// Manages incident reports, including viewing pending reports for reviewers.
import java.sql.*;

public class IncidentManager {

    private static final String URL = "jdbc:sqlite:spikeawareness.db";

    // View only pending reports (for reviewers)
    public static void viewPendingIncidents() {

        String sql = "SELECT * FROM incident_reports WHERE status = 'pending'";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            boolean found = false;

            while (rs.next()) {
                found = true;

                System.out.println(
                        "[" + rs.getInt("report_id") + "] " +
                                rs.getString("city") + " | " +
                                rs.getString("incident_date")
                );

                System.out.println("    " + rs.getString("description"));
                System.out.println("----------------------------------");
            }

            if (!found) {
                System.out.println("No pending reports.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}