package cli;

public class Booking {
    private final String bookingId;
    private final String userEmail;
    private final String turfId;
    private final String timeSlot;

    public Booking(String bookingId, String userEmail, String turfId, String timeSlot) {
        this.bookingId = bookingId;
        this.userEmail = userEmail;
        this.turfId = turfId;
        this.timeSlot = timeSlot;
    }

    // Getters and Setters
    public String getBookingId() { return bookingId; }
    public String getUserEmail() { return userEmail; }
    public String getTurfId() { return turfId; }
    public String getTimeSlot() { return timeSlot; }
}