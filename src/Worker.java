import java.util.*;
import java.sql.*;

public class Worker extends RoleMenu{
    static Scanner scanner = new Scanner(System.in);
    private static final String DB_URL = "jdbc:sqlite:construction_company.db";
    public void showMenu(){
        while(true) {
            System.out.println("1. Show all tasks");
            System.out.println("2. Finish task");
            System.out.println("3. Show active tasks");
            System.out.println("4. Show salary");
            System.out.println("5. Exit");
            int option = scanner.nextInt();

            if(option == 1){
                viewMyTasks(Main.currentUser);
            } else if (option == 2) {
                markTaskAsFinished();
            } else if (option == 3) {
                viewPActiveTasks(Main.currentUser);
            } else if (option == 4) {
                viewWorkerSalary();
            } else if (option == 5) {
                break;
            } else {
                System.out.println("Invalid option");
            }
        }
    }

    private static void viewMyTasks(String username) {
        scanner.nextLine();
        String getTasksSQL = "SELECT task, status FROM tasks WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement getTasksStmt = conn.prepareStatement(getTasksSQL)) {

            getTasksStmt.setString(1, username);
            ResultSet taskRs = getTasksStmt.executeQuery();

            System.out.println("\n📝 Your Tasks:");
            boolean hasTasks = false;
            while (taskRs.next()) {
                String task = taskRs.getString("task");
                String status = taskRs.getString("status");
                System.out.println("- " + task + " [" + status + "]");
                hasTasks = true;
            }

            if (!hasTasks) {
                System.out.println("📭 You have no tasks assigned.");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void markTaskAsFinished() {
        String getTasksSQL = "SELECT id, task, status FROM tasks WHERE username = ?";
        String updateTaskSQL = "UPDATE tasks SET status = 'finished' WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement getTasksStmt = conn.prepareStatement(getTasksSQL)) {

            getTasksStmt.setString(1, Main.currentUser);
            ResultSet rs = getTasksStmt.executeQuery();

            List<Integer> taskIds = new ArrayList<>();
            System.out.println("\n📋 Your tasks:");
            int index = 1;
            while (rs.next()) {
                int taskId = rs.getInt("id");
                String task = rs.getString("task");
                String status = rs.getString("status");
                System.out.println(index + ". " + task + " [" + status + "]");
                taskIds.add(taskId);
                index++;
            }

            if (taskIds.isEmpty()) {
                System.out.println("📭 No tasks assigned.");
                return;
            }

            System.out.print("Enter the number of the task to mark as finished: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if (choice < 1 || choice > taskIds.size()) {
                System.out.println("❌ Invalid choice.");
                return;
            }

            int selectedTaskId = taskIds.get(choice - 1);

            try (PreparedStatement updateStmt = conn.prepareStatement(updateTaskSQL)) {
                updateStmt.setInt(1, selectedTaskId);
                updateStmt.executeUpdate();
                System.out.println("✅ Task marked as finished!");
            }

        } catch (SQLException e) {
            System.out.println("Error updating task: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("❌ Please enter a valid number.");
            scanner.nextLine(); // clear invalid input
        }
    }
    private static void viewPActiveTasks(String username) {
        String getTasksSQL = "SELECT task FROM tasks WHERE username = ? AND status = 'assigned'";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement getTasksStmt = conn.prepareStatement(getTasksSQL)) {

            getTasksStmt.setString(1, username);
            ResultSet taskRs = getTasksStmt.executeQuery();

            System.out.println("\n📌 Your Active Tasks:");
            boolean hasTasks = false;
            while (taskRs.next()) {
                String task = taskRs.getString("task");
                System.out.println("- " + task);
                hasTasks = true;
            }

            if (!hasTasks) {
                System.out.println("✅ You have no active tasks!");
            }

        } catch (SQLException e) {
            System.out.println("Error fetching active tasks: " + e.getMessage());
        }
    }
    private static void viewWorkerSalary() {
        scanner.nextLine(); // clear leftover newline


        String username = Main.currentUser;

        String sql = "SELECT role, salary FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                double salary = rs.getDouble("salary");

                if (!role.equalsIgnoreCase("worker")) {
                    System.out.println("❌ That user is not a worker.");
                    return;
                }

                System.out.printf("💰 your salary: %.2f\n", salary);
            } else {
                System.out.println("❌ User not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving salary: " + e.getMessage());
        }
    }
}
