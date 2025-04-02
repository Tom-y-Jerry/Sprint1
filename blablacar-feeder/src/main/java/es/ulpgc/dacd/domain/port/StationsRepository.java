package es.ulpgc.dacd.domain.port;

import es.ulpgc.dacd.domain.model.Station;
import java.util.List;

public interface StationsRepository {
    void save(Station station);
    void saveAll(List<Station> stations);
    List<Station> findAll();
}
