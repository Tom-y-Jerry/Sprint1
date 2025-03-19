package es.ulpgc.dacd;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledTasks {
    public static void main(String[] args) {
        DatabaseManager.createTables(); // Asegurar que las tablas existen

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        // Ejecutar BlaBlaCarAPI cada 1 hora
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("🔄 Consultando BlaBlaCar...");
            BlaBlaCarAPI.getStopsAndSave();
        }, 0, 1, TimeUnit.HOURS);

        // Ejecutar TicketmasterAPI cada 1 hora
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("🔄 Consultando Ticketmaster...");
            TicketmasterAPI.getEventsAndSave("Madrid");
        }, 0, 1, TimeUnit.HOURS);
    }
}
