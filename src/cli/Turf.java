package cli;

import java.util.ArrayList;
import java.util.List;

public class Turf {
    private String turfId;
    private String sportType;
    private String location;
    private List<TimeSlot> timeSlots;

    public Turf(String turfId, String sportType, String location) {
        this.turfId = turfId;
        this.sportType = sportType;
        this.location = location;
        this.timeSlots = new ArrayList<>();
    }

    // Getters and Setters
    public String getTurfId() { return turfId; }
    public String getSportType() { return sportType; }
    public String getLocation() { return location; }
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
}