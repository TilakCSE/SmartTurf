package cli;

import interfaces.IAdminManager;
import interfaces.IBookingManager;
import exceptions.TurfNotAvailableException;
import exceptions.BookingNotFoundException;
import exceptions.TimeSlotNotFoundException;
import java.util.List;
import java.util.Scanner;

public class AdminManager implements IAdminManager {
    private TurfManager turfManager;
    private IBookingManager bookingManager;
    private ReviewManager reviewManager; // Add ReviewManager
    private Scanner scanner = new Scanner(System.in);

    public AdminManager(TurfManager turfManager, IBookingManager bookingManager, ReviewManager reviewManager) {
        this.turfManager = turfManager;
        this.bookingManager = bookingManager;
        this.reviewManager = reviewManager; // Initialize ReviewManager
    }

    @Override
    public void addTurf(String turfId, String sportType, String location, double feePerHour) throws TurfNotAvailableException {
        // Create a new Turf object
        Turf turf = new Turf(turfId, sportType, location, feePerHour);

        // Add timeslots dynamically
        while (true) {
            System.out.print("Add a timeslot? (yes/no): ");
            String choice = scanner.nextLine().trim().toLowerCase();
            if (!choice.equals("yes")) {
                break;
            }

            System.out.print("Enter Slot ID: ");
            String slotId = scanner.nextLine();
            System.out.print("Enter Slot Time (e.g., 10:00 AM - 11:00 AM): ");
            String slotTime = scanner.nextLine();

            turf.addTimeSlot(slotId, slotTime);
            System.out.println("Timeslot added successfully!");
        }

        // Add the turf
        turfManager.addTurf(turf);
        System.out.println("Turf added successfully!");
    }

    @Override
    public void deleteTurf(String turfId) throws TurfNotAvailableException {
        Turf turf = turfManager.getTurfById(turfId); // Get turf by ID
        turfManager.deleteTurf(turf); // Call deleteTurf with Turf object
        System.out.println("Turf deleted successfully!");
    }

    @Override
    public void viewBookedTurfs() {
        List<Booking> bookings = bookingManager.getAllBookings();
        if (bookings.isEmpty()) {
            System.out.println("No turfs are currently booked.");
        } else {
            System.out.println("Booked Turfs:");
            for (Booking booking : bookings) {
                try {
                    Turf turf = turfManager.getTurfById(booking.getTurfId()); // Get turf by ID
                    System.out.println("Booking ID: " + booking.getBookingId() +
                            ", Turf ID: " + booking.getTurfId() +
                            ", Sport: " + turf.getSportType() +
                            ", Location: " + turf.getLocation() +
                            ", Slot ID: " + booking.getSlotId() +
                            ", Client Email: " + booking.getUserEmail() +
                            ", Booking Date: " + booking.getBookingDate());

                    // Display payment details
                    Payment payment = booking.getPayment();
                    if (payment == null) {
                        System.out.println("  Payment Status: Pending");
                        System.out.println("  Payment Mode: Cash"); // Default to Cash if no payment is set
                    } else {
                        System.out.println("  Payment ID: " + payment.getPaymentId());
                        System.out.println("  Payment Amount: ₹" + payment.getAmount());
                        System.out.println("  Payment Mode: " + payment.getPaymentMode());
                        System.out.println("  Payment Date: " + (payment.getPaymentDate() == null ? "Pending" : payment.getPaymentDate()));
                        System.out.println("  Payment Status: " + payment.getPaymentDetails());
                    }

                    // Display reviews for this turf
                    List<Review> reviews = reviewManager.getReviewsByTurfId(booking.getTurfId());
                    if (reviews.isEmpty()) {
                        System.out.println("  No reviews for this turf.");
                    } else {
                        System.out.println("  Reviews:");
                        for (Review review : reviews) {
                            System.out.println("    Review ID: " + review.getReviewId() +
                                    ", Stars: " + review.getStars() +
                                    ", Review: " + review.getReviewText() +
                                    ", Client Email: " + review.getUserEmail());
                        }
                    }
                } catch (TurfNotAvailableException e) {
                    System.out.println("Error: Turf not found for booking ID " + booking.getBookingId());
                }
            }
        }
    }

    @Override
    public void viewAllTurfs() {
        List<Turf> turfs = turfManager.getAvailableTurfs(); // Call getAvailableTurfs
        if (turfs.isEmpty()) {
            System.out.println("No turfs available.");
        } else {
            System.out.println("All Turfs:");
            for (Turf turf : turfs) {
                System.out.println("Turf ID: " + turf.getTurfId() +
                        ", Sport: " + turf.getSportType() +
                        ", Location: " + turf.getLocation() +
                        ", Fee per Hour: ₹" + turf.getFeePerHour() +
                        ", Available: " + turf.isAvailable());
            }
        }
    }

    @Override
    public void editTurf(String turfId) throws TurfNotAvailableException {
        Turf turf = turfManager.getTurfById(turfId);
        if (turf == null) {
            throw new TurfNotAvailableException("Turf not found!");
        }

        System.out.println("Editing Turf: " + turfId);
        System.out.print("Enter new Turf ID (leave blank to keep current): ");
        String newTurfId = scanner.nextLine().trim();
        if (!newTurfId.isEmpty()) {
            turf.setTurfId(newTurfId);
        }

        System.out.print("Enter new Sport Type (leave blank to keep current): ");
        String newSportType = scanner.nextLine().trim();
        if (!newSportType.isEmpty()) {
            turf.setSportType(newSportType);
        }

        System.out.print("Enter new Location (leave blank to keep current): ");
        String newLocation = scanner.nextLine().trim();
        if (!newLocation.isEmpty()) {
            turf.setLocation(newLocation);
        }

        System.out.print("Enter new Fee per Hour (leave blank to keep current): ");
        String feeInput = scanner.nextLine().trim();
        if (!feeInput.isEmpty()) {
            double newFee = Double.parseDouble(feeInput);
            turf.setFeePerHour(newFee);
        }

        System.out.println("Turf updated successfully!");
    }

    @Override
    public void cancelClientBooking(String bookingId) throws BookingNotFoundException, TimeSlotNotFoundException {
        bookingManager.cancelBooking(bookingId);
        System.out.println("Client booking canceled successfully!");
    }

    @Override
    public void editPaymentStatus(String bookingId, String status) throws BookingNotFoundException {
        Booking booking = bookingManager.getBookingById(bookingId);

        if (status.equalsIgnoreCase("Pending") || status.equalsIgnoreCase("Completed")) {
            if (booking.getPayment() == null) {
                // Create a new payment if it doesn't exist
                Payment payment = new Payment("P" + (bookingManager.getAllBookings().size() + 1),
                        booking.getTurf().getFeePerHour(),
                        "Cash",
                        "Payment updated by admin");
                booking.setPayment(payment);
            }

            // Update payment status
            if (status.equalsIgnoreCase("Pending")) {
                booking.getPayment().setPaymentDetails("Pending - Cash will be collected at the venue.");
            } else if (status.equalsIgnoreCase("Completed")) {
                booking.getPayment().setPaymentDetails("Completed - Payment collected at the venue.");
            }

            System.out.println("Payment status updated to: " + status);
        } else {
            System.out.println("Invalid status! Please enter 'Pending' or 'Completed'.");
        }
    }
}