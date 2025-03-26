package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class DatabaseConnection {
    private static String URL = "jdbc:mysql://localhost:3306/";
    private static String USER = "root";
    private static String PASSWORD = "";
    private static final String DB_NAME = "apex_turf";
    private static boolean isInitialized = false;

    public static void initialize() throws SQLException {
        if (isInitialized) return;

        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=== Database Connection Setup ===");
        System.out.print("Enter MySQL host [localhost]: ");
        String host = scanner.nextLine();
        if (host.isEmpty()) host = "localhost";

        System.out.print("Enter MySQL port [3306]: ");
        String port = scanner.nextLine();
        if (port.isEmpty()) port = "3306";

        System.out.print("Enter MySQL username [root]: ");
        USER = scanner.nextLine();
        if (USER.isEmpty()) USER = "root";

        System.out.print("Enter MySQL password: ");
        PASSWORD = scanner.nextLine();

        URL = "jdbc:mysql://" + host + ":" + port + "/";
        isInitialized = true;

        try (Connection conn = getRootConnection()) {
            System.out.println("✅ Database connection successful!");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL + DB_NAME, USER, PASSWORD);
    }

    public static Connection getRootConnection() throws SQLException {  // Changed to public
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}