package es.ulpgc.dacd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:database.db";

    public static void createTables() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            // Tabla para los viajes de BlaBlaCar
            String createTripsTable = "CREATE TABLE IF NOT EXISTS viajes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "origen TEXT, " +
                    "destino TEXT, " +
                    "precio REAL, " +
                    "fecha_salida TEXT)";
            stmt.execute(createTripsTable);

            // Tabla para los eventos de Ticketmaster
            String createEventsTable = "CREATE TABLE IF NOT EXISTS eventos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre TEXT, " +
                    "ciudad TEXT, " +
                    "fecha TEXT, " +
                    "precio_min REAL, " +
                    "precio_max REAL)";
            stmt.execute(createEventsTable);

            System.out.println("Tablas creadas correctamente.");

        } catch (SQLException e) {
            System.err.println("Error al crear las tablas: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
