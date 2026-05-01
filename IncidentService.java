// (SID: 2506288) / (Team: Kafka)
// Service class for handling incident reports, including reporting, viewing, and reviewing incidents.
import java.sql.*;

public class IncidentService {

    private static final String URL = "jdbc:sqlite:spikeawareness.db";

    // Saves a new incident report
    public static void reportIncident(String city, String desc, String date,
                                      Integer adminId, boolean anonymous, boolean consent) {

        String sql = """
            INSERT INTO incident_reports 
            (city, description, incident_date, admin_id, is_anonymous, status, consent)
            VALUES (?, ?, ?, ?, ?, 'pending', ?)
        """;

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, city);
            pstmt.setString(2, desc);
            pstmt.setString(3, date);

            if (anonymous || adminId == null) {
                pstmt.setNull(4, Types.INTEGER);
                pstmt.setInt(5, 1);
            } else {
                pstmt.setInt(4, adminId);
                pstmt.setInt(5, 0);
            }

            pstmt.setInt(6, consent ? 1 : 0);

            pstmt.executeUpdate();
            System.out.println("Incident reported");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Shows all incidents
    public static void viewIncidents() {

        String sql = "SELECT * FROM incident_reports";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(
                        rs.getInt("report_id") + " | " +
                                rs.getString("city") + " | " +
                                rs.getString("incident_date") + " | " +
                                rs.getString("status")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Shows only pending reports
    public static void viewPendingIncidents() {

        String sql = "SELECT * FROM incident_reports WHERE status = 'pending'";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println("[" + rs.getInt("report_id") + "] " +
                        rs.getString("city") + " | " +
                        rs.getString("incident_date"));

                System.out.println("  " + rs.getString("description"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Approves or rejects a report
    public static void reviewIncident(int id, String decision, String newCity, String newDesc) {

        String status = decision.equalsIgnoreCase("a") ? "approved" : "rejected";

        String sql = """
            UPDATE incident_reports
            SET status = ?,
                city = CASE WHEN ? = '' THEN city ELSE ? END,
                description = CASE WHEN ? = '' THEN description ELSE ? END,
                reviewed_at = CURRENT_TIMESTAMP
            WHERE report_id = ?
        """;

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setString(2, newCity);
            pstmt.setString(3, newCity);
            pstmt.setString(4, newDesc);
            pstmt.setString(5, newDesc);
            pstmt.setInt(6, id);

            pstmt.executeUpdate();
            System.out.println("Report " + status);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Shows public stories (approved + consent)
    public static void viewPublicStories() {

        String sql = """
            SELECT city, description, incident_date
            FROM incident_reports
            WHERE status = 'approved' AND consent = 1
            ORDER BY report_timestamp DESC
            LIMIT 10
        """;

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(
                        rs.getString("city") + " | " +
                                rs.getString("incident_date")
                );
                System.out.println(rs.getString("description"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Calculates risk per city
    public static void citySafetyReport() {

        String sql = """
            SELECT city,
            COUNT(CASE WHEN status='approved' 
            AND incident_date >= date('now','-4 months') 
            THEN 1 END) as count
            FROM incident_reports
            GROUP BY city
        """;

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                int c = rs.getInt("count");
                String level = (c >= 10) ? "HIGH" : (c >= 5) ? "MEDIUM" : "LOW";

                System.out.println(rs.getString("city") + " | " + level);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}