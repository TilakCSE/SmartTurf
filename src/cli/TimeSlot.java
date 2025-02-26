package cli;

public class TimeSlot {
    private String slotId;
    private String time;
    private boolean isAvailable;

    public TimeSlot(String slotId, String time, boolean isAvailable) {
        this.slotId = slotId;
        this.time = time;
        this.isAvailable = isAvailable;
    }

    // Getters and Setters
    public String getSlotId() { return slotId; }
    public String getTime() { return time; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
}