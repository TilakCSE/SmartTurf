package cli;

import Database.PaymentDB;
import interfaces.*;
import exceptions.*;

import java.sql.SQLException;
import java.util.Scanner;

public class AdminManager implements IAdminManager {
    private final TurfManager turfManager;
    private final IBookingManager bookingManager;

    public AdminManager(TurfManager turfManager, IBookingManager bookingManager) {
        this.turfManager = turfManager;
        this.bookingManager = bookingManager;
    }

    @Override
    public void addTurf(String turfId, String sportType, String location, double feePerHour)
            throws TurfNotAvailableException {
        try {
            Turf turf = new Turf(turfId, sportType, location, feePerHour);
            turfManager.addTurf(turf);
            System.out.println("Turf added successfully!");
        } catch (Exception e) {
            throw new TurfNotAvailableException("Failed to add turf: " + e.getMessage());
        }
    }

    @Override
    public void deleteTurf(String turfId) throws TurfNotAvailableException {
        try {
            Turf turf = turfManager.getTurfById(turfId);
            turfManager.deleteTurf(turf);
            System.out.println("Turf deleted successfully!");
        } catch (Exception e) {
            throw new TurfNotAvailableException("Failed to delete turf: " + e.getMessage());
        }
    }

    @Override
    public void viewBookedTurfs() {
        try {
            bookingManager.getAllBookings().forEach(booking -> {
                System.out.println(
                        "Booking ID: " + booking.getBookingId() +
                                ", Turf ID: " + booking.getTurfId() +
                                ", Slot: " + booking.getSlotId()
                );
            });
        } catch (Exception e) {
            System.err.println("Error fetching bookings: " + e.getMessage());
        }
    }

    @Override
    public void viewAllTurfs() {
        turfManager.getAllTurfs().forEach(turf ->
                System.out.println(
                        "ID: " + turf.getTurfId() +
                                ", Sport: " + turf.getSportType() +
                                ", Location: " + turf.getLocation()
                )
        );
    }

    @Override
    public void editTurf(String turfId) throws TurfNotAvailableException {
        try {
            Turf turf = turfManager.getTurfById(turfId);
            Scanner scanner = new Scanner(System.in);

            System.out.println("\nEditing Turf: " + turfId);
            System.out.print("New Sport Type (leave blank to keep current): ");
            String sportType = scanner.nextLine();
            if (!sportType.isEmpty()) turf.setSportType(sportType);

            System.out.print("New Location (leave blank to keep current): ");
            String location = scanner.nextLine();
            if (!location.isEmpty()) turf.setLocation(location);

            System.out.print("New Fee per Hour (0 to keep current): ");
            String feeInput = scanner.nextLine();
            if (!feeInput.isEmpty()) {
                double fee = Double.parseDouble(feeInput);
                if (fee > 0) turf.setFeePerHour(fee);
            }

            System.out.println("Turf updated successfully!");
        } catch (Exception e) {
            throw new TurfNotAvailableException("Failed to edit turf: " + e.getMessage());
        }
    }
    @Override
    public void editPaymentStatus(String bookingId, String status) throws BookingNotFoundException {
        try {
            // Convert bookingId to int if needed (assuming DB uses int)
            int bookingIdInt = Integer.parseInt(bookingId);

            // Get the payment record
            PaymentDB payment = PaymentDB.getPaymentByBookingId(bookingIdInt);

            if (payment == null) {
                throw new BookingNotFoundException("No payment found for booking ID: " + bookingId);
            }

            // Validate status
            if (!status.equalsIgnoreCase("Pending") && !status.equalsIgnoreCase("Completed")) {
                System.out.println("Invalid status. Only 'Pending' or 'Completed' allowed.");
                return;
            }

            // Update payment status
            PaymentDB.updatePaymentStatus(payment.getPaymentId(), status);

            // If marking as completed, set the payment date
            if (status.equalsIgnoreCase("Completed")) {
                PaymentDB.updatePaymentDate(payment.getPaymentId(), new java.sql.Date(System.currentTimeMillis()));
            } else {
                PaymentDB.updatePaymentDate(payment.getPaymentId(), null);
            }

            System.out.println("Payment status updated successfully!");
        } catch (NumberFormatException e) {
            throw new BookingNotFoundException("Invalid booking ID format");
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
