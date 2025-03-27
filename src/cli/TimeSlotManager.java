package cli;

import interfaces.ITimeSlotManager;
import exceptions.TimeSlotNotAvailableException;
import exceptions.TimeSlotNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotManager implements ITimeSlotManager {
    private Turf turf;

    public TimeSlotManager(Turf turf) {
        this.turf = turf;
    }

    @Override
    public void bookTimeSlot(String slotId) throws TimeSlotNotAvailableException, TimeSlotNotFoundException {
        TimeSlot slot = turf.getTimeSlotById(slotId);
        if (slot == null) {
            throw new TimeSlotNotFoundException("Time Slot not found!");
        }
        if (!slot.isAvailable()) {
            throw new TimeSlotNotAvailableException("Time Slot is not available!");
        }
        slot.setAvailable(false);
    }

    @Override
    public void cancelTimeSlot(String slotId) throws TimeSlotNotFoundException {
        TimeSlot slot = turf.getTimeSlotById(slotId);
        if (slot == null) {
            throw new TimeSlotNotFoundException("Time Slot not found!");
        }
        slot.setAvailable(true);
    }

    @Override
    public List<TimeSlot> getAvailableTimeSlots() {
        List<TimeSlot> availableSlots = new ArrayList<>();
        for (TimeSlot slot : turf.getTimeSlots()) {
            if (slot.isAvailable()) {
                availableSlots.add(slot);
            }
        }
        return availableSlots;
    }
}