package cli;

import Database.*;
import interfaces.IAdminManager;
import interfaces.IBookingManager;
import exceptions.*;
import java.sql.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class CLIApp {
    private static UserManager userManager = new UserManager();
    private static TurfManager turfManager = new TurfManager();
    private static IBookingManager bookingManager = new BookingManager(turfManager);
    private static ReviewManager reviewManager = new ReviewManager();
    private static IAdminManager adminManager = new AdminManager(turfManager, bookingManager, reviewManager);
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            DatabaseConnection.initialize();
            DatabaseSetup.initializeDatabase();

            while (true) {
                System.out.println("\n=== Smart Turf Booking System ===");
                System.out.println("1. Login as Admin");
                System.out.println("2. Proceed as Client");
                System.out.println("3. Exit");
                System.out.print("Enter choice: ");

                try {
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    switch (choice) {
                        case 1:
                            loginAdmin();
                            break;
                        case 2:
                            showClientMenu();
                            break;
                        case 3:
                            System.exit(0);
                            break;
                        default:
                            System.out.println("Invalid choice!");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Please enter a number!");
                    scanner.nextLine();
                }
            }
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }

    private static void loginAdmin() {
        System.out.print("\nEnter admin username: ");
        String username = scanner.nextLine();
        System.out.print("Enter admin password: ");
        String password = scanner.nextLine();

        try {
            AdminDB admin = AdminDB.getAdminByCredentials(username, password);
            if (admin != null) {
                System.out.println("Admin login successful!");
                showAdminMenu();
            } else {
                System.out.println("Invalid credentials!");
            }
        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
        }
    }

    private static void showAdminMenu() {
        while (true) {
            System.out.println("\n=== Admin Menu ===");
            System.out.println("1. Add Turf");
            System.out.println("2. Delete Turf");
            System.out.println("3. View Booked Turfs");
            System.out.println("4. View All Turfs");
            System.out.println("5. Edit Turf");
            System.out.println("6. Cancel Client Booking");
            System.out.println("7. Edit Payment Status");
            System.out.println("8. View All Payments");
            System.out.println("9. Logout");
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        addTurf();
                        break;
                    case 2:
                        deleteTurf();
                        break;
                    case 3:
                        viewBookedTurfs();
                        break;
                    case 4:
                        viewAllTurfs();
                        break;
                    case 5:
                        editTurf();
                        break;
                    case 6:
                        cancelClientBooking();
                        break;
                    case 7:
                        editPaymentStatus();
                        break;
                    case 8:
                        viewAllPayments();
                        break;
                    case 9:
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine();
            } catch (TurfNotAvailableException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void addTurf() {
        System.out.print("Enter Turf ID: ");
        String turfId = scanner.nextLine();
        System.out.print("Enter Sport Type: ");
        String sportType = scanner.nextLine();
        System.out.print("Enter Location: ");
        String location = scanner.nextLine();
        System.out.print("Enter Fee per Hour: ");
        double feePerHour = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        try {
            adminManager.addTurf(turfId, sportType, location, feePerHour);
        } catch (TurfNotAvailableException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void deleteTurf() throws TurfNotAvailableException {
        System.out.print("Enter Turf ID: ");
        String turfId = scanner.nextLine();
        adminManager.deleteTurf(turfId);
    }

    private static void viewBookedTurfs() {
        adminManager.viewBookedTurfs();
    }

    private static void viewAllTurfs() {
        adminManager.viewAllTurfs();
    }

    private static void editTurf() {
        System.out.print("Enter Turf ID to edit: ");
        String turfId = scanner.nextLine();
        try {
            adminManager.editTurf(turfId);
        } catch (TurfNotAvailableException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void viewAllPayments() {
        try {
            List<PaymentDB> payments = PaymentDB.getAllPayments();
            if (payments.isEmpty()) {
                System.out.println("No payments found.");
                return;
            }

            System.out.println("\n=== All Payments ===");
            System.out.printf("%-10s %-12s %-15s %-15s %-20s%n",
                    "PaymentID", "Amount", "Mode", "Status", "Date");

            for (PaymentDB payment : payments) {
                String dateStr = payment.getPaymentDate() != null
                        ? new SimpleDateFormat("dd-MMM-yyyy").format(payment.getPaymentDate())
                        : "Pending";

                System.out.printf("%-10d ₹%-11.2f %-15s %-15s %-20s%n",
                        payment.getPaymentId(),
                        payment.getAmount(),
                        payment.getPaymentMode(),
                        payment.getPaymentStatus(),
                        dateStr);
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    private static void cancelClientBooking() {
        System.out.print("Enter Booking ID to cancel: ");
        String bookingId = scanner.nextLine();
        try {
            adminManager.cancelClientBooking(bookingId);
        } catch (BookingNotFoundException | TimeSlotNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void editPaymentStatus() {
        try {
            System.out.print("Enter Booking ID: ");
            int bookingId = scanner.nextInt();
            scanner.nextLine();

            PaymentDB payment = PaymentDB.getPaymentByBookingId(bookingId);
            if (payment == null) {
                System.out.println("No payment found for this booking.");
                return;
            }

            PaymentDisplayUtil.displayPayment(payment);

            System.out.print("Enter new status (Pending/Completed): ");
            String status = scanner.nextLine();

            if (status.equalsIgnoreCase("completed")) {
                PaymentDB.updatePaymentStatus(payment.getPaymentId(), "Completed");
                PaymentDB.updatePaymentDate(payment.getPaymentId(), new java.sql.Date(System.currentTimeMillis()));
                System.out.println("Payment marked as completed.");
            } else if (status.equalsIgnoreCase("pending")) {
                PaymentDB.updatePaymentStatus(payment.getPaymentId(), "Pending");
                PaymentDB.updatePaymentDate(payment.getPaymentId(), null);
                System.out.println("Payment marked as pending.");
            } else {
                System.out.println("Invalid status. Use 'Pending' or 'Completed'.");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void showClientMenu() {
        while (true) {
            System.out.println("\n=== Client Menu ===");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. View Available Turfs");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        registerUser();
                        break;
                    case 2:
                        int clientId = loginClient();
                        if (clientId != -1) {
                            showLoggedInClientMenu(clientId);
                        }
                        break;
                    case 3:
                        viewAvailableTurfs();
                        break;
                    case 4:
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a number!");
                scanner.nextLine();
            }
        }
    }

    private static void registerUser() {
        System.out.println("\n=== User Registration ===");

        String username;
        while (true) {
            System.out.print("Enter username: ");
            username = scanner.nextLine();

            try {
                // Check if username exists
                if (UsersDB.usernameExists(username)) {
                    System.out.println("Username '" + username + "' already exists. Please choose another.");
                } else {
                    break; // Exit loop if username is available
                }
            } catch (SQLException e) {
                System.err.println("Error checking username availability. Please try again.");
                return;
            }
        }

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.print("Enter contact info: ");
        String contact = scanner.nextLine();

        try {
            // Create user with auto-increment ID
            UsersDB.createUser(new UsersDB(0, username, password, "client"));

            // Get the created user to get the generated ID
            UsersDB user = UsersDB.getUserByCredentials(username, password);
            if (user != null) {
                ClientDB.createClient(new ClientDB(
                        user.getUserId(),
                        username,
                        password,
                        email,
                        contact
                ));
                System.out.println("Registration successful! Your ID: " + user.getUserId());
            }
        } catch (SQLException e) {
            System.err.println("Registration failed: " + e.getMessage());
        }
    }

    private static int loginClient() {
        System.out.println("\n=== Client Login ===");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            ClientDB client = ClientDB.getClientByCredentials(username, password);
            if (client != null) {
                System.out.println("Login successful!");
                return client.getClientId();
            }
            System.out.println("Invalid credentials!");
        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
        }
        return -1; // Invalid login
    }

    private static void showLoggedInClientMenu(int clientId) {
        while (true) {
            System.out.println("\n=== Client Dashboard ===");
            System.out.println("1. View Available Turfs");
            System.out.println("2. Book a Turf");
            System.out.println("3. Cancel Booking");
            System.out.println("4. View Payment History");
            System.out.println("5. Submit Review");
            System.out.println("6. Logout");
            System.out.print("Enter choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        viewAvailableTurfs();
                        break;
                    case 2:
                        bookTurf(clientId);
                        break;
                    case 3:
                        cancelBooking(clientId);
                        break;
                    case 4:
                        viewPaymentHistory(clientId);
                        break;
                    case 5:
                        submitReview(clientId);
                        break;
                    case 6:
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a number!");
                scanner.nextLine();
            }
        }
    }

    private static void viewAvailableTurfs() {
        try {
            List<TurfDB> turfs = TurfDB.getAllTurfs();
            if (turfs.isEmpty()) {
                System.out.println("No turfs available.");
                return;
            }

            System.out.println("\n=== Available Turfs ===");
            for (TurfDB turf : turfs) {
                System.out.printf("\nID: %d | %s (%s)\n",
                        turf.getTurfId(), turf.getTurfName(), turf.getTurfType());
                System.out.printf("Location: %s | ₹%.2f per hour\n",
                        turf.getLocation(), turf.getFeePerHour());

                List<TimeSlotsDB> slots = TimeSlotsDB.getAvailableSlotsByTurf(turf.getTurfId());
                if (slots.isEmpty()) {
                    System.out.println("  No available slots");
                } else {
                    System.out.println("  Available Slots:");
                    for (TimeSlotsDB slot : slots) {
                        System.out.printf("  Slot %d: %s\n",
                                slot.getSlotId(), slot.getSlotTime());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading turfs: " + e.getMessage());
        }
    }

    private static void bookTurf(int clientId) {
        try {
            System.out.println("\n=== Book a Turf ===");
            viewAvailableTurfs(); // Show available turfs first

            System.out.print("\nEnter Turf ID: ");
            int turfId = scanner.nextInt();
            System.out.print("Enter Slot ID: ");
            int slotId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Get turf for price
            TurfDB turf = TurfDB.getTurfById(turfId);
            if (turf == null) {
                System.out.println("Invalid Turf ID!");
                return;
            }

            // Verify slot is available
            TimeSlotsDB slot = TimeSlotsDB.getSlotById(slotId);
            if (slot == null || !slot.isAvailable() || slot.getTurfId() != turfId) {
                System.out.println("Slot not available or doesn't match turf!");
                return;
            }

            // Show payment options
            System.out.println("\nSelect Payment Method:");
            System.out.println("1. Cash (Pay at venue)");
            System.out.println("2. Online Payment");
            System.out.print("Enter choice: ");
            int paymentChoice = scanner.nextInt();
            scanner.nextLine();

            String paymentMode;
            String paymentStatus;

            if (paymentChoice == 2) {
                paymentMode = "Online";
                paymentStatus = "Completed";
                System.out.print("Enter transaction reference: ");
                String reference = scanner.nextLine();
                // Here you would normally process payment gateway integration
                System.out.println("Online payment successful! Ref: " + reference);
            } else {
                paymentMode = "Cash";
                paymentStatus = "Pending";
            }

            // Create booking
            String bookingDate = new java.sql.Date(System.currentTimeMillis()).toString();
            int bookingId = BookingDB.createBooking(clientId, turfId, slotId, bookingDate);

            if (bookingId != -1) {
                // Create payment record
                int paymentId = PaymentDB.createPayment(
                        bookingId,
                        turf.getFeePerHour(),
                        paymentMode,
                        paymentStatus,
                        paymentChoice == 2 ? new java.sql.Date(System.currentTimeMillis()) : null
                );

                // Mark slot as booked
                TimeSlotsDB.updateSlotAvailability(slotId, false);

                // Display confirmation
                System.out.println("\nBooking Successful!");
                System.out.println("Booking ID: " + bookingId);
                System.out.println("Turf: " + turf.getTurfName());
                System.out.println("Slot: " + slot.getSlotTime());
                System.out.printf("Amount: ₹%.2f%n", turf.getFeePerHour());
                System.out.println("Payment Mode: " + paymentMode);
                System.out.println("Status: " + paymentStatus);

                if (paymentChoice == 1) {
                    System.out.println("\nPlease pay ₹" + turf.getFeePerHour() + " in cash when you arrive.");
                }
            } else {
                System.out.println("Booking failed!");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.err.println("Invalid input! Please enter numbers only.");
            scanner.nextLine(); // Clear invalid input
        }
    }


    private static void cancelBooking(int clientId) {
        try {
            System.out.println("\n=== Cancel Booking ===");

            // Show user's bookings
            List<BookingDB> bookings = BookingDB.getBookingsByClient(clientId);
            if (bookings.isEmpty()) {
                System.out.println("You have no active bookings.");
                return;
            }

            System.out.println("Your Bookings:");
            for (BookingDB booking : bookings) {
                TurfDB turf = TurfDB.getTurfById(booking.getTurfId());
                TimeSlotsDB slot = TimeSlotsDB.getSlotById(booking.getSlotId());
                System.out.printf("ID: %d | %s on %s at %s\n",
                        booking.getBookingId(),
                        turf.getTurfName(),
                        booking.getBookingDate(),
                        slot.getSlotTime());
            }

            System.out.print("\nEnter Booking ID to cancel: ");
            int bookingId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Verify booking belongs to this client
            boolean validBooking = bookings.stream()
                    .anyMatch(b -> b.getBookingId() == bookingId);

            if (validBooking) {
                BookingDB.cancelBooking(bookingId);
                System.out.println("Booking cancelled successfully!");
            } else {
                System.out.println("Invalid Booking ID!");
            }

        } catch (SQLException | InputMismatchException e) {
            System.err.println("Cancellation failed: " + e.getMessage());
            scanner.nextLine();
        }
    }

    private static void viewPaymentHistory(int clientId) {
        try {
            List<BookingDB> bookings = BookingDB.getBookingsByClient(clientId);
            if (bookings.isEmpty()) {
                System.out.println("No bookings found.");
                return;
            }

            System.out.println("\n=== Your Payment History ===");
            for (BookingDB booking : bookings) {
                PaymentDB payment = PaymentDB.getPaymentByBookingId(booking.getBookingId());
                if (payment != null) {
                    System.out.println("\nBooking ID: " + booking.getBookingId());
                    PaymentDisplayUtil.displayPayment(payment);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    private static void submitReview(int clientId) {
        try {
            System.out.println("\n=== Submit Review ===");

            // Show user's bookings
            List<BookingDB> bookings = BookingDB.getBookingsByClient(clientId);
            if (bookings == null || bookings.size() == 0) {  // Changed from isEmpty()
                System.out.println("You have no bookings to review.");
                return;
            }

            System.out.println("Your Bookings:");
            for (BookingDB booking : bookings) {
                TurfDB turf = TurfDB.getTurfById(booking.getTurfId());
                System.out.printf("ID: %d | %s on %s\n",
                        booking.getBookingId(),
                        turf.getTurfName(),
                        booking.getBookingDate());
            }

            System.out.print("\nEnter Booking ID to review: ");
            int bookingId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Validate booking belongs to client
            boolean validBooking = false;
            BookingDB selectedBooking = null;
            for (BookingDB booking : bookings) {
                if (booking.getBookingId() == bookingId) {
                    validBooking = true;
                    selectedBooking = booking;
                    break;
                }
            }

            if (!validBooking) {
                System.out.println("Invalid Booking ID!");
                return;
            }

            System.out.print("Enter rating (1-5 stars): ");
            int stars = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (stars < 1 || stars > 5) {
                System.out.println("Rating must be 1-5 stars!");
                return;
            }

            System.out.print("Enter your review comments: ");
            String reviewText = scanner.nextLine();

            // Use java.sql.Date instead of LocalDate
            java.sql.Date reviewDate = new java.sql.Date(System.currentTimeMillis());

            // Submit review
            ReviewsDB.addReview(
                    bookingId,
                    clientId,
                    selectedBooking.getTurfId(),
                    reviewText,
                    stars,
                    reviewDate.toString()
            );

            System.out.println("Thank you for your review!");
        } catch (SQLException e) {
            System.err.println("Review submission failed: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.err.println("Invalid input! Please enter numbers where required.");
            scanner.nextLine(); // Clear invalid input
        }
    }
}