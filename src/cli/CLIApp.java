package cli;

import Database.*;
import interfaces.IBookingManager;
import interfaces.IAdminManager;
import exceptions.*;
import java.sql.*;
import java.util.*;
import java.time.LocalDate;

public class CLIApp {
    private static UserManager userManager = new UserManager();
    private static TurfManager turfManager = new TurfManager();
    private static IBookingManager bookingManager = new BookingManager(turfManager);
    private static ReviewManager reviewManager = new ReviewManager(); // Add ReviewManager
    private static IAdminManager adminManager = new AdminManager(turfManager, bookingManager, reviewManager); // Pass ReviewManager
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
                        case 1 -> loginAdmin();
                        case 2 -> showClientMenu();
                        case 3 -> System.exit(0);
                        default -> System.out.println("Invalid choice!");
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
                System.out.println("Admin Menu");
                System.out.println("1. Add Turf");
                System.out.println("2. Delete Turf");
                System.out.println("3. View Booked Turfs");
                System.out.println("4. View All Turfs");
                System.out.println("5. Edit Turf");
                System.out.println("6. Cancel Client Booking");
                System.out.println("7. Edit Payment Status");
                System.out.println("8. Logout");
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
                            return;
                        default:
                            System.out.println("Invalid choice! Please enter a number between 1 and 8.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input! Please enter a number.");
                    scanner.nextLine(); // Clear the invalid input from the scanner
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
            System.out.print("Enter Booking ID: ");
            String bookingId = scanner.nextLine();
            System.out.print("Enter Payment Status (Pending/Completed): ");
            String status = scanner.nextLine();

            try {
                adminManager.editPaymentStatus(bookingId, status);
            } catch (BookingNotFoundException e) {
                System.out.println(e.getMessage());
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
                    case 1 -> registerUser();
                    case 2 -> {
                        int clientId = loginClient();
                        if (clientId != -1) showLoggedInClientMenu(clientId);
                    }
                    case 3 -> viewAvailableTurfs();
                    case 4 -> { return; }
                    default -> System.out.println("Invalid choice!");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a number!");
                scanner.nextLine();
            }
        }
    }

    private static void registerUser() {
        System.out.println("\n=== User Registration ===");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

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
            System.out.println("4. Submit Review");
            System.out.println("5. Logout");
            System.out.print("Enter choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1 -> viewAvailableTurfs();
                    case 2 -> bookTurf(clientId);
                    case 3 -> cancelBooking(clientId);
                    case 4 -> submitReview(clientId);
                    case 5 -> { return; }
                    default -> System.out.println("Invalid choice!");
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

            // Show available turfs first
            viewAvailableTurfs();

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

            // Create booking
            String bookingDate = java.sql.Date.valueOf(LocalDate.now()).toString();
            int bookingId = BookingDB.createBooking(clientId, turfId, slotId, bookingDate);

            // Create payment record
            PaymentDB.createPayment(
                    bookingId,
                    turf.getFeePerHour(),
                    "Cash", // Default payment mode
                    "Pending",
                    null
            );

            // Mark slot as booked
            TimeSlotsDB.updateSlotAvailability(slotId, false);

            System.out.println("\nBooking successful!");
            System.out.println("Booking ID: " + bookingId);
            System.out.printf("Amount to pay: ₹%.2f (Cash on arrival)\n", turf.getFeePerHour());

        } catch (SQLException | InputMismatchException e) {
            System.err.println("Booking failed: " + e.getMessage());
            scanner.nextLine();
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

    private static void submitReview(int clientId) {
        try {
            System.out.println("\n=== Submit Review ===");

            // Show user's past bookings
            List<BookingDB> bookings = BookingDB.getBookingsByClient(clientId);
            if (bookings.isEmpty()) {
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

            // Verify booking exists and belongs to client
            Optional<BookingDB> booking = bookings.stream()
                    .filter(b -> b.getBookingId() == bookingId)
                    .findFirst();

            if (booking.isEmpty()) {
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

            // Submit review
            ReviewsDB.addReview(
                    bookingId,
                    clientId,
                    booking.get().getTurfId(),
                    reviewText,
                    stars,
                    java.sql.Date.valueOf(LocalDate.now()).toString()
            );

            System.out.println("Thank you for your review!");

        } catch (SQLException | InputMismatchException e) {
            System.err.println("Review submission failed: " + e.getMessage());
            scanner.nextLine();
        }
    }
}