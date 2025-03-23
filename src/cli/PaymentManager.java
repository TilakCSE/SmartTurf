package cli;

import exceptions.BookingNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class PaymentManager {
    private Map<String, Payment> payments; // Map booking ID to payment

    public PaymentManager() {
        payments = new HashMap<>();
    }

    // Add payment to a booking
    public void addPaymentToBooking(String bookingId, double amount, String paymentMode, String paymentDetails) throws BookingNotFoundException {
        if (!payments.containsKey(bookingId)) {
            throw new BookingNotFoundException("Booking ID not found!");
        }
        Payment payment = new Payment("P" + (payments.size() + 1), amount, paymentMode, paymentDetails);
        payments.put(bookingId, payment);
        System.out.println("Payment added to booking " + bookingId);
    }

    // Get payment details for a booking
    public Payment getPayment(String bookingId) throws BookingNotFoundException {
        if (!payments.containsKey(bookingId)) {
            throw new BookingNotFoundException("Booking ID not found!");
        }
        return payments.get(bookingId);
    }

    // Check if payment is pending
    public boolean isPaymentPending(String bookingId) throws BookingNotFoundException {
        if (!payments.containsKey(bookingId)) {
            throw new BookingNotFoundException("Booking ID not found!");
        }
        return payments.get(bookingId) == null;
    }
}