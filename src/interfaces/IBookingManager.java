package interfaces;

import exceptions.*;
import cli.Booking; // Import the Booking class

import java.sql.SQLException;
import java.util.List;

public interface IBookingManager {
    void bookTurf(int clientId, int turfId, int slotId, String paymentMode)
            throws BookingException, SQLException;



    void cancelBooking(String bookingId) throws BookingNotFoundException, TimeSlotNotFoundException;
    List<Booking> getAllBookings(); // Add this method
    void addPaymentToBooking(String bookingId, double amount, String paymentMode, String paymentDetails) throws BookingNotFoundException;
    Booking getBookingById(String bookingId) throws BookingNotFoundException; // Add this method
    String getLatestBookingId();
}