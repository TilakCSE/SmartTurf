package Database;

import java.sql.*;
import java.util.Scanner;

public class DatabaseConnection {
    private static String URL = "jdbc:mysql://localhost:3306/";
    private static String USER = null;
    private static String PASSWORD = null;
    private static final String DB_NAME = "apex_turf";
    private static boolean credentialsSet = false;

    public static void initialize() throws SQLException {
        if (!credentialsSet) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter MySQL username [root]: ");
            USER = scanner.nextLine();
            if (USER.isEmpty()) USER = "root";

            System.out.print("Enter MySQL password: ");
            PASSWORD = scanner.nextLine();

            credentialsSet = true;
        }

        // Test connection
        try (Connection testConn = getRootConnection()) {
            System.out.println("✅ Database connection successful!");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL + DB_NAME, USER, PASSWORD);
    }

    public static Connection getRootConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}