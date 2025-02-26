package exceptions;

public class TimeSlotNotAvailableException extends Exception {
    public TimeSlotNotAvailableException(String message) {
        super(message);
    }
}