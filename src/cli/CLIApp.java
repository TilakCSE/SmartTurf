package cli;

import interfaces.IBookingManager;
import interfaces.IAdminManager;
import exceptions.InvalidCredentialsException;
import exceptions.TimeSlotNotAvailableException;
import exceptions.TimeSlotNotFoundException;
import exceptions.BookingNotFoundException;
import exceptions.TurfNotAvailableException;
import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;

public class CLIApp {
    private static UserManager userManager = new UserManager();
    private static TurfManager turfManager = new TurfManager();
    private static IBookingManager bookingManager = new BookingManager(turfManager);
    private static ReviewManager reviewManager = new ReviewManager(); // Add ReviewManager
    private static IAdminManager adminManager = new AdminManager(turfManager, bookingManager, reviewManager); // Pass ReviewManager
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("Welcome to Smart Turf Booking System");
            System.out.println("1. Login as Admin");
            System.out.println("2. Proceed as Client");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        loginAdmin();
                        break;
                    case 2:
                        showClientMenu(); // Directly show the client menu
                        break;
                    case 3:
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice! Please enter a number between 1 and 3.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine(); // Clear the invalid input from the scanner
            }
        }
    }

    private static void loginAdmin() {
        System.out.print("Enter admin username: ");
        String username = scanner.nextLine();
        System.out.print("Enter admin password: ");
        String password = scanner.nextLine();

        // Hardcoded admin credentials for simplicity
        if (username.equals("admin") && password.equals("admin123")) {
            System.out.println("Admin login successful!");
            showAdminMenu();
        } else {
            System.out.println("Invalid admin credentials!");
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
            System.out.println("Client Menu");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. View Available Turfs");
            System.out.println("4. Book a Turf");
            System.out.println("5. Cancel Booking");
            System.out.println("6. Go Back");
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        registerUser();
                        break;
                    case 2:
                        loginClient();
                        break;
                    case 3:
                        viewTurfs();
                        break;
                    case 4:
                        bookTurf(null); // Pass null since the user is not logged in yet
                        break;
                    case 5:
                        cancelBooking();
                        break;
                    case 6:
                        return; // Go back to the main menu
                    default:
                        System.out.println("Invalid choice! Please enter a number between 1 and 6.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine(); // Clear the invalid input from the scanner
            }
        }
    }

    private static void registerUser() {
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        userManager.registerUser(name, email, password);
        System.out.println("Registration Successful!");
    }

    private static void loginClient() {
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        try {
            User user = userManager.loginUser(email, password);
            System.out.println("Client login successful!");
            showLoggedInClientMenu(user); // Show the logged-in client menu
        } catch (InvalidCredentialsException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void showLoggedInClientMenu(User user) {
        while (true) {
            System.out.println("Logged-In Client Menu");
            System.out.println("1. View Available Turfs");
            System.out.println("2. Book a Turf");
            System.out.println("3. Cancel Booking");
            System.out.println("4. Submit Review");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        viewTurfs();
                        break;
                    case 2:
                        bookTurf(user);
                        break;
                    case 3:
                        cancelBooking();
                        break;
                    case 4:
                        submitReview(user);
                        break;
                    case 5:
                        return; // Logout and go back to the client menu
                    default:
                        System.out.println("Invalid choice! Please enter a number between 1 and 5.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine(); // Clear the invalid input from the scanner
            }
        }
    }

    private static void viewTurfs() {
        List<Turf> turfs = turfManager.getAvailableTurfs();
        if (turfs.isEmpty()) {
            System.out.println("No turfs available.");
        } else {
            System.out.println("Available Turfs:");
            for (Turf turf : turfs) {
                System.out.println("Turf ID: " + turf.getTurfId() +
                        ", Sport: " + turf.getSportType() +
                        ", Location: " + turf.getLocation() +
                        ", Fee per Hour: ₹" + turf.getFeePerHour() +
                        ", Available: " + turf.isAvailable());

                // Display timeslots
                List<TimeSlot> timeSlots = turf.getTimeSlots();
                if (timeSlots.isEmpty()) {
                    System.out.println("  No timeslots available.");
                } else {
                    System.out.println("  Timeslots:");
                    for (TimeSlot slot : timeSlots) {
                        System.out.println("    Slot ID: " + slot.getSlotId() +
                                ", Time: " + slot.getTime() +
                                ", Available: " + slot.isAvailable());
                    }
                }
            }
        }
    }

    private static void bookTurf(User user) {
        if (user == null) {
            System.out.println("You must be logged in to book a turf.");
            return;
        }

        System.out.print("Enter Turf ID: ");
        String turfId = scanner.nextLine();
        System.out.print("Enter Slot ID: ");
        String slotId = scanner.nextLine();

        try {
            // Get the turf to check the fee
            Turf turf = turfManager.getTurfById(turfId);
            double feePerHour = turf.getFeePerHour();

            // Ask for payment mode
            String paymentMode;
            while (true) {
                System.out.print("Enter Payment Mode (Cash/UPI/Credit Card): ");
                paymentMode = scanner.nextLine().trim();

                // Validate payment mode
                if (paymentMode.equalsIgnoreCase("Cash") ||
                        paymentMode.equalsIgnoreCase("UPI") ||
                        paymentMode.equalsIgnoreCase("Credit Card")) {
                    break; // Valid payment mode
                } else {
                    System.out.println("Invalid payment mode! Please choose Cash, UPI, or Credit Card.");
                }
            }

            // Book the turf
            bookingManager.bookTurf(user.getEmail(), turfId, slotId, paymentMode);

            // Generate and display the booking ID
            String bookingId = bookingManager.getLatestBookingId();
            System.out.println("Booking Successful! Your Booking ID: " + bookingId);

            // Show payment message
            System.out.println("Payment of ₹" + feePerHour + " will be collected at the venue.");
            System.out.println("We do not accept advance payments for now.");
        } catch (TurfNotAvailableException | TimeSlotNotAvailableException | TimeSlotNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void cancelBooking() {
        System.out.print("Enter Booking ID: ");
        String bookingId = scanner.nextLine();
        try {
            bookingManager.cancelBooking(bookingId);
            System.out.println("Booking Cancelled Successfully!");
        } catch (TimeSlotNotFoundException | BookingNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void submitReview(User user) {
        System.out.print("Enter Booking ID: ");
        String bookingId = scanner.nextLine();

        try {
            // Check if the booking exists
            Booking booking = bookingManager.getBookingById(bookingId);

            // Check if the booking belongs to the logged-in user
            if (!booking.getUserEmail().equals(user.getEmail())) {
                System.out.println("You can only submit reviews for your own bookings.");
                return;
            }

            // Ask for review details
            System.out.print("Enter Stars (1-5): ");
            int stars = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            if (stars < 1 || stars > 5) {
                System.out.println("Invalid stars! Please enter a number between 1 and 5.");
                return;
            }

            System.out.print("Enter Review Text: ");
            String reviewText = scanner.nextLine();

            // Add the review
            reviewManager.addReview(bookingId, booking.getTurfId(), user.getEmail(), stars, reviewText);
            System.out.println("Review submitted successfully!");
        } catch (BookingNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}