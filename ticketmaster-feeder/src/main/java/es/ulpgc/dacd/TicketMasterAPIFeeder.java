package es.ulpgc.dacd;

import com.google.gson.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.sql.*;
import java.util.concurrent.*;

public class TicketMasterAPIFeeder {

    private static final String API_KEY = ConfigReader.getApiKey("TICKETMASTER_API_KEY");
    private static final String API_URL = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=" + API_KEY + "&countryCode=ES";
    private static final String DB_URL = "jdbc:sqlite:data.db";

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> {
            try {
                String jsonData = fetchDataFromAPI();
                JsonArray events = parseJson(jsonData);
                insertDataIntoDatabase(events);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.DAYS);
    }

    private static String fetchDataFromAPI() throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(API_URL).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new Exception("Error en la API: " + response);
            return response.body().string();
        }
    }

    private static JsonArray parseJson(String jsonData) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);
        JsonObject embedded = jsonObject.getAsJsonObject("_embedded");

        JsonArray events = new JsonArray();
        JsonElement eventsElement = embedded.get("events");

        if (eventsElement.isJsonArray()) {
            events = eventsElement.getAsJsonArray();
        } else if (eventsElement.isJsonObject()) {
            events.add(eventsElement.getAsJsonObject());
        }

        return events;
    }

    private static void insertDataIntoDatabase(JsonArray events) {
        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS eventos (
                    id TEXT PRIMARY KEY,
                    nombre TEXT,
                    fecha TEXT,
                    ciudad TEXT
                );
                """;

        String insertSQL = """
                INSERT OR IGNORE INTO eventos (id, nombre, fecha, ciudad)
                VALUES (?, ?, ?, ?);
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableSQL);
            }

            try (PreparedStatement statement = conn.prepareStatement(insertSQL)) {
                for (JsonElement element : events) {
                    JsonObject event = element.getAsJsonObject();

                    String id = event.get("id").getAsString();
                    String nombre = event.get("name").getAsString();
                    String fecha = event.getAsJsonObject("dates").getAsJsonObject("start").get("localDate").getAsString();
                    String ciudad = event.getAsJsonObject("_embedded")
                            .getAsJsonArray("venues").get(0).getAsJsonObject()
                            .getAsJsonObject("city").get("name").getAsString();

                    statement.setString(1, id);
                    statement.setString(2, nombre);
                    statement.setString(3, fecha);
                    statement.setString(4, ciudad);

                    statement.executeUpdate();
                }
            }

            System.out.println("Datos de eventos insertados correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
