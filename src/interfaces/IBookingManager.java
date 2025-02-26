package interfaces;

import exceptions.TimeSlotNotAvailableException;
import exceptions.TimeSlotNotFoundException;
import exceptions.TurfNotAvailableException;
import exceptions.BookingNotFoundException;

public interface IBookingManager {
    void bookTurf(String userEmail, String turfId, String slotId)
            throws TimeSlotNotAvailableException, TimeSlotNotFoundException, TurfNotAvailableException;
    void cancelBooking(String bookingId) throws BookingNotFoundException, TimeSlotNotFoundException;
}