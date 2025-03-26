package Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotsDB {
    private int slotId;
    private int turfId;
    private String slotTime;
    private boolean isAvailable;

    public TimeSlotsDB(int slotId, int turfId, String slotTime, boolean isAvailable) {
        this.slotId = slotId;
        this.turfId = turfId;
        this.slotTime = slotTime;
        this.isAvailable = isAvailable;
    }

    // Getters
    public int getSlotId() { return slotId; }
    public int getTurfId() { return turfId; }
    public String getSlotTime() { return slotTime; }
    public boolean isAvailable() { return isAvailable; }

    // CRUD Operations
    public static int addTimeSlot(int turfId, String slotTime) throws SQLException {
        String sql = "INSERT INTO TimeSlots (turf_id, slot_time, is_available) VALUES (?, ?, TRUE)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, turfId);
            stmt.setString(2, slotTime);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public static TimeSlotsDB getSlotById(int slotId) throws SQLException {
        String sql = "SELECT * FROM TimeSlots WHERE slot_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, slotId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new TimeSlotsDB(
                        rs.getInt("slot_id"),
                        rs.getInt("turf_id"),
                        rs.getString("slot_time"),
                        rs.getBoolean("is_available")
                );
            }
        }
        return null;
    }

    public static List<TimeSlotsDB> getSlotsByTurf(int turfId) throws SQLException {
        List<TimeSlotsDB> slots = new ArrayList<>();
        String sql = "SELECT * FROM TimeSlots WHERE turf_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, turfId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                slots.add(new TimeSlotsDB(
                        rs.getInt("slot_id"),
                        rs.getInt("turf_id"),
                        rs.getString("slot_time"),
                        rs.getBoolean("is_available")
                ));
            }
        }
        return slots;
    }

    public static List<TimeSlotsDB> getAvailableSlotsByTurf(int turfId) throws SQLException {
        List<TimeSlotsDB> slots = new ArrayList<>();
        String sql = "SELECT * FROM TimeSlots WHERE turf_id = ? AND is_available = TRUE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, turfId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                slots.add(new TimeSlotsDB(
                        rs.getInt("slot_id"),
                        rs.getInt("turf_id"),
                        rs.getString("slot_time"),
                        true
                ));
            }
        }
        return slots;
    }

    public static void updateSlotAvailability(int slotId, boolean isAvailable) throws SQLException {
        String sql = "UPDATE TimeSlots SET is_available = ? WHERE slot_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, isAvailable);
            stmt.setInt(2, slotId);
            stmt.executeUpdate();
        }
    }

    public static void deleteSlot(int slotId) throws SQLException {
        String sql = "DELETE FROM TimeSlots WHERE slot_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, slotId);
            stmt.executeUpdate();
        }
    }
}