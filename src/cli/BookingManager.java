package cli;
import Database.*;
import exceptions.*;
import interfaces.IBookingManager;
import java.sql.*;
import java.sql.SQLException;
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
    public void bookTurf(int clientId, int turfId, int slotId, String paymentMode)
            throws BookingException, SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Validate Turf exists
            if (!TurfDB.turfExists(turfId)) {
                throw new BookingException("❌ Error: Turf ID " + turfId + " doesn't exist");
            }

            // 2. Validate Slot exists and belongs to this turf
            TimeSlotsDB slot = TimeSlotsDB.getSlotById(slotId);
            if (slot == null) {
                throw new BookingException("❌ Error: Time slot doesn't exist");
            }
            if (slot.getTurfId() != turfId) {
                throw new BookingException("❌ Error: This slot doesn't belong to the selected turf");
            }
            if (!slot.isAvailable()) {
                throw new BookingException("❌ Error: This time slot is already booked");
            }

            // 3. Validate Payment Mode
            if (!paymentMode.equalsIgnoreCase("cash") && !paymentMode.equalsIgnoreCase("online")) {
                throw new BookingException("❌ Error: Payment mode must be 'cash' or 'online'");
            }

            // 4. Create booking record
            String bookingDate = new java.sql.Date(System.currentTimeMillis()).toString();
            int bookingId = BookingDB.createBooking(conn, clientId, turfId, slotId, bookingDate);
            if (bookingId == -1) {
                throw new BookingException("❌ Error: Failed to create booking");
            }

            // 5. Process payment
            TurfDB turf = TurfDB.getTurfById(turfId);
            String paymentStatus = paymentMode.equalsIgnoreCase("online") ? "Completed" : "Pending";
            Date paymentDate = paymentMode.equalsIgnoreCase("online") ?
                    new java.sql.Date(System.currentTimeMillis()) : null;

            PaymentDB.createPayment(bookingId, turf.getFeePerHour(),
                    paymentMode, paymentStatus, paymentDate);

            // 6. Mark slot as unavailable
            TimeSlotsDB.updateSlotAvailability(conn, slotId, false);

            conn.commit();
            System.out.println("\n✅ Booking successful! ID: " + bookingId);
            System.out.println("Turf: " + turf.getTurfName());
            System.out.println("Slot: " + slot.getSlotTime());
            System.out.printf("Amount: ₹%.2f%n", turf.getFeePerHour());
            System.out.println("Payment Mode: " + paymentMode);
            System.out.println("Status: " + paymentStatus);

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw new BookingException("❌ Database error: " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
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
            return bookings.get(bookings.size() - 1).getBookingId();
        }
    }