package ru.job4j.cinema.repository;

import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.cinema.model.Film;

import java.util.Collection;
import java.util.Optional;

@Repository
public class Sql2oFilmRepository implements FilmRepository {
    private final Sql2o sql2o;

    public Sql2oFilmRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<Film> save(Film film) {
        try (var connection = sql2o.open()) {
            var sql = """
            INSERT INTO films (name, description, "year", genre_id,
            minimal_age, duration_in_minutes, file_id)
            VALUES (:name, :description, :year, :genreId,
            :minimalAge, :durationInMinutes, :fileId)
            """;
            var query = connection.createQuery(sql, true)
                    .addParameter("name", film.getName())
                    .addParameter("description", film.getDescription())
                    .addParameter("year", film.getYear())
                    .addParameter("genreId", film.getGenreId())
                    .addParameter("minimalAge", film.getMinimalAge())
                    .addParameter("durationInMinutes", film.getDurationInMinutes())
                    .addParameter("fileId", film.getFileId());

            int generatedId = query.executeUpdate().getKey(Integer.class);
            film.setId(generatedId);
            return Optional.of(film);
        } catch (Sql2oException e) {
            throw new Sql2oException("Failed to save film", e);
        }
    }

    @Override
    public Optional<Film> findById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM films WHERE id = :id");
            query.addParameter("id", id);
            var film = query.setColumnMappings(Film.COLUMN_MAPPING)
                    .executeAndFetchFirst(Film.class);
            return Optional.ofNullable(film);
        }
    }

    @Override
    public Collection<Film> findAll() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM films")
                    .setColumnMappings(Film.COLUMN_MAPPING)
                    .executeAndFetch(Film.class);
            return query;
        }
    }

    @Override
    public void deleteById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("DELETE FROM films WHERE id = :id")
                    .addParameter("id", id)
                    .executeUpdate();
        }
    }
}
