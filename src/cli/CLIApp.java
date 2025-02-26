package cli;

import interfaces.IBookingManager;
import interfaces.ITurfManager;
import exceptions.InvalidCredentialsException;
import exceptions.TimeSlotNotAvailableException;
import exceptions.TimeSlotNotFoundException;
import exceptions.BookingNotFoundException;
import exceptions.TurfNotAvailableException;
import java.util.List;
import java.util.Scanner;

public class CLIApp {
    private static UserManager userManager = new UserManager();
    private static ITurfManager turfManager = new TurfManager();
    private static IBookingManager bookingManager = new BookingManager((TurfManager) turfManager);
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initializeData();
        while (true) {
            System.out.println("Welcome to Smart Turf Booking System");
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
                    System.exit(0);
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void initializeData() {
        // Add turfs with time slots
        Turf turf1 = new Turf("T1", "Football", "Location A");
        turf1.addTimeSlot("S1", "10:00 AM - 11:00 AM");
        turf1.addTimeSlot("S2", "11:00 AM - 12:00 PM");
        turfManager.addTurf(turf1);

        Turf turf2 = new Turf("T2", "Cricket", "Location B");
        turf2.addTimeSlot("S1", "10:00 AM - 11:00 AM");
        turf2.addTimeSlot("S2", "11:00 AM - 12:00 PM");
        turfManager.addTurf(turf2);
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

    private static void loginUser() {
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        try {
            User user = userManager.loginUser(email, password);
            System.out.println("Login Successful!");
            showTurfMenu(user);
        } catch (InvalidCredentialsException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void showTurfMenu(User user) {
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
                    bookTurf(user);
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
        List<Turf> turfs = turfManager.getAvailableTurfs();
        for (Turf turf : turfs) {
            System.out.println("Turf ID: " + turf.getTurfId() + ", Sport: " + turf.getSportType() +
                    ", Location: " + turf.getLocation());
            System.out.println("Available Time Slots:");
            for (TimeSlot slot : turf.getTimeSlots()) {
                System.out.println("  Slot ID: " + slot.getSlotId() + ", Time: " + slot.getTime() +
                        ", Available: " + slot.isAvailable());
            }
        }
    }

    private static void bookTurf(User user) {
        System.out.print("Enter Turf ID: ");
        String turfId = scanner.nextLine();
        System.out.print("Enter Slot ID: ");
        String slotId = scanner.nextLine();
        try {
            bookingManager.bookTurf(user.getEmail(), turfId, slotId);
        } catch (TimeSlotNotAvailableException | TimeSlotNotFoundException | TurfNotAvailableException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void cancelBooking() {
        System.out.print("Enter Booking ID: ");
        String bookingId = scanner.nextLine();
        try {
            bookingManager.cancelBooking(bookingId);
        } catch (BookingNotFoundException | TimeSlotNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}