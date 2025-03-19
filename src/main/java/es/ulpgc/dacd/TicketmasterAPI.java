package es.ulpgc.dacd;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TicketmasterAPI {
    private static final String API_KEY = ConfigReader.getApiKey("TICKETMASTER_API_KEY");
    private static final String BASE_URL = "https://app.ticketmaster.com/discovery/v2/events.json";

    public static void getEvents(String city) {
        try {
            String urlString = BASE_URL + "?city=" + city + "&apikey=" + API_KEY;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) { // HTTP OK
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println("Respuesta Ticketmaster: " + response);
            } else {
                System.err.println("Error en la solicitud: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("Error al conectar con Ticketmaster: " + e.getMessage());
        }
    }

    public static void getEventsAndSave(String city) {
        getEventsAndSave(city);
    }

    public static void main(String[] args) {
        getEvents("Madrid");
    }
}
