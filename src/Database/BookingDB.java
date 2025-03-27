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
    public static int createBooking(Connection conn, int clientId, int turfId,
                                    int slotId, String bookingDate) throws SQLException {
        String sql = "INSERT INTO Booking (client_id, turf_id, slot_id, booking_date) " +
                "VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, clientId);
            stmt.setInt(2, turfId);
            stmt.setInt(3, slotId);
            stmt.setString(4, bookingDate);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public static int createBooking(int clientId, int turfId, int slotId, String bookingDate)
            throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return createBooking(conn, clientId, turfId, slotId, bookingDate);
        }
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
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Get slot ID from booking
            int slotId;
            String getSlotSql = "SELECT slot_id FROM booking WHERE booking_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(getSlotSql)) {
                stmt.setInt(1, bookingId);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) throw new SQLException("Booking not found");
                slotId = rs.getInt("slot_id");
            }

            // 2. Delete payment record
            String deletePaymentSql = "DELETE FROM payment WHERE booking_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deletePaymentSql)) {
                stmt.setInt(1, bookingId);
                stmt.executeUpdate();
            }

            // 3. Delete reviews
            String deleteReviewsSql = "DELETE FROM reviews WHERE booking_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteReviewsSql)) {
                stmt.setInt(1, bookingId);
                stmt.executeUpdate();
            }

            // 4. Delete booking
            String deleteBookingSql = "DELETE FROM booking WHERE booking_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteBookingSql)) {
                stmt.setInt(1, bookingId);
                if (stmt.executeUpdate() == 0) {
                    throw new SQLException("Booking not found");
                }
            }

            // 5. Free up the time slot
            TimeSlotsDB.updateSlotAvailability(slotId, true);

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
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