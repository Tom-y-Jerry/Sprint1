package es.ulpgc.dacd;

import com.google.gson.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.zip.GZIPInputStream;

public class BlaBlaCarAPI {
    private static final String API_KEY = ConfigReader.getApiKey("BLABLACAR_API_KEY");
    private static final String BASE_URL = "https://bus-api.blablacar.com/v3/stops";
    private static final String DB_URL = "jdbc:sqlite:data.db";

    public static void main(String[] args) {
        try {
            String jsonData = fetchStopsFromAPI();
            JsonArray stops = parseStopsFromJson(jsonData);
            saveStopsToDatabase(stops);
        } catch (Exception e) {
            System.err.println("Error general: " + e.getMessage());
        }
    }

    private static String fetchStopsFromAPI() throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Token " + API_KEY);
        conn.setRequestProperty("Accept-Encoding", "gzip");
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Error en la solicitud: " + conn.getResponseCode());
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                "gzip".equalsIgnoreCase(conn.getContentEncoding())
                        ? new GZIPInputStream(conn.getInputStream())
                        : conn.getInputStream()))) {
            return in.lines().reduce("", (a, b) -> a + b);
        }
    }

    private static JsonArray parseStopsFromJson(String jsonData) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);
        return jsonObject.getAsJsonArray("stops");
    }

    private static void saveStopsToDatabase(JsonArray stops) {
        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS stations (
                    id INTEGER PRIMARY KEY,
                    carrier_id TEXT,
                    short_name TEXT,
                    long_name TEXT,
                    time_zone TEXT,
                    latitude REAL,
                    longitude REAL,
                    is_meta_gare BOOLEAN,
                    address TEXT
                );
                """;

        String insertSQL = """
                INSERT OR IGNORE INTO stations (id, carrier_id, short_name, long_name, time_zone, latitude, longitude, is_meta_gare, address)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableSQL);
            }

            try (PreparedStatement statement = conn.prepareStatement(insertSQL)) {
                for (JsonElement element : stops) {
                    JsonObject stop = element.getAsJsonObject();

                    int id = stop.get("id").getAsInt();
                    String carrierId = stop.has("_carrier_id") ? stop.get("_carrier_id").getAsString() : null;
                    String shortName = stop.has("short_name") ? stop.get("short_name").getAsString() : null;
                    String longName = stop.has("long_name") ? stop.get("long_name").getAsString() : null;
                    String timeZone = stop.has("time_zone") ? stop.get("time_zone").getAsString() : null;
                    double latitude = stop.has("latitude") ? stop.get("latitude").getAsDouble() : 0.0;
                    double longitude = stop.has("longitude") ? stop.get("longitude").getAsDouble() : 0.0;
                    boolean isMetaGare = stop.has("is_meta_gare") && stop.get("is_meta_gare").getAsBoolean();
                    String address = stop.has("address") ? stop.get("address").getAsString() : null;

                    statement.setInt(1, id);
                    statement.setString(2, carrierId);
                    statement.setString(3, shortName);
                    statement.setString(4, longName);
                    statement.setString(5, timeZone);
                    statement.setDouble(6, latitude);
                    statement.setDouble(7, longitude);
                    statement.setBoolean(8, isMetaGare);
                    statement.setString(9, address);

                    statement.executeUpdate();
                }
            }

            System.out.println("Datos de estaciones insertados correctamente en SQLite.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
