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

    // Getters
    public int getTurfId() { return turfId; }
    public String getTurfName() { return turfName; }
    public String getTurfType() { return turfType; }
    public String getLocation() { return location; }
    public double getFeePerHour() { return feePerHour; }
    public int getOwnerId() { return ownerId; }

    // CRUD Operations
    public static int createTurf(String turfName, String turfType, String location,
                                 double feePerHour, int ownerId) throws SQLException {
        String sql = "INSERT INTO Turf (turf_name, turf_type, location, feeperhour, owner_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, turfName);
            stmt.setString(2, turfType);
            stmt.setString(3, location);
            stmt.setDouble(4, feePerHour);
            stmt.setInt(5, ownerId);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

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

    public static void updateTurf(TurfDB turf) throws SQLException {
        String sql = "UPDATE Turf SET turf_name = ?, turf_type = ?, location = ?, feeperhour = ?, owner_id = ? WHERE turf_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, turf.getTurfName());
            stmt.setString(2, turf.getTurfType());
            stmt.setString(3, turf.getLocation());
            stmt.setDouble(4, turf.getFeePerHour());
            stmt.setInt(5, turf.getOwnerId());
            stmt.setInt(6, turf.getTurfId());
            stmt.executeUpdate();
        }
    }

    public static void deleteTurf(int turfId) throws SQLException {
        String sql = "DELETE FROM Turf WHERE turf_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, turfId);
            stmt.executeUpdate();
        }
    }

    public static List<TurfDB> getTurfsByOwner(int ownerId) throws SQLException {
        List<TurfDB> turfs = new ArrayList<>();
        String sql = "SELECT * FROM Turf WHERE owner_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ownerId);
            ResultSet rs = stmt.executeQuery();
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
}