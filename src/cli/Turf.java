package cli;

public class Turf {
    private final String turfId;
    private final String sportType;
    private final String location;
    private boolean isAvailable;

    public Turf(String turfId, String sportType, String location, boolean isAvailable) {
        this.turfId = turfId;
        this.sportType = sportType;
        this.location = location;
        this.isAvailable = isAvailable;
    }

    // Getters and Setters
    public String getTurfId() { return turfId; }
    public String getSportType() { return sportType; }
    public String getLocation() { return location; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
}