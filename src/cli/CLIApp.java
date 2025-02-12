package cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CLIApp {
    private static List<User> users = new ArrayList<>();
    private static final List<Turf> turfs = new ArrayList<>();
    private static final List<Booking> bookings = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        loadData();
        while (true) {
            System.out.println("Welcome, User");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    loginUser();
                    break;
                case 3:
                    saveData();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice!");
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
        users.add(new User(name, email, password));
        System.out.println("Registration Successful!");
    }

    private static void loginUser() {
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                System.out.println("Login Successful!");
                showTurfMenu();
                return;
            }
        }
        System.out.println("Invalid email or password!");
    }

    private static void showTurfMenu() {
        while (true) {
            System.out.println("1. View Available Turfs");
            System.out.println("2. Book a Turf");
            System.out.println("3. Cancel Booking");
            System.out.println("4. Logout");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewTurfs();
                    break;
                case 2:
                    bookTurf();
                    break;
                case 3:
                    cancelBooking();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void viewTurfs() {
        for (Turf turf : turfs) {
            System.out.println("Turf ID: " + turf.getTurfId() + ", Sport: " + turf.getSportType() +
                    ", Location: " + turf.getLocation() + ", Available: " + turf.isAvailable());
        }
    }

    private static void bookTurf() {
        System.out.print("Enter Turf ID: ");
        String turfId = scanner.nextLine();
        System.out.print("Enter Time Slot: ");
        String timeSlot = scanner.nextLine();
        bookings.add(new Booking("B" + bookings.size(), "user@example.com", turfId, timeSlot));
        System.out.println("Booking Successful!");
    }

    private static void cancelBooking() {
        System.out.print("Enter Booking ID: ");
        String bookingId = scanner.nextLine();
        bookings.removeIf(booking -> booking.getBookingId().equals(bookingId));
        System.out.println("Booking Cancelled!");
    }

    private static void loadData() {
        users = FileHandler.loadUsers();
        // Load turfs and bookings similarly
    }

    private static void saveData() {
        FileHandler.saveUsers(users);
        // Save turfs and bookings similarly
    }
}