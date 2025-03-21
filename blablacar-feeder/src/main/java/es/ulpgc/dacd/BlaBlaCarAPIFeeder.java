package es.ulpgc.dacd;

import okhttp3.*;
import java.sql.*;
import java.sql.Connection;

import com.google.gson.*;

public class BlaBlaCarAPIFeeder {

    private static final String API_URL = "https://bus-api.blablacar.com/v3/stops"; // URL real
    private static final String DB_URL = "jdbc:mysql://localhost:3306/apis_data";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "tomyjerry2025.";

    public static void main(String[] args) {
        try {
            String jsonData = fetchDataFromAPI();
            JsonArray stops = parseJson(jsonData);
            insertDataIntoDatabase(stops);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String fetchDataFromAPI() throws Exception {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer TU_CLAVE_DE_API") // Asegúrate de poner el token correcto
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Código de respuesta: " + response.code());
                System.out.println("Cuerpo de respuesta: " + response.body().string());
                throw new Exception("Error en la API: " + response);
            }
            return response.body().string();
        }
    }

    private static JsonArray parseJson(String jsonData) {
        Gson gson = new Gson();
        return gson.fromJson(jsonData, JsonArray.class);  // La API devuelve un array de estaciones
    }

    private static void insertDataIntoDatabase(JsonArray stops) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO estaciones (id, nombre, latitud, longitud, direccion) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);

            for (JsonElement element : stops) {
                JsonObject stop = element.getAsJsonObject();
                int id = stop.get("id").getAsInt();
                String nombre = stop.get("short_name").getAsString();
                double latitud = stop.get("latitude").getAsDouble();
                double longitud = stop.get("longitude").getAsDouble();
                String direccion = stop.get("address").getAsString();

                statement.setInt(1, id);
                statement.setString(2, nombre);
                statement.setDouble(3, latitud);
                statement.setDouble(4, longitud);
                statement.setString(5, direccion);

                statement.executeUpdate();
            }
            System.out.println("Datos de estaciones insertados correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

