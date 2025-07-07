package ru.job4j.cinema.repository;

import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.cinema.model.Hall;

import java.util.Collection;
import java.util.Optional;

@Repository
public class Sql2oHallRepository implements HallRepository {
    private final Sql2o sql2o;

    public Sql2oHallRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<Hall> findById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM halls WHERE id = :id");
            query.addParameter("id", id);
            var hall = query.setColumnMappings(Hall.COLUMN_MAPPING).
                    executeAndFetchFirst(Hall.class);
            return Optional.ofNullable(hall);
        }
    }

    @Override
    public Optional<Hall> save(Hall hall) {
        try (var connection = sql2o.open()) {
            var sql = """
                INSERT INTO halls (name, row_count, place_count, description)
                VALUES (:name, :rowCount, :placeCount, :description)""";
            var query = connection.createQuery(sql, true)
                    .addParameter("name", hall.getName())
                    .addParameter("rowCount", hall.getRowCount())
                    .addParameter("placeCount", hall.getPlaceCount())
                    .addParameter("description", hall.getDescription());
            int generatedId = query.executeUpdate().getKey(Integer.class);
            hall.setId(generatedId);
            return Optional.of(hall);
        } catch (Sql2oException e) {
            throw new Sql2oException("Failed to save hall " + e);
        }
    }

    @Override
    public Collection<Hall> findAll() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM halls");
            return query.setColumnMappings(Hall.COLUMN_MAPPING)
                    .executeAndFetch(Hall.class);
        }
    }

    @Override
    public void deleteById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("DELETE FROM halls WHERE id = :id")
                    .addParameter("id", id);
            query.executeUpdate();
        }
    }
}
