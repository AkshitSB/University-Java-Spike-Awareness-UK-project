// (SID: 2506288) / (Team: Kafka)
// Admin class for handling admin login and management
import java.sql.*;

public class Admin {

    private static final String URL = "jdbc:sqlite:spikeawareness.db";

    // Handles login by checking username and password
    public static String login(String username, String password) {

        String sql = "SELECT password_hash, role FROM admins WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) return null;

            String storedHash = rs.getString("password_hash");
            String role = rs.getString("role");

            if (hashPassword(password).equals(storedHash)) {
                return role;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Creates a new admin (only allowed for super_admin)
    public static boolean createAdmin(String currentUserRole, String username, String password, String role) {

        if (!currentUserRole.equals("super_admin")) {
            System.out.println("Access Denied");
            return false;
        }

        String sql = "INSERT INTO admins (username, password_hash, role) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, hashPassword(password));
            pstmt.setString(3, role);

            pstmt.executeUpdate();
            System.out.println("Admin created");
            return true;

        } catch (SQLException e) {
            System.out.println("Username already exists "+e.getMessage());
            return false;
        }
    }

    // Simple hashing (can be improved later)
    private static String hashPassword(String password) {
        return Integer.toString(password.hashCode());
    }

    // Creates all tables and default data
    public static void initializeDatabase() {

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            // Admin table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS admins (
                    admin_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password_hash TEXT NOT NULL,
                    role TEXT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
            """);

            // Incident table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS incident_reports (
                    report_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    city TEXT,
                    description TEXT,
                    incident_date TEXT,
                    report_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    admin_id INTEGER,
                    is_anonymous INTEGER,
                    status TEXT,
                    consent INTEGER,
                    reviewed_at TIMESTAMP
                );
            """);

            // Resources table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS resources (
                    resource_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    type TEXT,
                    step_number INTEGER,
                    content TEXT
                );
            """);

            // Default admin
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as cnt FROM admins");
            if (rs.next() && rs.getInt("cnt") == 0) {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO admins (username, password_hash, role) VALUES (?, ?, ?)");
                ps.setString(1, "HeadAdmin");
                ps.setString(2, hashPassword("Admin@123"));
                ps.setString(3, "super_admin");
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}