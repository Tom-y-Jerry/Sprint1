package es.ulpgc.dacd;

import com.google.gson.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.sql.*;
import java.util.concurrent.*;

public class BlaBlaCarAPIFeeder {

    private static final String API_URL = "https://bus-api.blablacar.com/v3/stops";
    private static final String DB_URL = "jdbc:sqlite:data.db";
    private static final String API_KEY = ConfigReader.getApiKey("BLABLACAR_API_KEY");

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> {
            try {
                String jsonData = fetchDataFromAPI();
                JsonArray stops = parseJson(jsonData);
                insertDataIntoDatabase(stops);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.HOURS);
    }

    private static String fetchDataFromAPI() throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Token " + API_KEY)
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new Exception("Error en la API: " + response);
            return response.body().string();
        }
    }


    private static JsonArray parseJson(String jsonData) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);
        return jsonObject.getAsJsonArray("stops");
    }

    private static void insertDataIntoDatabase(JsonArray stops) {
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

            System.out.println("Datos de estaciones insertados correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}