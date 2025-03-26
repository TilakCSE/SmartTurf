package Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {
    public static void initializeDatabase() throws SQLException {
        try (Connection conn = DatabaseConnection.getRootConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS apex_turf");
            stmt.executeUpdate("USE apex_turf");

            String[] ddlStatements = {
                    // Users table
                    "CREATE TABLE IF NOT EXISTS Users (" +
                            "user_id INT NOT NULL PRIMARY KEY," +
                            "user_name VARCHAR(50) NOT NULL," +
                            "password VARCHAR(100) NOT NULL," +
                            "user_type ENUM('client','admin') NOT NULL)",

                    // Client table
                    "CREATE TABLE IF NOT EXISTS Client (" +
                            "client_id INT NOT NULL PRIMARY KEY," +
                            "user_name VARCHAR(50) NOT NULL," +
                            "password VARCHAR(100) NOT NULL," +
                            "client_email VARCHAR(100) NOT NULL," +
                            "contact_info VARCHAR(15) NOT NULL," +
                            "CONSTRAINT fk_client_user FOREIGN KEY (client_id) REFERENCES Users(user_id))",

                    // Admin table
                    "CREATE TABLE IF NOT EXISTS Admin (" +
                            "admin_id INT NOT NULL PRIMARY KEY," +
                            "user_name VARCHAR(50) NOT NULL," +
                            "password VARCHAR(100) NOT NULL," +
                            "contact_info VARCHAR(15) NOT NULL," +
                            "CONSTRAINT fk_admin_user FOREIGN KEY (admin_id) REFERENCES Users(user_id))",

                    // Turf table
                    "CREATE TABLE IF NOT EXISTS Turf (" +
                            "turf_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                            "turf_name VARCHAR(50) NOT NULL," +
                            "turf_type VARCHAR(20) NOT NULL," +
                            "location VARCHAR(100) NOT NULL," +
                            "feeperhour DECIMAL(10,2) NOT NULL," +
                            "owner_id INT NOT NULL," +
                            "CONSTRAINT fk_turf_admin FOREIGN KEY (owner_id) REFERENCES Admin(admin_id))",

                    // TimeSlots table
                    "CREATE TABLE IF NOT EXISTS TimeSlots (" +
                            "slot_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                            "turf_id INT NOT NULL," +
                            "slot_time DATETIME NOT NULL," +
                            "is_available BOOLEAN NOT NULL DEFAULT TRUE," +
                            "CONSTRAINT fk_slot_turf FOREIGN KEY (turf_id) REFERENCES Turf(turf_id))",

                    // Booking table
                    "CREATE TABLE IF NOT EXISTS Booking (" +
                            "booking_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                            "client_id INT NOT NULL," +
                            "turf_id INT NOT NULL," +
                            "slot_id INT NOT NULL," +
                            "booking_date DATE NOT NULL," +
                            "CONSTRAINT fk_booking_client FOREIGN KEY (client_id) REFERENCES Client(client_id)," +
                            "CONSTRAINT fk_booking_turf FOREIGN KEY (turf_id) REFERENCES Turf(turf_id)," +
                            "CONSTRAINT fk_booking_slot FOREIGN KEY (slot_id) REFERENCES TimeSlots(slot_id))",

                    // Payment table
                    "CREATE TABLE IF NOT EXISTS Payment (" +
                            "payment_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                            "booking_id INT NOT NULL UNIQUE," +
                            "amount DECIMAL(10,2) NOT NULL," +
                            "payment_mode VARCHAR(20) NOT NULL," +
                            "payment_date DATE NOT NULL," +
                            "payment_status VARCHAR(20) NOT NULL," +
                            "CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES Booking(booking_id))",

                    // Reviews table
                    "CREATE TABLE IF NOT EXISTS Reviews (" +
                            "review_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                            "booking_id INT NOT NULL," +
                            "client_id INT NOT NULL," +
                            "turf_id INT NOT NULL," +
                            "review_text TEXT," +
                            "stars INT NOT NULL CHECK (stars BETWEEN 1 AND 5)," +
                            "review_date DATE NOT NULL," +
                            "CONSTRAINT fk_review_booking FOREIGN KEY (booking_id) REFERENCES Booking(booking_id)," +
                            "CONSTRAINT fk_review_client FOREIGN KEY (client_id) REFERENCES Client(client_id)," +
                            "CONSTRAINT fk_review_turf FOREIGN KEY (turf_id) REFERENCES Turf(turf_id))"
            };

            for (String ddl : ddlStatements) {
                stmt.executeUpdate(ddl);
            }

            // Insert default admin
            stmt.executeUpdate("INSERT IGNORE INTO Users VALUES (1, 'admin', 'admin123', 'admin')");
            stmt.executeUpdate("INSERT IGNORE INTO Admin VALUES (1, 'admin', 'admin123', '0000000000')");

            System.out.println("✅ Database tables created successfully!");
        }
    }
}