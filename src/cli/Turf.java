package cli;

import java.util.ArrayList;
import java.util.List;

public class Turf {
    private String turfId;
    private String sportType;
    private String location;
    private boolean isAvailable;
    private double feePerHour; // Fee per hour for this turf
    private List<TimeSlot> timeSlots;

    // Constructor with feePerHour
    public Turf(String turfId, String sportType, String location, double feePerHour) {
        this.turfId = turfId;
        this.sportType = sportType;
        this.location = location;
        this.feePerHour = feePerHour;
        this.isAvailable = true; // Turf is available by default
        this.timeSlots = new ArrayList<>();
    }

    // Getters and Setters
    public String getTurfId() { return turfId; }
    public void setTurfId(String turfId) { this.turfId = turfId; } // Add setter for turfId

    public String getSportType() { return sportType; }
    public void setSportType(String sportType) { this.sportType = sportType; } // Add setter for sportType

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; } // Add setter for location

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public double getFeePerHour() { return feePerHour; }
    public void setFeePerHour(double feePerHour) { this.feePerHour = feePerHour; } // Add setter for feePerHour

    public List<TimeSlot> getTimeSlots() { return timeSlots; }

    // Add a time slot
    public void addTimeSlot(String slotId, String time) {
        timeSlots.add(new TimeSlot(slotId, time, true));
    }

    // Get a time slot by ID
    public TimeSlot getTimeSlotById(String slotId) {
        for (TimeSlot slot : timeSlots) {
            if (slot.getSlotId().equals(slotId)) {
                return slot;
            }
        }
        return null;
    }

    // Check if all time slots are booked
    public boolean areAllTimeSlotsBooked() {
        for (TimeSlot slot : timeSlots) {
            if (slot.isAvailable()) {
                return false; // At least one time slot is available
            }
        }
        return true; // All time slots are booked
    }
}