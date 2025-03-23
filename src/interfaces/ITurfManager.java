package interfaces;

import exceptions.TurfNotAvailableException;
import java.util.List;
import cli.Turf;

public interface ITurfManager {
    void addTurf(Turf turf);
    List<cli.Turf> getAvailableTurfs();
    cli.Turf getTurfById(String turfId) throws TurfNotAvailableException;
}