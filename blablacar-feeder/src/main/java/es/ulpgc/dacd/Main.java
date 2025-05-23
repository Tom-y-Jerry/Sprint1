package es.ulpgc.dacd;

import es.ulpgc.dacd.domain.port.Stations;
import es.ulpgc.dacd.domain.port.StationsRepository;
import es.ulpgc.dacd.infrastructure.api.ApiKeyLoader;
import es.ulpgc.dacd.infrastructure.api.BlaBlaCarAPIClient;
import es.ulpgc.dacd.infrastructure.api.BlaBlaCarStations;
import es.ulpgc.dacd.infrastructure.adapter.SQLiteStationsRepository;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final String DB_URL = "jdbc:sqlite:data.db";

    public static void main(String[] args) {
        String apiKey = ApiKeyLoader.loadApiKey("C:\\Users\\lucia\\OneDrive - Universidad de Las Palmas de Gran Canaria\\segundo\\apikeyblablacar.txt");

        BlaBlaCarAPIClient apiClient = new BlaBlaCarAPIClient(apiKey);
        Stations stations = new BlaBlaCarStations(apiClient);
        StationsRepository repository = new SQLiteStationsRepository(DB_URL);
        BlaBlaCarServiceController service = new BlaBlaCarServiceController(stations, repository);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(service::run, 0, 1, TimeUnit.HOURS);

    }
}