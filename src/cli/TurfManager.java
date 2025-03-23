package cli;

import exceptions.TurfNotAvailableException;
import java.util.ArrayList;
import java.util.List;

public class TurfManager {
    private List<Turf> turfs;

    public TurfManager() {
        turfs = new ArrayList<>();
        initializeTurfs();
    }

    private void initializeTurfs() {
        // Add turfs with fees
        Turf turf1 = new Turf("T1", "Football", "Location A", 1100); // Fee: 1100 per hour
        turf1.addTimeSlot("S1", "10:00 AM - 11:00 AM");
        turf1.addTimeSlot("S2", "11:00 AM - 12:00 PM");
        turfs.add(turf1);

        Turf turf2 = new Turf("T2", "Cricket", "Location B", 1200); // Fee: 1200 per hour
        turf2.addTimeSlot("S1", "10:00 AM - 11:00 AM");
        turf2.addTimeSlot("S2", "11:00 AM - 12:00 PM");
        turfs.add(turf2);

        Turf turf3 = new Turf("T3", "Tennis", "Location C", 1300); // Fee: 1300 per hour
        turf3.addTimeSlot("S1", "10:00 AM - 11:00 AM");
        turf3.addTimeSlot("S2", "11:00 AM - 12:00 PM");
        turfs.add(turf3);
    }

    // Add a turf
    public void addTurf(Turf turf) throws TurfNotAvailableException {
        // Check if turf ID already exists
        for (Turf existingTurf : turfs) {
            if (existingTurf.getTurfId().equals(turf.getTurfId())) {
                throw new TurfNotAvailableException("Turf ID already exists!");
            }
        }
        turfs.add(turf);
    }

    // Delete a turf
    public void deleteTurf(Turf turf) {
        turfs.remove(turf);
    }

    // Get turf by ID
    public Turf getTurfById(String turfId) throws TurfNotAvailableException {
        for (Turf turf : turfs) {
            if (turf.getTurfId().equals(turfId)) {
                return turf;
            }
        }
        throw new TurfNotAvailableException("Turf not found!");
    }

    // Get all available turfs
    public List<Turf> getAvailableTurfs() {
        List<Turf> availableTurfs = new ArrayList<>();
        for (Turf turf : turfs) {
            if (turf.isAvailable()) {
                availableTurfs.add(turf);
            }
        }
        return availableTurfs;
    }

    // Get all turfs (for admin view)
    public List<Turf> getAllTurfs() {
        return turfs;
    }
}