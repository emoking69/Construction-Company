import java.sql.*;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    private static final String DB_URL = "jdbc:sqlite:construction_company.db";
    private static final String[] ALLOWED_ROLES = {
            "manager", "worker", "director", "sales manager", "marketing"
    };
    private static String currentRole = null;
    public static String currentUser = null;
    public static int loggedInUserId = -1;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        createUsersTable();

            System.out.println("\n1. Register");
            System.out.println("2. Login");
            System.out.println("0. Exit");
            System.out.print("Choose option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if (option == 1) {
                System.out.print("Enter username: ");
                String username = scanner.nextLine();

                System.out.print("Enter password: ");
                String password = scanner.nextLine();

                // Step 2: Validate role input
                String role;
                while (true) {
                    System.out.print("Enter role  (Manager\\Worker\\Director\\Sales manager\\Marketing): ");
                    role = scanner.nextLine().toLowerCase();

                    boolean valid = false;
                    for (String allowed : ALLOWED_ROLES) {
                        if (allowed.equals(role)) {
                            valid = true;
                            break;
                        }
                    }

                    if (valid) break;
                    System.out.println("Invalid role. Try again.");
                }

                // Register with validated role
                registerUser(username, password, role);
            }
            else if (option == 2) {
                System.out.print("Enter your account type: ");
                String acc_type = scanner.nextLine();
                if(Arrays.toString(ALLOWED_ROLES).contains(acc_type)){
                        System.out.print("Enter username: ");
                        String username = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String password = scanner.nextLine();
                        loginUser(username, password);

                        RoleMenu menu = Menus.getMenu(currentRole);
                        if (menu != null) {
                        menu.showMenu();
                        } else {
                            System.out.println("Unknown role. Exiting.");
                        }
                } else {
                        System.out.println("Wrong account type. Try again!");
                        return;
                }


            } else if (option == 0) {
                return;
            }

        scanner.close();
    }

     private static void createUsersTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL" +
                ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    private static void registerUser(String username, String password, String role) {
        String sql = "INSERT INTO users(username, password, role) VALUES(?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role.toLowerCase());
            pstmt.executeUpdate();

            System.out.println("User registered successfully with role: " + role);
        } catch (SQLException e) {
            System.out.println("Registration error: " + e.getMessage());
        }
    }

    private static void loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
                currentUser = username;

            if (rs.next()) {
                String role = rs.getString("role");
                currentRole = role;
                loggedInUserId = rs.getInt("id");
                System.out.println("Login successful. Welcome " + username + " (" + role + ")");
            } else {
                System.out.println("Invalid login.");
            }
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }
    }
}


/*
1 connect database to main class
2 make user login password system
3 make the first check acc_type eg manager worker director...
4 after successful sign in open menu with only exit function (for now)
5 based on chosen role open menu
6 write menus logics. start with manager first 


? clean code

coverage option used by marketing doesnt change percentage 
*/