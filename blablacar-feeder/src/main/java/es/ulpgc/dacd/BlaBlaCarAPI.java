package es.ulpgc.dacd;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

public class BlaBlaCarAPI {
    private static final String API_KEY = ConfigReader.getApiKey("BLABLACAR_API_KEY");
    private static final String BASE_URL = "https://bus-api.blablacar.com/v3/stops";

    public static void getStops() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Token " + API_KEY);
            conn.setRequestProperty("Accept-Encoding", "gzip");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        "gzip".equalsIgnoreCase(conn.getContentEncoding()) ?
                                new GZIPInputStream(conn.getInputStream()) :
                                conn.getInputStream()));
                String response = in.lines().reduce("", (a, b) -> a + b);
                in.close();
                System.out.println("Respuesta BlaBlaCar: " + response);
            } else {
                System.err.println("Error en la solicitud: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            System.err.println("Error al conectar con BlaBlaCar: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        getStops();
    }
}
