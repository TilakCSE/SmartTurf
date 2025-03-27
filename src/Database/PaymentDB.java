package Database;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PaymentDB {
    private int paymentId;
    private int bookingId;
    private double amount;
    private String paymentMode;
    private String paymentStatus;
    private Date paymentDate; // java.sql.Date

    public PaymentDB(int paymentId, int bookingId, double amount,
                     String paymentMode, String paymentStatus, Date paymentDate) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentMode = paymentMode;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
    }

    // Getters
    public int getPaymentId() { return paymentId; }
    public int getBookingId() { return bookingId; }
    public double getAmount() { return amount; }
    public String getPaymentMode() { return paymentMode; }
    public String getPaymentStatus() { return paymentStatus; }
    public Date getPaymentDate() { return paymentDate; }

    // Create new payment
    public static int createPayment(int bookingId, double amount, String paymentMode,
                                    String paymentStatus, Date paymentDate) throws SQLException {
        String sql = "INSERT INTO Payment (booking_id, amount, payment_mode, payment_status, payment_date) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, bookingId);
            stmt.setDouble(2, amount);
            stmt.setString(3, paymentMode);
            stmt.setString(4, paymentStatus);

            if (paymentDate != null) {
                stmt.setDate(5, paymentDate);
            } else {
                stmt.setNull(5, Types.DATE);
            }

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    // Get payment by ID
    public static PaymentDB getPaymentById(int paymentId) throws SQLException {
        String sql = "SELECT * FROM Payment WHERE payment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, paymentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new PaymentDB(
                        rs.getInt("payment_id"),
                        rs.getInt("booking_id"),
                        rs.getDouble("amount"),
                        rs.getString("payment_mode"),
                        rs.getString("payment_status"),
                        rs.getDate("payment_date")
                );
            }
        }
        return null;
    }

    // Get payment by booking ID
    public static PaymentDB getPaymentByBookingId(int bookingId) throws SQLException {
        String sql = "SELECT * FROM Payment WHERE booking_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new PaymentDB(
                        rs.getInt("payment_id"),
                        rs.getInt("booking_id"),
                        rs.getDouble("amount"),
                        rs.getString("payment_mode"),
                        rs.getString("payment_status"),
                        rs.getDate("payment_date")
                );
            }
        }
        return null;
    }

    // Update payment status
    public static void updatePaymentStatus(int paymentId, String newStatus) throws SQLException {
        String sql = "UPDATE Payment SET payment_status = ? WHERE payment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, paymentId);
            stmt.executeUpdate();
        }
    }

    // Update payment date
    public static void updatePaymentDate(int paymentId, Date newDate) throws SQLException {
        String sql = "UPDATE Payment SET payment_date = ? WHERE payment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (newDate != null) {
                stmt.setDate(1, newDate);
            } else {
                stmt.setNull(1, Types.DATE);
            }
            stmt.setInt(2, paymentId);
            stmt.executeUpdate();
        }
    }

    // Get all payments
    public static List<PaymentDB> getAllPayments() throws SQLException {
        List<PaymentDB> payments = new ArrayList<>();
        String sql = "SELECT * FROM Payment";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                payments.add(new PaymentDB(
                        rs.getInt("payment_id"),
                        rs.getInt("booking_id"),
                        rs.getDouble("amount"),
                        rs.getString("payment_mode"),
                        rs.getString("payment_status"),
                        rs.getDate("payment_date")
                ));
            }
        }
        return payments;
    }

    // Get payments by status
    public static List<PaymentDB> getPaymentsByStatus(String status) throws SQLException {
        List<PaymentDB> payments = new ArrayList<>();
        String sql = "SELECT * FROM Payment WHERE payment_status = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                payments.add(new PaymentDB(
                        rs.getInt("payment_id"),
                        rs.getInt("booking_id"),
                        rs.getDouble("amount"),
                        rs.getString("payment_mode"),
                        rs.getString("payment_status"),
                        rs.getDate("payment_date")
                ));
            }
        }
        return payments;
    }

    // Date formatter for display
    public static String formatPaymentDate(Date date) {
        if (date == null) return "Not Paid";
        return new SimpleDateFormat("MMM dd, yyyy").format(date);
    }

    // Convert string to SQL date
    public static Date parseDateString(String dateString) throws java.text.ParseException {
        if (dateString == null || dateString.trim().isEmpty()) return null;
        java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        return new Date(utilDate.getTime());
    }
}