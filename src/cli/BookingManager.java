package cli;

import interfaces.IBookingManager;
import exceptions.BookingNotFoundException;
import exceptions.TimeSlotNotAvailableException;
import exceptions.TimeSlotNotFoundException;
import exceptions.TurfNotAvailableException;
import java.util.ArrayList;
import java.util.List;

public class BookingManager implements IBookingManager {
    private List<Booking> bookings;
    private TurfManager turfManager;

    public BookingManager(TurfManager turfManager) {
        this.bookings = new ArrayList<>();
        this.turfManager = turfManager;
    }

    @Override
    public void bookTurf(String userEmail, String turfId, String slotId, String paymentMode)
            throws TimeSlotNotAvailableException, TimeSlotNotFoundException, TurfNotAvailableException {
        // Get the turf
        Turf turf = turfManager.getTurfById(turfId);

        // Book the time slot
        TimeSlotManager timeSlotManager = new TimeSlotManager(turf);
        timeSlotManager.bookTimeSlot(slotId);

        // Generate a unique booking ID
        String bookingId = "B" + (bookings.size() + 1);

        // Create a booking with Turf reference
        Booking booking = new Booking(bookingId, userEmail, turfId, slotId, turf);

        // Add the payment mode to the booking
        Payment payment = new Payment("P" + (bookings.size() + 1), turf.getFeePerHour(), paymentMode, "Pending");
        booking.setPayment(payment);

        // Add the booking
        bookings.add(booking);
        System.out.println("Booking Successful! Your Booking ID: " + bookingId);

        // Check if all time slots are booked
        if (turf.areAllTimeSlotsBooked()) {
            turf.setAvailable(false); // Mark the turf as unavailable
        }
    }

    @Override
    public void cancelBooking(String bookingId) throws BookingNotFoundException, TimeSlotNotFoundException {
        // Find the booking
        Booking bookingToRemove = null;
        for (Booking booking : bookings) {
            if (booking.getBookingId().equals(bookingId)) {
                bookingToRemove = booking;
                break;
            }
        }

        // If booking not found, throw exception
        if (bookingToRemove == null) {
            throw new BookingNotFoundException("Booking ID not found!");
        }

        // Mark the time slot as available
        try {
            Turf turf = turfManager.getTurfById(bookingToRemove.getTurfId());
            TimeSlotManager timeSlotManager = new TimeSlotManager(turf);
            timeSlotManager.cancelTimeSlot(bookingToRemove.getSlotId());

            // Mark the turf as available if any time slot is available
            turf.setAvailable(true);
        } catch (TurfNotAvailableException e) {
            System.out.println("Error: Turf not found while canceling booking.");
        }

        // Remove the booking
        bookings.remove(bookingToRemove);
        System.out.println("Booking Cancelled Successfully!");
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookings; // Return the list of all bookings
    }

    @Override
    public void addPaymentToBooking(String bookingId, double amount, String paymentMode, String paymentDetails) throws BookingNotFoundException {
        // Find the booking
        Booking booking = null;
        for (Booking b : bookings) {
            if (b.getBookingId().equals(bookingId)) {
                booking = b;
                break;
            }
        }

        // If booking not found, throw exception
        if (booking == null) {
            throw new BookingNotFoundException("Booking ID not found!");
        }

        // Create a payment and add it to the booking
        Payment payment = new Payment("P" + (bookings.size() + 1), amount, paymentMode, paymentDetails);
        booking.setPayment(payment);
        System.out.println("Payment added to booking " + bookingId);
    }

    @Override
    public Booking getBookingById(String bookingId) throws BookingNotFoundException {
        for (Booking booking : bookings) {
            if (booking.getBookingId().equals(bookingId)) {
                return booking;
            }
        }
        throw new BookingNotFoundException("Booking ID not found!"); // Throw exception if booking is not found
    }

    @Override
    public String getLatestBookingId() {
        if (bookings.isEmpty()) {
            return null;
        }
        return bookings.getLast().getBookingId();
    }
}