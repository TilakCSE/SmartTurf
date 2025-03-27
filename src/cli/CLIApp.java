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
    private static ReviewManager reviewManager = new ReviewManager();  // Declared but unused
    private static IAdminManager adminManager = new AdminManager(turfManager, bookingManager);
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
            System.out.println("5. Edit Payment Status");
            System.out.println("6. View All Payments");
            System.out.println("7. Edit Turf Info");
            System.out.println("8. Manage Time Slots");
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
                        editPaymentStatus();
                        break;
                    case 6:
                        viewAllPayments();
                        break;
                    case 7:
                        editTurf();
                        break;
                    case 8:
                        manageTurfTimeSlots();
                        break;
                    case 9:
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    private static void addTurf() {
        try {
            System.out.println("\n=== Add New Turf ===");
            String turfName = InputValidator.getNonEmptyString(scanner, "Turf Name: ");
            String turfType = InputValidator.getNonEmptyString(scanner, "Sport Type: ");
            String location = InputValidator.getNonEmptyString(scanner, "Location: ");
            double feePerHour = InputValidator.getValidFee(scanner);

            // Now this will work because addTurf returns an int
            int turfId = TurfDB.addTurf(turfName, turfType, location, feePerHour, 1);

            if (turfId != -1) {
                System.out.println("\n=== Add Time Slots ===");
                addTimeSlotsToTurf(turfId);
                System.out.println("Turf added successfully with ID: " + turfId);
            } else {
                System.out.println("Failed to add turf!");
            }
        } catch (SQLException e) {
            System.err.println("Error adding turf: " + e.getMessage());
        }
    }

    private static void manageTurfTimeSlots() {
        try {
            viewAllTurfs();
            System.out.print("\nEnter Turf ID to manage slots (0 to cancel): ");
            int turfId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (turfId != 0) {
                addTimeSlotsToTurf(turfId); // Reuse the same method
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    private static void deleteTurf() {
        try {
            viewAllTurfs();
            int turfId = InputValidator.getValidInt(scanner,
                    "Enter Turf ID to delete (0 to cancel): ",
                    0, Integer.MAX_VALUE);

            if (turfId == 0) {
                System.out.println("Deletion cancelled.");
                return;
            }

            if (InputValidator.getYesNoConfirmation(scanner,
                    "WARNING: This will delete ALL related bookings and time slots. Continue? (y/n)")) {

                TurfDB.deleteTurf(turfId);
                System.out.println("Turf and all related data deleted successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Deletion failed: " + e.getMessage());
        }
    }

    private static void addTimeSlotsToTurf(int turfId) throws SQLException {
        while (true) {
            System.out.println("\nCurrent Time Slots:");
            List<TimeSlotsDB> slots = TimeSlotsDB.getSlotsByTurf(turfId);
            if (slots.isEmpty()) {
                System.out.println("No slots added yet");
            } else {
                slots.forEach(slot ->
                        System.out.printf("%d: %s (%s)\n",
                                slot.getSlotId(),
                                slot.getSlotTime(),
                                slot.isAvailable() ? "Available" : "Booked"));
            }

            System.out.println("\n1. Add New Time Slot");
            System.out.println("2. Finish Adding Slots");

            int choice = InputValidator.getValidInt(scanner, "Choice: ", 1, 2);


            if (choice == 1) {
                String slotTime = InputValidator.getValidTimeSlot(
                        scanner,
                        "Enter time slot (e.g., '10:00 AM - 11:00 AM'): "
                );
                TimeSlotsDB.addTimeSlot(turfId, slotTime);
            } else {
                break;
            }
        }
    }

    private static void editTurf() {
        try {
            while (true) { // Continuous editing until user chooses to exit
                viewAllTurfs();
                System.out.print("\nEnter Turf ID to edit (0 to exit): ");
                int turfId = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (turfId == 0) break;

                TurfDB turf = TurfDB.getTurfById(turfId);
                if (turf == null) {
                    System.out.println("Invalid Turf ID!");
                    continue;
                }

                System.out.println("\nEditing Turf: " + turf.getTurfName());
                System.out.println("1. Edit Time Slots");
                System.out.println("2. Edit Basic Info");
                System.out.println("3. Back to Menu");
                System.out.print("Choice: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        editTimeSlots(turfId);
                        break;
                    case 2:
                        editTurfBasicInfo(turfId);
                        break;
                    case 3:
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
    private static void editTimeSlots(int turfId) throws SQLException {
        List<TimeSlotsDB> slots = TimeSlotsDB.getSlotsByTurf(turfId);
        System.out.println("\n=== Time Slots ===");
        for (TimeSlotsDB slot : slots) {
            System.out.printf("%d: %s (%s)\n",
                    slot.getSlotId(),
                    slot.getSlotTime(),
                    slot.isAvailable() ? "Available" : "Booked");
        }

        System.out.print("\nEnter Slot ID to toggle availability (0 to cancel): ");
        int slotId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (slotId != 0) {
            TimeSlotsDB slot = TimeSlotsDB.getSlotById(slotId);
            if (slot != null && slot.getTurfId() == turfId) {
                TimeSlotsDB.updateSlotAvailability(slotId, !slot.isAvailable());
                System.out.println("Slot availability updated!");
            } else {
                System.out.println("Invalid Slot ID for this turf!");
            }
        }
    }
    private static void editTurfBasicInfo(int turfId) throws SQLException {
        TurfDB turf = TurfDB.getTurfById(turfId);
        if (turf == null) {
            System.out.println("Turf not found!");
            return;
        }

        while (true) {
            System.out.println("\n=== Editing Turf: " + turf.getTurfName() + " ===");
            System.out.println("Current Details:");
            System.out.println("1. Name: " + turf.getTurfName());
            System.out.println("2. Sport Type: " + turf.getTurfType());
            System.out.println("3. Location: " + turf.getLocation());
            System.out.println("4. Fee/Hour: ₹" + turf.getFeePerHour());
            System.out.println("5. Back to Turf Menu");
            System.out.print("Select field to edit (1-5): ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter new name: ");
                    String newName = scanner.nextLine();
                    TurfDB.updateTurfName(turfId, newName);
                    System.out.println("Name updated successfully!");
                    break;

                case 2:
                    System.out.print("Enter new sport type: ");
                    String newType = scanner.nextLine();
                    TurfDB.updateTurfType(turfId, newType);
                    System.out.println("Sport type updated successfully!");
                    break;

                case 3:
                    System.out.print("Enter new location: ");
                    String newLocation = scanner.nextLine();
                    TurfDB.updateTurfLocation(turfId, newLocation);
                    System.out.println("Location updated successfully!");
                    break;

                case 4:
                    double newFee = InputValidator.getValidDouble(scanner,
                            "Enter new fee/hour: ₹", 0, 10000);
                    TurfDB.updateTurfFee(turfId, newFee);
                    System.out.println("Fee updated successfully!");
                    break;

                case 5:
                    return;

                default:
                    System.out.println("Invalid choice! Please enter 1-5");
            }

            // Refresh turf data after update
            turf = TurfDB.getTurfById(turfId);
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

    private static void viewBookedTurfs() {
        try {
            System.out.println("\n=== Booked Turfs ===");
            List<BookingDB> bookings = BookingDB.getAllBookings();

            if (bookings.isEmpty()) {
                System.out.println("No booked turfs found.");
                return;
            }

            System.out.printf("%-10s %-20s %-15s %-15s %-15s\n",
                    "BookingID", "Turf", "Client", "Date", "Time");

            for (BookingDB booking : bookings) {
                TurfDB turf = TurfDB.getTurfById(booking.getTurfId());
                ClientDB client = ClientDB.getClientById(booking.getClientId());
                TimeSlotsDB slot = TimeSlotsDB.getSlotById(booking.getSlotId());

                System.out.printf("%-10d %-20s %-15s %-15s %-15s\n",
                        booking.getBookingId(),
                        turf.getTurfName(),
                        client.getUserName(),
                        booking.getBookingDate(),
                        slot.getSlotTime());
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    private static void viewAllTurfs() {
        try {
            List<TurfDB> turfs = TurfDB.getAllTurfs();
            System.out.println("\n=== All Turfs ===");
            System.out.printf("%-10s %-15s %-15s %-10s %-10s\n",
                    "ID", "Sport", "Location", "Fee", "Status");

            for (TurfDB turf : turfs) {
                boolean isAvailable = TurfDB.isTurfAvailable(turf.getTurfId());
                System.out.printf("%-10d %-15s %-15s ₹%-9.2f %-10s\n",
                        turf.getTurfId(),
                        turf.getTurfType(),
                        turf.getLocation(),
                        turf.getFeePerHour(),
                        isAvailable ? "Available" : "Booked");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
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

        String username = InputValidator.getNonEmptyString(scanner, "Enter username: ");
        String email = InputValidator.getValidEmail(scanner);
        String password;
        while (true) {
            password = InputValidator.getNonEmptyString(scanner, "Enter password (min 6 chars): ");
            if (password.length() >= 6) break;
            System.out.println("Password must be at least 6 characters!");
        }
        String contact = InputValidator.getNonEmptyString(scanner, "Enter contact info: ");

        try {
            if (UsersDB.usernameExists(username)) {
                System.out.println("Username already exists!");
                return;
            }

            UsersDB.createUser(new UsersDB(0, username, password, "client"));
            UsersDB user = UsersDB.getUserByCredentials(username, password);

            if (user != null) {
                ClientDB.createClient(new ClientDB(
                        user.getUserId(),
                        username,
                        password,
                        email,
                        contact
                ));
                System.out.println("Registration successful! ID: " + user.getUserId());
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
                    case 5:  // Submit Review
                        submitReview(clientId, reviewManager);  // Pass reviewManager
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
            viewAvailableTurfs();

            int turfId = InputValidator.getValidInt(scanner, "Enter Turf ID: ", 1, Integer.MAX_VALUE);
            int slotId = InputValidator.getValidInt(scanner, "Enter Slot ID: ", 1, Integer.MAX_VALUE);

            int paymentChoice = InputValidator.getValidInt(scanner,
                    "Payment Method:\n1. Cash\n2. Online\nChoice (1-2): ", 1, 2);

            String paymentMode = (paymentChoice == 2) ? "Online" : "Cash";
            String paymentStatus = (paymentChoice == 2) ? "Completed" : "Pending";
            String transactionRef = "";

            if (paymentChoice == 2) {
                transactionRef = InputValidator.getNonEmptyString(scanner, "Enter transaction reference: ");
            }

            String bookingDate = new java.sql.Date(System.currentTimeMillis()).toString();
            int bookingId = BookingDB.createBooking(clientId, turfId, slotId, bookingDate);

            if (bookingId != -1) {
                PaymentDB.createPayment(
                        bookingId,
                        TurfDB.getTurfById(turfId).getFeePerHour(),
                        paymentMode,
                        paymentStatus,
                        (paymentChoice == 2) ? new java.sql.Date(System.currentTimeMillis()) : null
                );

                TimeSlotsDB.updateSlotAvailability(slotId, false);

                System.out.println("\n=== Booking Confirmation ===");
                System.out.println("Booking ID: " + bookingId);
                System.out.println("Turf: " + TurfDB.getTurfById(turfId).getTurfName());
                System.out.println("Slot: " + TimeSlotsDB.getSlotById(slotId).getSlotTime());
                System.out.printf("Amount: ₹%.2f%n", TurfDB.getTurfById(turfId).getFeePerHour());
                System.out.println("Payment Mode: " + paymentMode);
                System.out.println("Status: " + paymentStatus);
            }
        } catch (SQLException e) {
            System.err.println("Booking failed: " + e.getMessage());
        }
    }


    private static void cancelBooking(int clientId) {
        try {
            List<BookingDB> bookings = BookingDB.getBookingsByClient(clientId);
            if (bookings.isEmpty()) {
                System.out.println("\nNo bookings to cancel.");
                return;
            }

            // Display bookings
            bookings.forEach(b -> {
                try {
                    TurfDB turf = TurfDB.getTurfById(b.getTurfId());
                    TimeSlotsDB slot = TimeSlotsDB.getSlotById(b.getSlotId());
                    System.out.printf("%d - %s on %s at %s\n",
                            b.getBookingId(), turf.getTurfName(),
                            b.getBookingDate(), slot.getSlotTime());
                } catch (SQLException e) {
                    System.err.println("Error displaying booking: " + e.getMessage());
                }
            });

            int bookingId = InputValidator.getValidInt(scanner,
                    "Enter Booking ID to cancel (0 to abort): ",
                    0, Integer.MAX_VALUE);

            if (bookingId != 0 && bookings.stream().anyMatch(b -> b.getBookingId() == bookingId)) {
                if (InputValidator.getYesNoConfirmation(scanner, "Confirm cancellation? (y/n)")) {
                    // Get payment info before cancelling
                    PaymentDB payment = PaymentDB.getPaymentByBookingId(bookingId);

                    // Perform cancellation
                    BookingDB.cancelBooking(bookingId);

                    // Show appropriate message
                    if (payment != null) {
                        if ("Cash".equalsIgnoreCase(payment.getPaymentMode())) {
                            System.out.println("Booking cancelled successfully!");
                        } else if ("Online".equalsIgnoreCase(payment.getPaymentMode())) {
                            System.out.println("Payment will be refunded to you within 24 hours.");
                            System.out.println("Booking cancelled successfully!");
                        }
                    }
                }
            } else {
                System.out.println("Invalid Booking ID!");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
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

    private static void submitReview(int clientId, ReviewManager reviewManager) {
        try {
            // 1. Get client's bookings
            List<BookingDB> bookings = BookingDB.getBookingsByClient(clientId);
            if (bookings.isEmpty()) {
                System.out.println("You have no completed bookings to review.");
                return;
            }

            // 2. Display bookings
            System.out.println("\nYour Bookings Available for Review:");
            for (BookingDB booking : bookings) {
                TurfDB turf = TurfDB.getTurfById(booking.getTurfId());
                System.out.printf("ID: %d | %s on %s\n",
                        booking.getBookingId(), turf.getTurfName(), booking.getBookingDate());
            }

            // 3. Get valid booking ID
            System.out.print("\nEnter Booking ID to review: ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.next(); // discard invalid input
            }
            int bookingId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // 4. Validate booking belongs to client
            boolean validBooking = bookings.stream()
                    .anyMatch(b -> b.getBookingId() == bookingId);
            if (!validBooking) {
                System.out.println("Error: This booking doesn't belong to you.");
                return;
            }

            // 5. Get rating (1-5)
            System.out.print("Enter rating (1-5 stars): ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input! Please enter a number 1-5.");
                scanner.next(); // discard invalid input
            }
            int stars = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (stars < 1 || stars > 5) {
                System.out.println("Error: Rating must be between 1-5 stars.");
                return;
            }

            // 6. Get review text
            System.out.print("Enter your review comments: ");
            String reviewText = scanner.nextLine().trim();
            if (reviewText.isEmpty()) {
                System.out.println("Error: Review comments cannot be empty.");
                return;
            }

            // 7. Get the turf ID from booking
            BookingDB booking = BookingDB.getBookingById(bookingId);
            if (booking == null) {
                System.out.println("Error: Booking not found.");
                return;
            }

            // 8. Submit review
            reviewManager.addReview(
                    String.valueOf(bookingId),
                    String.valueOf(clientId),
                    String.valueOf(booking.getTurfId()),
                    reviewText,
                    stars
            );

            System.out.println("\nThank you for your review!");

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace(); // Add this for debugging
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}