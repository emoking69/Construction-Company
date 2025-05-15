import java.util.*;
import  java.sql.*;

public class Director extends RoleMenu{
    static Scanner scanner = new Scanner(System.in);
    private static final String DB_URL = "jdbc:sqlite:construction_company.db";
    public void showMenu(){
        while (true){
            System.out.println("1. Show coverage");
            System.out.println("2. Show budget types");
            System.out.println("3. Show spent budget on marketing");
            System.out.println("4. Show current marketing budget");
            System.out.println("5. Show total salaries budget");
            System.out.println("6. Raise salary");
            System.out.println("7. Lower salary");
            System.out.println("8. Show available equipment");
            System.out.println("9. Exit");
            int option = scanner.nextInt();

            if (option == 1){
                showCoverageByRegion();
            } else if (option == 2){
                System.out.println("Marketing\nSalaries");
            } else if (option == 3){
                while(true){
                    System.out.println("\n--- Choose platform ---\n1. Facebook\n2. Instagram\n3. YouTube\n4. Telegram\n0. Back");
                    int choice = scanner.nextInt();
                    if (choice == 0) break;
                    Marketing.showBudgetSpent(choice);
                }
            } else if (option == 4){
                System.out.println(Marketing.getMarketingBudget());
            } else if (option == 5){
                showTotalSalaryBudget();
            } else if (option == 6){
                increaseSalary();
            } else if (option == 7){
                decreaseSalary();
            } else if (option == 8){
                String equipment = """
                                 Bulldozer
                                 Snowcat
                                 Snowplow
                                 Skidder
                                 Tractor
                                 Track tractor
                                 Locomotive
                                 Artillery tractor
                                 Crawler-transporter
                        """;
                System.out.println(equipment);
            } else if (option == 9){
                break;
            } else {
                System.out.println("Invalid option");
            }
        }
    }
    private static void showTotalSalaryBudget() {
        String sql = "SELECT SUM(salary) AS total_salary FROM users";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                double total = rs.getDouble("total_salary");
                System.out.printf("💼 Total salary budget: %.2f\n", total);
            } else {
                System.out.println("No salary data found.");
            }

        } catch (SQLException e) {
            System.out.println("Error calculating total salary: " + e.getMessage());
        }
    }
    private static void increaseSalary() {
        scanner.nextLine();

        System.out.print("Enter username to increase salary: ");
        String username = scanner.nextLine();

        System.out.print("Enter amount to increase (e.g., 2000): ");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine());
            if (amount <= 0) {
                System.out.println("❌ Amount must be positive.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid amount.");
            return;
        }

        String getSalarySQL = "SELECT salary FROM users WHERE username = ?";
        String updateSalarySQL = "UPDATE users SET salary = ? WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement getStmt = conn.prepareStatement(getSalarySQL);
             PreparedStatement updateStmt = conn.prepareStatement(updateSalarySQL)) {

            getStmt.setString(1, username);
            ResultSet rs = getStmt.executeQuery();

            if (rs.next()) {
                double currentSalary = rs.getDouble("salary");
                double newSalary = currentSalary + amount;

                updateStmt.setDouble(1, newSalary);
                updateStmt.setString(2, username);
                updateStmt.executeUpdate();

                System.out.printf("✅ Salary increased from %.2f to %.2f%n", currentSalary, newSalary);
            } else {
                System.out.println("❌ User not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error updating salary: " + e.getMessage());
        }
    }
    private static void decreaseSalary() {
        scanner.nextLine();

        System.out.print("Enter username to decrease salary: ");
        String username = scanner.nextLine();

        System.out.print("Enter amount to decrease (e.g., 1500): ");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine());
            if (amount <= 0) {
                System.out.println("❌ Amount must be positive.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid amount.");
            return;
        }

        String getSalarySQL = "SELECT salary FROM users WHERE username = ?";
        String updateSalarySQL = "UPDATE users SET salary = ? WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement getStmt = conn.prepareStatement(getSalarySQL);
             PreparedStatement updateStmt = conn.prepareStatement(updateSalarySQL)) {

            getStmt.setString(1, username);
            ResultSet rs = getStmt.executeQuery();

            if (rs.next()) {
                double currentSalary = rs.getDouble("salary");
                double newSalary = currentSalary - amount;

                if (newSalary < 0) {
                    System.out.println("❌ Salary cannot go below 0.");
                    return;
                }

                updateStmt.setDouble(1, newSalary);
                updateStmt.setString(2, username);
                updateStmt.executeUpdate();

                System.out.printf("✅ Salary decreased from %.2f to %.2f%n", currentSalary, newSalary);
            } else {
                System.out.println("❌ User not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error updating salary: " + e.getMessage());
        }
    }
}
