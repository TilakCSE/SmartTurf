package cli;

import Database.ReviewsDB;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReviewManager {
    private List<Review> reviews;

    public ReviewManager() {
        reviews = new ArrayList<>();
    }

    // Add a review
    public void addReview(String bookingId, String clientId, String turfId,
                          String reviewText, int stars) {
        try {
            // Generate review ID
            String reviewId = "R" + (reviews.size() + 1);

            // Create review date (current date)
            String reviewDate = new java.sql.Date(System.currentTimeMillis()).toString();

            // Add to database
            ReviewsDB.addReview(
                    Integer.parseInt(bookingId),
                    Integer.parseInt(clientId),
                    Integer.parseInt(turfId),
                    reviewText,
                    stars,
                    reviewDate
            );

            // Also add to local list
            reviews.add(new Review(reviewId, bookingId, turfId, clientId, stars, reviewText));

        } catch (NumberFormatException e) {
            System.err.println("Invalid ID format: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Failed to save review: " + e.getMessage());
        }
    }
}