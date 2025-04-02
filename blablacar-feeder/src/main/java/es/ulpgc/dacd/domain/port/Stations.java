package es.ulpgc.dacd.domain.port;

import es.ulpgc.dacd.domain.model.Station;

import java.util.List;

public interface Stations {
    List<Station> getCleanStations();
}
