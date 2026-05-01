
// (SID: 2506288) / (Team: Kafka)
// Main application entry point
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Initialize database
        Admin.initializeDatabase();

        while (true) {

            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. Admin Login");
            System.out.println("2. Report an incident");
            System.out.println("3. City Spiking ratings");
            System.out.println("4. View resources");
            System.out.println("5. View recent incident stories");
            System.out.println("6. Exit");
            System.out.print("Choose option: ");

            int mainChoice = scanner.nextInt();
            scanner.nextLine();

            switch (mainChoice) {

                case 1:
                    handleAdminLogin(scanner);
                    break;

                case 2:
                    handleGuestReport(scanner);
                    break;

                case 3:
                    IncidentService.citySafetyReport();
                    System.out.println("\nPress Enter to return...");
                    scanner.nextLine();
                    break;

                case 4:
                    showResources(scanner);
                    System.out.println("\nPress Enter to return...");
                    scanner.nextLine();
                    break;

                case 5:
                    IncidentService.viewPublicStories();
                    System.out.println("\nPress Enter to return...");
                    scanner.nextLine();
                    break;

                case 6:
                    System.out.println("Goodbye!");
                    return;

                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    private static void handleAdminLogin(Scanner scanner) {

        System.out.println("\n=== Admin Login ===");

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        String role = Admin.login(username, password);

        if (role == null) {
            System.out.println("Login failed.");
            return;
        }

        adminMenu(scanner, role);
    }

    private static void adminMenu(Scanner scanner, String role) {

        while (true) {

            System.out.println("\n=== ADMIN MENU (" + role + ") ===");

            if (role.equals("super_admin")) {
                System.out.println("1. Create Admin");
            }

            if (role.equals("reviewer") || role.equals("super_admin")) {
                System.out.println("2. View Incidents");
                System.out.println("3. Review Incidents");
            }

            System.out.println("4. City Risk Report");
            System.out.println("5. Manage Resources");
            System.out.println("6. Logout");

            System.out.print("Choose option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {

                case 1:
                    if (!role.equals("super_admin")) {
                        System.out.println("Access Denied");
                        System.out.println("\nPress Enter to return...");
                        scanner.nextLine();
                        break;
                    }

                    System.out.print("Enter username: ");
                    String newUser = scanner.nextLine();

                    System.out.print("Enter password: ");
                    String newPass = scanner.nextLine();

                    System.out.print("Enter role(super_admin, moderator, reviewer): ");
                    String newRole = scanner.nextLine();

                    Admin.createAdmin(role, newUser, newPass, newRole);
                    System.out.println("\nPress Enter to return...");
                    scanner.nextLine();
                    break;

                case 2:
                    if (!(role.equals("reviewer") || role.equals("super_admin"))) {
                        System.out.println("Access Denied");
                        System.out.println("\nPress Enter to return...");
                        scanner.nextLine();
                        break;
                    }
                    IncidentService.viewIncidents();
                    System.out.println("\nPress Enter to return...");
                    scanner.nextLine();
                    break;

                case 3:
                    if (!(role.equals("reviewer") || role.equals("super_admin"))) {
                        System.out.println("Access Denied");
                        System.out.println("\nPress Enter to return...");
                        scanner.nextLine();
                        break;
                    }
                    handleReview(scanner);
                    break;

                case 4:
                    IncidentService.citySafetyReport();
                    System.out.println("\nPress Enter to return...");
                    scanner.nextLine();
                    break;

                case 5:
                    if (!(role.equals("moderator") || role.equals("super_admin"))) {
                        System.out.println("Access Denied");

                        break;
                    }
                    manageResources(scanner);
                    System.out.println("\nPress Enter to return...");
                    scanner.nextLine();
                    break;

                case 6:
                    return;

                default:
                    System.out.println("Invalid choice");
            }
        }
    }



    private static void handleGuestReport(Scanner scanner) {

        System.out.println("\n=== Report Incident ===");

        // ===== CITY VALIDATION =====
        String city;
        while (true) {
            System.out.print("Enter city: ");
            city = scanner.nextLine();

            if (city.trim().isEmpty()) {
                System.out.println("City cannot be empty.");
                continue;
            }

            if (!city.matches("[a-zA-Z ]+")) {
                System.out.println("City must contain only letters.");
                continue;
            }

            // Normalize (London, Manchester, etc.)
            city = city.substring(0,1).toUpperCase() + city.substring(1).toLowerCase();
            break;
        }

        // ===== DATE VALIDATION =====
        String date;
        while (true) {
            System.out.print("Enter date (YYYY-MM-DD): ");
            date = scanner.nextLine();

            try {
                LocalDate parsedDate = LocalDate.parse(date);

                if (parsedDate.isAfter(LocalDate.now())) {
                    System.out.println("Error: Date cannot be in the future.");
                    continue;
                }

                break;

            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Use YYYY-MM-DD.");
            }
        }

        System.out.print("Do you consent to share this story publicly? (y/n): ");
        String consentInput = scanner.nextLine();

        boolean consent = consentInput.equalsIgnoreCase("y");

        // ===== DESCRIPTION =====
        String description;
        while (true) {
            System.out.print("Enter description (Your Story) : ");
            description = scanner.nextLine();

            if (description.trim().isEmpty()) {
                System.out.println("Description cannot be empty.");
                continue;
            }

            break;
        }

        // ===== SAVE =====
        IncidentService.reportIncident(city, description, date, null, true, consent);
    }

    private static void handleReview(Scanner scanner) {

        IncidentManager.viewPendingIncidents();

        System.out.print("Enter report ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Edit city (leave blank to keep): ");
        String city = scanner.nextLine();

        System.out.print("Edit description (leave blank to keep): ");
        String desc = scanner.nextLine();

        System.out.print("Approve or Reject (a/r): ");
        String decision = scanner.nextLine();

        IncidentService.reviewIncident(id, decision, city, desc);
    }

    private static void manageResources(Scanner scanner) {

        while (true) {

            System.out.println("\n=== MANAGE RESOURCES ===");
            System.out.println("1. View Response Guide");
            System.out.println("2. View Prevention Guide");
            System.out.println("3. Add Step");
            System.out.println("4. Edit Step");
            System.out.println("5. Delete Step");
            System.out.println("6. Back");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {

                case 1:
                    ResourceService.viewResources("response");
                    break;

                case 2:
                    ResourceService.viewResources("prevention");
                    break;

                case 3:
                    System.out.print("Type (response/prevention): ");
                    String type = scanner.nextLine();

                    System.out.print("Enter step: ");
                    String content = scanner.nextLine();

                    ResourceService.addResource(type, content);
                    break;

                case 4:
                    System.out.print("Enter step ID: ");
                    int editId = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("New content: ");
                    String newContent = scanner.nextLine();

                    ResourceService.updateResource(editId, newContent);
                    break;

                case 5:
                    System.out.print("Enter step ID: ");
                    int deleteId = scanner.nextInt();
                    scanner.nextLine();

                    ResourceService.deleteResource(deleteId);
                    break;

                case 6:
                    return;
            }
        }
    }

    private static void showResources(Scanner scanner) {

        while (true) {

            System.out.println("\n=== RESOURCES ===");
            System.out.println("1. Response Guide");
            System.out.println("2. Prevention Guide");
            System.out.println("3. Back");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    ResourceService.viewResources("response");
                    break;

                case 2:
                    ResourceService.viewResources("prevention");
                    break;

                case 3:
                    return;
            }
        }
    }
}