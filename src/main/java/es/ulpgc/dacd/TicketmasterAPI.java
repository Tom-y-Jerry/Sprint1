package es.ulpgc.dacd;

import com.google.gson.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;

public class TicketMasterAPI {
    private static final String API_KEY = ConfigReader.getApiKey("TICKETMASTER_API_KEY");
    private static final String BASE_URL = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=" + API_KEY + "&city=Madrid";
    private static final String DB_URL = "jdbc:sqlite:data.db";

    public static void main(String[] args) {
        try {
            String jsonData = fetchEvents();
            JsonArray events = parseEvents(jsonData);
            saveToDatabase(events);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String fetchEvents() throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Error en la solicitud: " + conn.getResponseCode());
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            return in.lines().reduce("", (a, b) -> a + b);
        }
    }

    private static JsonArray parseEvents(String jsonData) {
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

    private static void saveToDatabase(JsonArray events) {
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

            System.out.println("Eventos guardados correctamente en SQLite.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
