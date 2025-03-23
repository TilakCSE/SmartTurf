package cli;

import java.util.ArrayList;
import java.util.List;

public class ReviewManager {
    private List<Review> reviews;

    public ReviewManager() {
        reviews = new ArrayList<>();
    }

    // Add a review
    public void addReview(String bookingId, String turfId, String userEmail, int stars, String reviewText) {
        String reviewId = "R" + (reviews.size() + 1);
        Review review = new Review(reviewId, bookingId, turfId, userEmail, stars, reviewText);
        reviews.add(review);
        System.out.println("Review added successfully!");
    }

    // Get all reviews
    public List<Review> getAllReviews() {
        return reviews;
    }

    // Get reviews by turf ID
    public List<Review> getReviewsByTurfId(String turfId) {
        List<Review> turfReviews = new ArrayList<>();
        for (Review review : reviews) {
            if (review.getTurfId().equals(turfId)) {
                turfReviews.add(review);
            }
        }
        return turfReviews;
    }
}