package es.ulpgc.dacd;
import com.google.gson.*;
import okhttp3.*;
import java.sql.*;
import java.sql.Connection;

public class TicketMasterAPIFeeder {

    private static final String API_URL = "https://app.ticketmaster.com/discovery/v2/events.json?apikey="; // Cambiar por la URL real
    private static final String DB_URL = "jdbc:mysql://localhost:3306/apis_data";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "tomyjerry2025.";

    public static void main(String[] args) {
        try {
            String jsonData = fetchDataFromAPI();
            JsonArray events = parseJson(jsonData);
            insertDataIntoDatabase(events);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        return jsonObject.getAsJsonArray("events");  // Ajustar clave según API
    }

    private static void insertDataIntoDatabase(JsonArray events) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO eventos (id, nombre, fecha, ciudad) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);

            for (JsonElement element : events) {
                JsonObject event = element.getAsJsonObject();
                String id = event.get("id").getAsString();
                String nombre = event.get("name").getAsString();
                String fecha = event.get("dates").getAsJsonObject().get("start").get("localDate").getAsString();
                String ciudad = event.get("venues").getAsJsonArray().get(0).getAsJsonObject().get("city").get("name").getAsString();

                statement.setString(1, id);
                statement.setString(2, nombre);
                statement.setString(3, fecha);
                statement.setString(4, ciudad);

                statement.executeUpdate();
            }
            System.out.println("Datos de Ticketmaster insertados correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
