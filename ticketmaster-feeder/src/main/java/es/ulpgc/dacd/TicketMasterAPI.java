package es.ulpgc.dacd;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TicketMasterAPI {
    private static final String API_KEY = ConfigReader.getApiKey("TICKETMASTER_API_KEY");
    private static final String BASE_URL = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=" + API_KEY;

    public static void getEvents(String city) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL + "&city=" + city).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response = in.lines().reduce("", (a, b) -> a + b);
                in.close();
                System.out.println("Respuesta Ticketmaster: " + response);
            } else {
                System.err.println("Error en la solicitud: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            System.err.println("Error al conectar con Ticketmaster: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        getEvents("Madrid");
    }
}
