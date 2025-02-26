package interfaces;

import exceptions.TurfNotAvailableException;
import java.util.List;

public interface ITurfManager {
    void addTurf(cli.Turf turf);
    List<cli.Turf> getAvailableTurfs();
    cli.Turf getTurfById(String turfId) throws TurfNotAvailableException;
}