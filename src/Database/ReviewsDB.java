package Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewsDB {
    private int reviewId;
    private int bookingId;
    private int clientId;
    private int turfId;
    private String reviewText;
    private int stars;
    private String reviewDate;

    public ReviewsDB(int reviewId, int bookingId, int clientId, int turfId,
                     String reviewText, int stars, String reviewDate) {
        this.reviewId = reviewId;
        this.bookingId = bookingId;
        this.clientId = clientId;
        this.turfId = turfId;
        this.reviewText = reviewText;
        this.stars = stars;
        this.reviewDate = reviewDate;
    }

    // Getters
    public int getReviewId() { return reviewId; }
    public int getBookingId() { return bookingId; }
    public int getClientId() { return clientId; }
    public int getTurfId() { return turfId; }
    public String getReviewText() { return reviewText; }
    public int getStars() { return stars; }
    public String getReviewDate() { return reviewDate; }

    // CRUD Operations
    public static int addReview(int bookingId, int clientId, int turfId,
                                String reviewText, int stars, String reviewDate) throws SQLException {
        String sql = "INSERT INTO Reviews (booking_id, client_id, turf_id, review_text, stars, review_date) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, bookingId);
            stmt.setInt(2, clientId);
            stmt.setInt(3, turfId);
            stmt.setString(4, reviewText);
            stmt.setInt(5, stars);
            stmt.setString(6, reviewDate);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public static ReviewsDB getReviewById(int reviewId) throws SQLException {
        String sql = "SELECT * FROM Reviews WHERE review_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reviewId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new ReviewsDB(
                        rs.getInt("review_id"),
                        rs.getInt("booking_id"),
                        rs.getInt("client_id"),
                        rs.getInt("turf_id"),
                        rs.getString("review_text"),
                        rs.getInt("stars"),
                        rs.getString("review_date")
                );
            }
        }
        return null;
    }


    public static List<ReviewsDB> getReviewsByTurf(int turfId) throws SQLException {
        List<ReviewsDB> reviews = new ArrayList<>();
        String sql = "SELECT * FROM Reviews WHERE turf_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, turfId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reviews.add(new ReviewsDB(
                        rs.getInt("review_id"),
                        rs.getInt("booking_id"),
                        rs.getInt("client_id"),
                        rs.getInt("turf_id"),
                        rs.getString("review_text"),
                        rs.getInt("stars"),
                        rs.getString("review_date")
                ));
            }
        }
        return reviews;
    }

    public static List<ReviewsDB> getReviewsByClient(int clientId) throws SQLException {
        List<ReviewsDB> reviews = new ArrayList<>();
        String sql = "SELECT * FROM Reviews WHERE client_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reviews.add(new ReviewsDB(
                        rs.getInt("review_id"),
                        rs.getInt("booking_id"),
                        rs.getInt("client_id"),
                        rs.getInt("turf_id"),
                        rs.getString("review_text"),
                        rs.getInt("stars"),
                        rs.getString("review_date")
                ));
            }
        }
        return reviews;
    }

    public static double getAverageRatingForTurf(int turfId) throws SQLException {
        String sql = "SELECT AVG(stars) as average FROM Reviews WHERE turf_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, turfId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("average");
            }
        }
        return 0;
    }

    public static void deleteReview(int reviewId) throws SQLException {
        String sql = "DELETE FROM Reviews WHERE review_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reviewId);
            stmt.executeUpdate();
        }
    }
}