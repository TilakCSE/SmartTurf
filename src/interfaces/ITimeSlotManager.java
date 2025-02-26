package interfaces;

import exceptions.TimeSlotNotAvailableException;
import exceptions.TimeSlotNotFoundException;
import java.util.List;

public interface ITimeSlotManager {
    void bookTimeSlot(String slotId) throws TimeSlotNotAvailableException, TimeSlotNotFoundException;
    void cancelTimeSlot(String slotId) throws TimeSlotNotFoundException;
    List<cli.TimeSlot> getAvailableTimeSlots();
}