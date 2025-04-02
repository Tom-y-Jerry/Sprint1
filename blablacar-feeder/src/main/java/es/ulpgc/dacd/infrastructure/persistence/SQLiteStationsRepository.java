package es.ulpgc.dacd.infrastructure.persistence;

import es.ulpgc.dacd.domain.model.Station;
import es.ulpgc.dacd.domain.port.StationsRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteStationsRepository implements StationsRepository {
    private final String dbUrl;

    public SQLiteStationsRepository(String dbUrl) {
        this.dbUrl = dbUrl;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS stations (
                id INTEGER PRIMARY KEY,
                carrier_id TEXT,
                short_name TEXT,
                long_name TEXT,
                time_zone TEXT,
                latitude REAL,
                longitude REAL,
                is_meta_gare BOOLEAN,
                address TEXT
            );
        """;
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Station station) {
        saveAll(List.of(station));
    }

    @Override
    public void saveAll(List<Station> stations) {
        String sql = """
            INSERT OR IGNORE INTO stations
            (id, carrier_id, short_name, long_name, time_zone, latitude, longitude, is_meta_gare, address)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
        """;
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Station s : stations) {
                stmt.setInt(1, s.getId());
                stmt.setString(2, s.getCarrierId());
                stmt.setString(3, s.getShortName());
                stmt.setString(4, s.getLongName());
                stmt.setString(5, s.getTimeZone());
                stmt.setDouble(6, s.getLatitude());
                stmt.setDouble(7, s.getLongitude());
                stmt.setBoolean(8, s.isMetaGare());
                stmt.setString(9, s.getAddress());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Station> findAll() {
        List<Station> stations = new ArrayList<>();
        String sql = "SELECT * FROM stations";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                stations.add(new Station(
                        rs.getInt("id"),
                        rs.getString("carrier_id"),
                        rs.getString("short_name"),
                        rs.getString("long_name"),
                        rs.getString("time_zone"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getBoolean("is_meta_gare"),
                        rs.getString("address")
                ));
            }
            System.out.println("Datos de estaciones insertados correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stations;
    }
}
