package Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDB {
    private int paymentId;
    private int bookingId;
    private double amount;
    private String paymentMode;
    private String paymentDate;
    private String paymentStatus;

    public PaymentDB(int paymentId, int bookingId, double amount, String paymentMode,
                     String paymentDate, String paymentStatus) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentMode = paymentMode;
        this.paymentDate = paymentDate;
        this.paymentStatus = paymentStatus;
    }

    // Getters
    public int getPaymentId() { return paymentId; }
    public int getBookingId() { return bookingId; }
    public double getAmount() { return amount; }
    public String getPaymentMode() { return paymentMode; }
    public String getPaymentDate() { return paymentDate; }
    public String getPaymentStatus() { return paymentStatus; }

    // CRUD Operations
    public static int createPayment(int bookingId, double amount, String paymentMode,
                                    String paymentDate, String paymentStatus) throws SQLException {
        String sql = "INSERT INTO Payment (booking_id, amount, payment_mode, payment_date, payment_status) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, bookingId);
            stmt.setDouble(2, amount);
            stmt.setString(3, paymentMode);
            stmt.setString(4, paymentDate);
            stmt.setString(5, paymentStatus);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public static PaymentDB getPaymentByBooking(int bookingId) throws SQLException {
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
                        rs.getString("payment_date"),
                        rs.getString("payment_status")
                );
            }
        }
        return null;
    }

    public static void updatePaymentStatus(int paymentId, String newStatus) throws SQLException {
        String sql = "UPDATE Payment SET payment_status = ? WHERE payment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, paymentId);
            stmt.executeUpdate();
        }
    }

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
                        rs.getString("payment_date"),
                        rs.getString("payment_status")
                ));
            }
        }
        return payments;
    }

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
                        rs.getString("payment_date"),
                        rs.getString("payment_status")
                ));
            }
        }
        return payments;
    }
}