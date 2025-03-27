package Database;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class DatabaseSetup {
    private static final int BATCH_SIZE = 1000; // Optimal for most databases

    public static void initializeDatabase() throws SQLException {
        try (Connection conn = DatabaseConnection.getRootConnection()) {
            // Check if database exists
            if (!databaseExists(conn)) {
                createDatabaseAndTables(conn);
                insertSampleData(conn);
                System.out.println("✅ Database initialized with sample data!");
            }
        }
    }

    private static boolean databaseExists(Connection conn) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getCatalogs()) {
            while (rs.next()) {
                if ("apex_turf".equalsIgnoreCase(rs.getString(1))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void createDatabaseAndTables(Connection conn) throws SQLException {
        String[] ddlScript = {
                // Database creation
                "CREATE DATABASE IF NOT EXISTS apex_turf",
                "USE apex_turf",

                // Users table
                "CREATE TABLE IF NOT EXISTS Users (" +
                        "user_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                        "user_name VARCHAR(50) NOT NULL UNIQUE," +
                        "password VARCHAR(100) NOT NULL," +
                        "user_type ENUM('client','admin') NOT NULL)",

                // Client table
                "CREATE TABLE IF NOT EXISTS Client (" +
                        "client_id INT NOT NULL PRIMARY KEY," +
                        "user_name VARCHAR(50) NOT NULL," +
                        "password VARCHAR(100) NOT NULL," +
                        "client_email VARCHAR(100) NOT NULL," +
                        "contact_info VARCHAR(15) NOT NULL," +
                        "FOREIGN KEY (client_id) REFERENCES Users(user_id))",

                // Admin table
                "CREATE TABLE IF NOT EXISTS Admin (" +
                        "admin_id INT NOT NULL PRIMARY KEY," +
                        "user_name VARCHAR(50) NOT NULL," +
                        "password VARCHAR(100) NOT NULL," +
                        "contact_info VARCHAR(15) NOT NULL," +
                        "FOREIGN KEY (admin_id) REFERENCES Users(user_id))",

                // Turf table
                "CREATE TABLE IF NOT EXISTS Turf (" +
                        "turf_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                        "turf_name VARCHAR(50) NOT NULL," +
                        "turf_type VARCHAR(20) NOT NULL," +
                        "location VARCHAR(100) NOT NULL," +
                        "feeperhour DECIMAL(10,2) NOT NULL," +
                        "owner_id INT NOT NULL," +
                        "FOREIGN KEY (owner_id) REFERENCES Admin(admin_id))",

                // TimeSlots table
                "CREATE TABLE IF NOT EXISTS TimeSlots (" +
                        "slot_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                        "turf_id INT NOT NULL," +
                        "slot_time VARCHAR(50) NOT NULL," +
                        "is_available BOOLEAN NOT NULL DEFAULT TRUE," +
                        "FOREIGN KEY (turf_id) REFERENCES Turf(turf_id))",

                // Booking table
                "CREATE TABLE IF NOT EXISTS Booking (" +
                        "booking_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                        "client_id INT NOT NULL," +
                        "turf_id INT NOT NULL," +
                        "slot_id INT NOT NULL," +
                        "booking_date DATE NOT NULL," +
                        "FOREIGN KEY (client_id) REFERENCES Client(client_id)," +
                        "FOREIGN KEY (turf_id) REFERENCES Turf(turf_id)," +
                        "FOREIGN KEY (slot_id) REFERENCES TimeSlots(slot_id))",

                // Payment table
                "CREATE TABLE IF NOT EXISTS Payment (" +
                        "payment_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                        "booking_id INT NOT NULL UNIQUE," +
                        "amount DECIMAL(10,2) NOT NULL," +
                        "payment_mode VARCHAR(20) NOT NULL," +
                        "payment_status VARCHAR(20) NOT NULL," +
                        "payment_date DATE," +
                        "FOREIGN KEY (booking_id) REFERENCES Booking(booking_id))",

                // Reviews table
                "CREATE TABLE IF NOT EXISTS Reviews (" +
                        "review_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                        "booking_id INT NOT NULL," +
                        "client_id INT NOT NULL," +
                        "turf_id INT NOT NULL," +
                        "review_text TEXT," +
                        "stars INT NOT NULL CHECK (stars BETWEEN 1 AND 5)," +
                        "review_date DATE NOT NULL," +
                        "FOREIGN KEY (booking_id) REFERENCES Booking(booking_id)," +
                        "FOREIGN KEY (client_id) REFERENCES Client(client_id)," +
                        "FOREIGN KEY (turf_id) REFERENCES Turf(turf_id))"
        };

        try (Statement stmt = conn.createStatement()) {
            for (String sql : ddlScript) {
                stmt.execute(sql);
            }
        }
    }

    private static void insertSampleData(Connection conn) throws SQLException {
        // Batch 1: Users and admin
        executeBatch(conn,
                "INSERT IGNORE INTO Users (user_id, user_name, password, user_type) VALUES (?, ?, ?, ?)",
                Arrays.asList(
                        new Object[]{1, "admin", "admin123", "admin"},
                        new Object[]{2, "client1", "client123", "client"},
                        new Object[]{3, "client2", "client123", "client"}
                )
        );

        executeBatch(conn,
                "INSERT IGNORE INTO Admin (admin_id, user_name, password, contact_info) VALUES (?, ?, ?, ?)",
                Arrays.asList(
                        new Object[][]{ // Note Object[][] instead of Object[]
                                {1, "admin", "admin123", "0000000000"}
                        }
                )
        );

        // Batch 2: Sample turfs
        executeBatch(conn,
                "INSERT IGNORE INTO Turf (turf_id, turf_name, turf_type, location, feeperhour, owner_id) VALUES (?, ?, ?, ?, ?, ?)",
                Arrays.asList(
                        new Object[]{1, "Football Arena", "Football", "North Campus", 1500.00, 1},
                        new Object[]{2, "Cricket Ground", "Cricket", "South Campus", 2000.00, 1},
                        new Object[]{3, "Tennis Court", "Tennis", "East Campus", 1200.00, 1}
                )
        );

        // Batch 3: Time slots (processed in chunks)
        List<Object[]> timeSlots = Arrays.asList(
                new Object[]{1, 1, "09:00 AM - 11:00 AM", true},
                new Object[]{2, 1, "11:00 AM - 01:00 PM", true},
                new Object[]{3, 1, "03:00 PM - 05:00 PM", true},
                new Object[]{4, 2, "09:00 AM - 11:00 AM", true},
                new Object[]{5, 2, "11:00 AM - 01:00 PM", true},
                new Object[]{6, 2, "03:00 PM - 05:00 PM", true},
                new Object[]{7, 3, "09:00 AM - 11:00 AM", true},
                new Object[]{8, 3, "11:00 AM - 01:00 PM", true},
                new Object[]{9, 3, "03:00 PM - 05:00 PM", true}
        );

        executeBatchInChunks(conn,
                "INSERT IGNORE INTO TimeSlots (slot_id, turf_id, slot_time, is_available) VALUES (?, ?, ?, ?)",
                timeSlots
        );
    }

    private static void executeBatch(Connection conn, String sql, List<Object[]> paramsList)
            throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Object[] params : paramsList) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private static void executeBatchInChunks(Connection conn, String sql, List<Object[]> paramsList)
            throws SQLException {
        for (int i = 0; i < paramsList.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, paramsList.size());
            List<Object[]> chunk = paramsList.subList(i, end);
            executeBatch(conn, sql, chunk);
        }
    }
}