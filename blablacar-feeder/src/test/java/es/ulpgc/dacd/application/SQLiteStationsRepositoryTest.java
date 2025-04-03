package es.ulpgc.dacd.application;
import es.ulpgc.dacd.domain.model.Station;
import es.ulpgc.dacd.domain.port.StationsRepository;
import es.ulpgc.dacd.infrastructure.adapter.SQLiteStationsRepository;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SQLiteStationsRepositoryTest {

    private static final String TEST_DB = "jdbc:sqlite:test.db";
    private StationsRepository repository;

    @BeforeEach
    public void setup() {
        // Eliminar la base de datos de test si ya existe
        File dbFile = new File("test.db");
        if (dbFile.exists()) dbFile.delete();

        repository = new SQLiteStationsRepository(TEST_DB);
    }

    @Test
    public void testSaveAndRetrieveSingleStation() {
        Station station = createSampleStation(1);
        repository.save(station);

        List<Station> result = repository.findAll();

        assertEquals(1, result.size());
        assertStationsEqual(station, result.get(0));
    }

    @Test
    public void testSaveAllAndRetrieveMultipleStations() {
        List<Station> stations = List.of(
                createSampleStation(1),
                createSampleStation(2),
                createSampleStation(3)
        );

        repository.saveAll(stations);
        List<Station> result = repository.findAll();

        assertEquals(3, result.size());
    }

    @Test
    public void testDuplicateInsertIsIgnored() {
        Station station = createSampleStation(1);
        repository.save(station);
        repository.save(station); // Intentamos insertar el mismo dos veces

        List<Station> result = repository.findAll();
        assertEquals(1, result.size()); // Solo debe haber uno
    }

    // Métodos auxiliares

    private Station createSampleStation(int id) {
        return new Station(
                id,
                "Carrier" + id,
                "Short" + id,
                "Long" + id,
                "Europe/Madrid",
                28.1 + id,
                -15.4 - id,
                id % 2 == 0,
                "Address " + id
        );
    }

    private void assertStationsEqual(Station expected, Station actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCarrierId(), actual.getCarrierId());
        assertEquals(expected.getShortName(), actual.getShortName());
        assertEquals(expected.getLongName(), actual.getLongName());
        assertEquals(expected.getTimeZone(), actual.getTimeZone());
        assertEquals(expected.getLatitude(), actual.getLatitude(), 0.001);
        assertEquals(expected.getLongitude(), actual.getLongitude(), 0.001);
        assertEquals(expected.isMetaGare(), actual.isMetaGare());
        assertEquals(expected.getAddress(), actual.getAddress());
    }
}

