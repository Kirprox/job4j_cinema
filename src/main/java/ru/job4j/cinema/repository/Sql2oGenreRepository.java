package ru.job4j.cinema.repository;

import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.cinema.model.Genre;

import java.util.Collection;
import java.util.Optional;

@Repository
public class Sql2oGenreRepository implements GenreRepository {
    private final Sql2o sql2o;

    public Sql2oGenreRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<Genre> findById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM genres WHERE id = :id");
            query.addParameter("id", id);
            var genre = query.executeAndFetchFirst(Genre.class);
            return Optional.ofNullable(genre);
        }
    }

    public Optional<Genre> save(Genre genre) {
        try (var connection = sql2o.open()) {
            var sql = """
                INSERT INTO genres (name)
                VALUES (:name)""";
            var query = connection.createQuery(sql, true)
                    .addParameter("name", genre.getName());
            int generatedId = query.executeUpdate().getKey(Integer.class);
            genre.setId(generatedId);
            return Optional.of(genre);
        } catch (Sql2oException e) {
            throw new Sql2oException("Failed to save genre " + e);
        }
    }

    @Override
    public Collection<Genre> findAll() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM genres");
            return query.executeAndFetch(Genre.class);
        }
    }

    @Override
    public void deleteById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("DELETE FROM genres WHERE id = :id");
            query.addParameter("id", id);
            query.executeUpdate();
        }
    }
}
