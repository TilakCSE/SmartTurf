package cli;

public class Review {
    private String reviewId;
    private String bookingId;
    private String turfId;
    private String userEmail;
    private int stars; // Rating out of 5
    private String reviewText; // Review text

    // Constructor
    public Review(String reviewId, String bookingId, String turfId, String userEmail, int stars, String reviewText) {
        this.reviewId = reviewId;
        this.bookingId = bookingId;
        this.turfId = turfId;
        this.userEmail = userEmail;
        this.stars = stars;
        this.reviewText = reviewText;
    }

    // Getters
    public String getReviewId() { return reviewId; }
    public String getBookingId() { return bookingId; }
    public String getTurfId() { return turfId; }
    public String getUserEmail() { return userEmail; }
    public int getStars() { return stars; }
    public String getReviewText() { return reviewText; }
}