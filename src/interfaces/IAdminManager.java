package interfaces;

import exceptions.TurfNotAvailableException;
import exceptions.BookingNotFoundException;
import exceptions.TimeSlotNotFoundException;

public interface IAdminManager {
    void addTurf(String turfId, String sportType, String location, double feePerHour) throws TurfNotAvailableException;
    void deleteTurf(String turfId) throws TurfNotAvailableException;
    void viewBookedTurfs();
    void viewAllTurfs();
    void editTurf(String turfId) throws TurfNotAvailableException;
    void cancelClientBooking(String bookingId) throws BookingNotFoundException, TimeSlotNotFoundException;
    void editPaymentStatus(String bookingId, String status) throws BookingNotFoundException;
}