package cli;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    private static final String USER_FILE = "users.txt";
    private static final String TURF_FILE = "turfs.txt";
    private static final String BOOKING_FILE = "bookings.txt";

    public static void saveUsers(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_FILE))) {
            users = (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Similar methods for Turf and Booking
}