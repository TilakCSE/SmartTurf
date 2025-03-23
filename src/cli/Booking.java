package cli;

import java.io.Serializable;
import java.time.LocalDate;

public class Booking implements Serializable {
    private String bookingId;
    private String userEmail;
    private String turfId;
    private String slotId;
    private Payment payment; // Payment is optional
    private Turf turf; // Add reference to Turf
    private LocalDate bookingDate; // Add booking date

    // Constructor without payment
    public Booking(String bookingId, String userEmail, String turfId, String slotId, Turf turf) {
        this.bookingId = bookingId;
        this.userEmail = userEmail;
        this.turfId = turfId;
        this.slotId = slotId;
        this.turf = turf; // Initialize Turf
        this.payment = null; // Payment is initially null
        this.bookingDate = LocalDate.now(); // Set booking date to current date
    }

    // Getters
    public String getBookingId() { return bookingId; }
    public String getUserEmail() { return userEmail; }
    public String getTurfId() { return turfId; }
    public String getSlotId() { return slotId; }
    public Payment getPayment() { return payment; }
    public Turf getTurf() { return turf; } // Add getter for Turf
    public LocalDate getBookingDate() { return bookingDate; } // Add getter for booking date

    // Setter for payment
    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    // Get payment status
    public String getPaymentStatus() {
        if (payment == null) {
            return "Pending"; // Payment is pending for cash
        } else {
            return payment.getPaymentDetails(); // Use getPaymentDetails
        }
    }
}