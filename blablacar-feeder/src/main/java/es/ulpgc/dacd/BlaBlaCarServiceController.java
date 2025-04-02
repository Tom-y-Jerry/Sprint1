package es.ulpgc.dacd;

import es.ulpgc.dacd.domain.model.*;
import es.ulpgc.dacd.domain.port.Stations;
import es.ulpgc.dacd.domain.port.StationsRepository;

import java.util.List;

public class BlaBlaCarServiceController {
    private final Stations stations;
    private final StationsRepository repository;

    public BlaBlaCarServiceController(Stations stations, StationsRepository repository) {
        this.stations = stations;
        this.repository = repository;
    }

    public void run() {
        System.out.println("Ejecutando BlaBlaCarService...");
        List<Station> cleanStations = stations.getCleanStations();
        System.out.println("Estaciones obtenidas: " + cleanStations.size());
        repository.saveAll(cleanStations);
        System.out.println("Datos de estaciones insertados correctamente.");
    }
}
