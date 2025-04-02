package es.ulpgc.dacd.infrastructure.api;

import com.google.gson.*;
import es.ulpgc.dacd.domain.model.Station;
import es.ulpgc.dacd.domain.port.Stations;

import java.util.*;
import java.util.stream.Collectors;

public class BlaBlaCarStations implements Stations {
    private final BlaBlaCarAPIClient apiClient;
    private final Gson gson = new Gson();

    private static final Set<String> POPULAR_CITIES = Set.of(
            "Paris", "Lyon", "Marseille", "Toulouse", "Bordeaux",
            "Lille", "Nantes", "Strasbourg", "Brussels", "Madrid",
            "Barcelona", "Milan", "Frankfurt", "Munich"
    );

    public BlaBlaCarStations(BlaBlaCarAPIClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public List<Station> getCleanStations() {
        List<Station> stations = new ArrayList<>();
        try {
            String json = apiClient.fetchStopsJson();
            JsonArray stops = gson.fromJson(json, JsonObject.class).getAsJsonArray("stops");

            for (JsonElement el : stops) {
                JsonObject stop = el.getAsJsonObject();
                Station station = parseStation(stop);
                if (isPopularCity(station.getLongName()) && station.getAddress() != null) {
                    stations.add(station);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return removeDuplicatesById(stations);
    }

    private Station parseStation(JsonObject stop) {
        return new Station(
                stop.get("id").getAsInt(),
                getAsString(stop, "_carrier_id"),
                getAsString(stop, "short_name"),
                getAsString(stop, "long_name"),
                getAsString(stop, "time_zone"),
                getAsDouble(stop, "latitude"),
                getAsDouble(stop, "longitude"),
                stop.has("is_meta_gare") && stop.get("is_meta_gare").getAsBoolean(),
                getAsString(stop, "address")
        );
    }

    private boolean isPopularCity(String cityName) {
        return cityName != null && POPULAR_CITIES.stream()
                .anyMatch(city -> cityName.toLowerCase().contains(city.toLowerCase()));
    }

    private String getAsString(JsonObject obj, String key) {
        return obj.has(key) ? obj.get(key).getAsString() : null;
    }

    private double getAsDouble(JsonObject obj, String key) {
        return obj.has(key) ? obj.get(key).getAsDouble() : 0.0;
    }

    private List<Station> removeDuplicatesById(List<Station> stations) {
        return stations.stream()
                .collect(Collectors.toMap(
                        Station::getId,
                        s -> s,
                        (s1, s2) -> s1
                ))
                .values().stream().toList();
    }
}
