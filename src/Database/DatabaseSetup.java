package Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {
    public static void initializeDatabase() throws SQLException {
        try (Connection conn = DatabaseConnection.getRootConnection();
             Statement stmt = conn.createStatement()) {

            // 1. Database and tables creation
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
                            "FOREIGN KEY (turf_id) REFERENCES Turf(turf_id))",

                    // Insert default admin
                    "INSERT IGNORE INTO Users (user_id, user_name, password, user_type) " +
                            "VALUES (1, 'admin', 'admin123', 'admin')",

                    "INSERT IGNORE INTO Admin (admin_id, user_name, password, contact_info) " +
                            "VALUES (1, 'admin', 'admin123', '0000000000')",

                    // Insert sample turfs
                    "INSERT IGNORE INTO Turf (turf_id, turf_name, turf_type, location, feeperhour, owner_id) " +
                            "VALUES " +
                            "(1, 'Football Arena', 'Football', 'North Campus', 1500.00, 1)," +
                            "(2, 'Cricket Ground', 'Cricket', 'South Campus', 2000.00, 1)," +
                            "(3, 'Tennis Court', 'Tennis', 'East Campus', 1200.00, 1)",

                    // Insert sample timeslots
                    "INSERT IGNORE INTO TimeSlots (slot_id, turf_id, slot_time, is_available) " +
                            "VALUES " +
                            "(1, 1, '09:00 AM - 11:00 AM', TRUE)," +
                            "(2, 1, '11:00 AM - 01:00 PM', TRUE)," +
                            "(3, 1, '03:00 PM - 05:00 PM', TRUE)," +
                            "(4, 2, '09:00 AM - 11:00 AM', TRUE)," +
                            "(5, 2, '11:00 AM - 01:00 PM', TRUE)," +
                            "(6, 2, '03:00 PM - 05:00 PM', TRUE)," +
                            "(7, 3, '09:00 AM - 11:00 AM', TRUE)," +
                            "(8, 3, '11:00 AM - 01:00 PM', TRUE)," +
                            "(9, 3, '03:00 PM - 05:00 PM', TRUE)"
            };

            // Execute all statements
            for (String sql : ddlScript) {
                stmt.executeUpdate(sql);
            }

            System.out.println("✅ Database initialized with sample data!");

        } catch (SQLException e) {
            System.err.println("❌ Database initialization failed: " + e.getMessage());
            throw e;
        }
    }
}