package Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TurfDB {
    private int turfId;
    private String turfName;
    private String turfType;
    private String location;
    private double feePerHour;
    private int ownerId;


    public TurfDB(int turfId, String turfName, String turfType, String location, double feePerHour, int ownerId) {
        this.turfId = turfId;
        this.turfName = turfName;
        this.turfType = turfType;
        this.location = location;
        this.feePerHour = feePerHour;
        this.ownerId = ownerId;
    }

    // Getters only (no setters needed for DB entity)
    public int getTurfId() { return turfId; }
    public String getTurfName() { return turfName; }
    public String getTurfType() { return turfType; }
    public String getLocation() { return location; }
    public double getFeePerHour() { return feePerHour; }
    public int getOwnerId() { return ownerId; }

    // Essential methods only
    public static TurfDB getTurfById(int turfId) throws SQLException {
        String sql = "SELECT * FROM Turf WHERE turf_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, turfId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new TurfDB(
                        rs.getInt("turf_id"),
                        rs.getString("turf_name"),
                        rs.getString("turf_type"),
                        rs.getString("location"),
                        rs.getDouble("feeperhour"),
                        rs.getInt("owner_id")
                );
            }
        }
        return null;
    }

    public static List<TurfDB> getAllTurfs() throws SQLException {
        List<TurfDB> turfs = new ArrayList<>();
        String sql = "SELECT * FROM Turf";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                turfs.add(new TurfDB(
                        rs.getInt("turf_id"),
                        rs.getString("turf_name"),
                        rs.getString("turf_type"),
                        rs.getString("location"),
                        rs.getDouble("feeperhour"),
                        rs.getInt("owner_id")
                ));
            }
        }
        return turfs;
    }

    public static boolean isTurfAvailable(int turfId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM TimeSlots WHERE turf_id = ? AND is_available = TRUE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, turfId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }
    // Add these new methods
    public static int addTurf(String turfName, String turfType, String location,
                              double feePerHour, int ownerId) throws SQLException {
        String sql = "INSERT INTO Turf (turf_name, turf_type, location, feeperhour, owner_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, turfName);
            stmt.setString(2, turfType);
            stmt.setString(3, location);
            stmt.setDouble(4, feePerHour);
            stmt.setInt(5, ownerId);
            stmt.executeUpdate();

            // Retrieve the auto-generated ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1; // Return -1 if insertion failed
    }

    public static boolean turfExists(int turfId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Turf WHERE turf_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, turfId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }


    public static void deleteTurf(int turfId) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. First delete all dependent bookings and related records
            deleteDependentRecords(conn, turfId);

            // 2. Then delete the turf
            String deleteTurfSql = "DELETE FROM Turf WHERE turf_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteTurfSql)) {
                stmt.setInt(1, turfId);
                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Turf not found");
                }
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }

    private static void deleteDependentRecords(Connection conn, int turfId) throws SQLException {
        // 1. Delete reviews for this turf's bookings
        String deleteReviewsSql = "DELETE r FROM Reviews r " +
                "JOIN Booking b ON r.booking_id = b.booking_id " +
                "WHERE b.turf_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteReviewsSql)) {
            stmt.setInt(1, turfId);
            stmt.executeUpdate();
        }

        // 2. Delete payments for this turf's bookings
        String deletePaymentsSql = "DELETE p FROM Payment p " +
                "JOIN Booking b ON p.booking_id = b.booking_id " +
                "WHERE b.turf_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deletePaymentsSql)) {
            stmt.setInt(1, turfId);
            stmt.executeUpdate();
        }

        // 3. Delete bookings for this turf
        String deleteBookingsSql = "DELETE FROM Booking WHERE turf_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteBookingsSql)) {
            stmt.setInt(1, turfId);
            stmt.executeUpdate();
        }

        // 4. Delete time slots for this turf
        String deleteSlotsSql = "DELETE FROM TimeSlots WHERE turf_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteSlotsSql)) {
            stmt.setInt(1, turfId);
            stmt.executeUpdate();
        }
    }

    public static void updateTurfAvailability(int turfId, boolean isAvailable) throws SQLException {
        String sql = "UPDATE Turf SET is_available = ? WHERE turf_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, isAvailable);
            stmt.setInt(2, turfId);
            stmt.executeUpdate();
        }
    }
    // Add these methods to TurfDB.java
    public static void updateTurfType(int turfId, String newType) throws SQLException {
        String sql = "UPDATE Turf SET turf_type = ? WHERE turf_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newType);
            stmt.setInt(2, turfId);
            stmt.executeUpdate();
        }
    }

    public static void updateTurfLocation(int turfId, String newLocation) throws SQLException {
        String sql = "UPDATE Turf SET location = ? WHERE turf_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newLocation);
            stmt.setInt(2, turfId);
            stmt.executeUpdate();
        }
    }

    public static void updateTurfFee(int turfId, double newFee) throws SQLException {
        String sql = "UPDATE Turf SET feeperhour = ? WHERE turf_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, newFee);
            stmt.setInt(2, turfId);
            stmt.executeUpdate();
        }
    }
    public static void updateTurfName(int turfId, String newName) throws SQLException {
        String sql = "UPDATE Turf SET turf_name = ? WHERE turf_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newName);
            stmt.setInt(2, turfId);
            stmt.executeUpdate();
        }
    }
}