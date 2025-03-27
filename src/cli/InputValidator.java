package cli;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

public class InputValidator {
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a");

    // Numeric input within range
    public static int getValidInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int input = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                if (input >= min && input <= max) return input;
                System.out.printf("Please enter between %d-%d%n", min, max);
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Numbers only.");
                scanner.nextLine();
            }
        }
    }

    public static boolean getYesNoConfirmation(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y")) return true;
            if (input.equals("n")) return false;
            System.out.println("Please enter 'y' or 'n'");
        }
    }

    // Required string input
    public static String getNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("This field cannot be empty!");
        }
    }
    public static double getValidDouble(Scanner scanner, String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            try {
                double input = scanner.nextDouble();
                scanner.nextLine(); // Consume newline
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.printf("Please enter between ₹%.2f-₹%.2f\n", min, max);
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter numbers only.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    // Email validation
    public static String getValidEmail(Scanner scanner) {
        while (true) {
            String email = getNonEmptyString(scanner, "Enter email: ");
            if (email.matches("^\\S+@\\S+\\.\\S+$")) return email;
            System.out.println("Invalid email format!");
        }
    }

    public static String getValidTimeSlot(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.matches("^((1[0-2]|0?[1-9]):([0-5][0-9]) [AP]M) - ((1[0-2]|0?[1-9]):([0-5][0-9]) [AP]M)$")) {
                try {
                    String[] parts = input.split(" - ");
                    Date startTime = TIME_FORMAT.parse(parts[0]);
                    Date endTime = TIME_FORMAT.parse(parts[1]);

                    if (endTime.after(startTime)) {
                        return input;
                    } else {
                        System.out.println("End time must be after start time!");
                    }
                } catch (ParseException e) {
                    System.out.println("Invalid time format!");
                }
            } else {
                System.out.println("Format must be like: 10:00 AM - 11:00 PM");
            }
        }
    }

    public static double getValidFee(Scanner scanner) {
        return getValidDouble(scanner, "Fee per hour (₹): ", 100, 10000); // ₹100-10,000 range
    }




    // Future date validation
    public static Date getFutureDate(Scanner scanner) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        while (true) {
            String dateStr = getNonEmptyString(scanner, "Enter date (YYYY-MM-DD): ");
            try {
                Date date = sdf.parse(dateStr);
                if (date.after(new Date())) return date;
                System.out.println("Date must be in the future!");
            } catch (ParseException e) {
                System.out.println("Invalid date format!");
            }
        }
    }
}