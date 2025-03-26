package Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDB {
    private int bookingId;
    private int clientId;
    private int turfId;
    private int slotId;
    private String bookingDate;

    public BookingDB(int bookingId, int clientId, int turfId, int slotId, String bookingDate) {
        this.bookingId = bookingId;
        this.clientId = clientId;
        this.turfId = turfId;
        this.slotId = slotId;
        this.bookingDate = bookingDate;
    }

    // Getters
    public int getBookingId() { return bookingId; }
    public int getClientId() { return clientId; }
    public int getTurfId() { return turfId; }
    public int getSlotId() { return slotId; }
    public String getBookingDate() { return bookingDate; }

    // CRUD Operations
    public static int createBooking(int clientId, int turfId, int slotId, String bookingDate) throws SQLException {
        String sql = "INSERT INTO Booking (client_id, turf_id, slot_id, booking_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, clientId);
            stmt.setInt(2, turfId);
            stmt.setInt(3, slotId);
            stmt.setString(4, bookingDate);
            stmt.executeUpdate();

            // Mark slot as unavailable
            TimeSlotsDB.updateSlotAvailability(slotId, false);

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public static BookingDB getBookingById(int bookingId) throws SQLException {
        String sql = "SELECT * FROM Booking WHERE booking_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new BookingDB(
                        rs.getInt("booking_id"),
                        rs.getInt("client_id"),
                        rs.getInt("turf_id"),
                        rs.getInt("slot_id"),
                        rs.getString("booking_date")
                );
            }
        }
        return null;
    }

    public static List<BookingDB> getBookingsByClient(int clientId) throws SQLException {
        List<BookingDB> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Booking WHERE client_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookings.add(new BookingDB(
                        rs.getInt("booking_id"),
                        rs.getInt("client_id"),
                        rs.getInt("turf_id"),
                        rs.getInt("slot_id"),
                        rs.getString("booking_date")
                ));
            }
        }
        return bookings;
    }

    public static List<BookingDB> getAllBookings() throws SQLException {
        List<BookingDB> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Booking";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                bookings.add(new BookingDB(
                        rs.getInt("booking_id"),
                        rs.getInt("client_id"),
                        rs.getInt("turf_id"),
                        rs.getInt("slot_id"),
                        rs.getString("booking_date")
                ));
            }
        }
        return bookings;
    }

    public static void cancelBooking(int bookingId) throws SQLException {
        // First get the slot ID to mark it as available
        BookingDB booking = getBookingById(bookingId);
        if (booking == null) return;

        String sql = "DELETE FROM Booking WHERE booking_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            stmt.executeUpdate();

            // Mark slot as available again
            TimeSlotsDB.updateSlotAvailability(booking.getSlotId(), true);
        }
    }

    public static List<BookingDB> getBookingsByTurf(int turfId) throws SQLException {
        List<BookingDB> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Booking WHERE turf_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, turfId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookings.add(new BookingDB(
                        rs.getInt("booking_id"),
                        rs.getInt("client_id"),
                        rs.getInt("turf_id"),
                        rs.getInt("slot_id"),
                        rs.getString("booking_date")
                ));
            }
        }
        return bookings;
    }
}