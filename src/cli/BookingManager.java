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
    public void bookTurf(String userEmail, String turfId, String slotId)
            throws TimeSlotNotAvailableException, TimeSlotNotFoundException, TurfNotAvailableException {
        // Generate a unique booking ID
        String bookingId = "B" + (bookings.size() + 1);

        // Book the time slot
        Turf turf = turfManager.getTurfById(turfId); // This can throw TurfNotAvailableException
        TimeSlotManager timeSlotManager = new TimeSlotManager(turf);
        timeSlotManager.bookTimeSlot(slotId); // This can throw TimeSlotNotAvailableException or TimeSlotNotFoundException

        // Add the booking
        bookings.add(new Booking(bookingId, userEmail, turfId, slotId));
        System.out.println("Booking Successful! Your Booking ID: " + bookingId);
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
        } catch (TurfNotAvailableException e) {
            // Handle the case where the turf is not found (unlikely since it was booked earlier)
            System.out.println("Error: Turf not found while canceling booking.");
        }

        // Remove the booking
        bookings.remove(bookingToRemove);
        System.out.println("Booking Cancelled Successfully!");
    }
}