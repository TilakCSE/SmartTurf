package cli;

public class Booking {
    private String bookingId;
    private String userEmail;
    private String turfId;
    private String slotId;

    public Booking(String bookingId, String userEmail, String turfId, String slotId) {
        this.bookingId = bookingId;
        this.userEmail = userEmail;
        this.turfId = turfId;
        this.slotId = slotId;
    }

    // Getters
    public String getBookingId() { return bookingId; }
    public String getUserEmail() { return userEmail; }
    public String getTurfId() { return turfId; }
    public String getSlotId() { return slotId; }
}