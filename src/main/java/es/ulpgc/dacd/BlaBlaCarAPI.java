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
            URL url = new URL(BASE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Token " + API_KEY); // Autenticación correcta
            conn.setRequestProperty("Accept-Encoding", "gzip"); // Pedimos respuesta comprimida
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) { // HTTP OK
                // Comprobamos si la respuesta está comprimida
                String encoding = conn.getContentEncoding();
                BufferedReader in;

                if ("gzip".equalsIgnoreCase(encoding)) {
                    in = new BufferedReader(new InputStreamReader(new GZIPInputStream(conn.getInputStream())));
                } else {
                    in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                }

                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println("Respuesta BlaBlaCar: " + response.toString());
            } else {
                System.err.println("Error en la solicitud: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("Error al conectar con BlaBlaCar: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        getStops();
    }
}
