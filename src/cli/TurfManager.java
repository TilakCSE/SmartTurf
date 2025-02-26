package cli;

import interfaces.ITurfManager;
import exceptions.TurfNotAvailableException;
import java.util.ArrayList;
import java.util.List;

public class TurfManager implements ITurfManager {
    private List<Turf> turfs;

    public TurfManager() {
        turfs = new ArrayList<>();
    }

    @Override
    public void addTurf(Turf turf) {
        turfs.add(turf);
    }

    @Override
    public List<Turf> getAvailableTurfs() {
        List<Turf> availableTurfs = new ArrayList<>();
        for (Turf turf : turfs) {
            availableTurfs.add(turf);
        }
        return availableTurfs;
    }

    @Override
    public Turf getTurfById(String turfId) throws TurfNotAvailableException {
        for (Turf turf : turfs) {
            if (turf.getTurfId().equals(turfId)) {
                return turf;
            }
        }
        throw new TurfNotAvailableException("Turf not found!");
    }
}