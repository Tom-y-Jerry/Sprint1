package es.ulpgc.dacd.application;

import es.ulpgc.dacd.domain.model.Station;
import es.ulpgc.dacd.domain.port.StationsRepository;
import es.ulpgc.dacd.infrastructure.persistence.SQLiteStationsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BlaBlaCarServiceTest {
    private StationsRepository repository;

    @BeforeEach
    public void setup() {
        repository = new SQLiteStationsRepository("jdbc:sqlite:data.db");
        // Limpieza inicial (opcional si quieres repetir sin duplicados)
        new File("test.db").delete();
    }

    @Test
    public void testSaveAndFindAll() {
        Station station = new Station(9999, "TEST", "Shorty", "Longy", "UTC", 2.5, 1.3, false, "Fake Avenue");

        repository.save(station);
        List<Station> results = repository.findAll();

        assertTrue(results.stream().anyMatch(s -> s.getId() == 9999));
    }
}
