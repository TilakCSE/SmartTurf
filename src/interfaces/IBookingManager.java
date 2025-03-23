package interfaces;

import exceptions.BookingNotFoundException;
import exceptions.TimeSlotNotAvailableException;
import exceptions.TimeSlotNotFoundException;
import exceptions.TurfNotAvailableException;
import cli.Booking; // Import the Booking class
import java.util.List;

public interface IBookingManager {
    void bookTurf(String userEmail, String turfId, String slotId, String paymentMode)
            throws TimeSlotNotAvailableException, TimeSlotNotFoundException, TurfNotAvailableException;
    void cancelBooking(String bookingId) throws BookingNotFoundException, TimeSlotNotFoundException;
    List<Booking> getAllBookings(); // Add this method
    void addPaymentToBooking(String bookingId, double amount, String paymentMode, String paymentDetails) throws BookingNotFoundException;
    Booking getBookingById(String bookingId) throws BookingNotFoundException; // Add this method
    String getLatestBookingId();
}