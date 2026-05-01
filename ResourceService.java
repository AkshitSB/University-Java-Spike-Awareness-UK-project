// (SID: 2506288) / (Team: Kafka)
// Manages resources for guides
import java.sql.*;

public class ResourceService {

    private static final String URL = "jdbc:sqlite:spikeawareness.db";

    // Displays steps for a guide
    public static void viewResources(String type) {

        String sql = "SELECT * FROM resources WHERE type = ? ORDER BY step_number";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, type);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getInt("step_number") + ". " +
                        rs.getString("content"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Adds a new step
    public static void addResource(String type, String content) {

        String sql = """
            INSERT INTO resources (type, step_number, content)
            VALUES (?, 
            (SELECT COALESCE(MAX(step_number),0)+1 FROM resources WHERE type=?), 
            ?)
        """;

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, type);
            pstmt.setString(2, type);
            pstmt.setString(3, content);

            pstmt.executeUpdate();
            System.out.println("Step added");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Updates step content
    public static void updateResource(int id, String content) {

        String sql = "UPDATE resources SET content=? WHERE resource_id=?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, content);
            pstmt.setInt(2, id);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Deletes a step
    public static void deleteResource(int id) {

        String sql = "DELETE FROM resources WHERE resource_id=?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}