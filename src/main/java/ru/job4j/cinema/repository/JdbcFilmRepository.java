package ru.job4j.cinema.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.repository.mapper.FilmRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;

@Repository
public class JdbcFilmRepository implements FilmRepository {

    private final JdbcTemplate jdbc;

    public JdbcFilmRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Film> save(Film film) {
        String sql = """
                    INSERT INTO films (name, description, "year", genre_id,
                    minimal_age, duration_in_minutes, file_id)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setInt(3, film.getYear());
            ps.setInt(4, film.getGenreId());
            ps.setInt(5, film.getMinimalAge());
            ps.setInt(6, film.getDurationInMinutes());
            ps.setInt(7, film.getFileId());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            film.setId(key.intValue());
            return Optional.of(film);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Film> findById(int id) {
        String sql = "SELECT * FROM films WHERE id = ?";
        try {
            Film film = jdbc.queryForObject(sql, new FilmRowMapper(), id);
            return Optional.of(film);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT * FROM films";
        return jdbc.query(sql, new FilmRowMapper());
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM films WHERE id = ?";
        jdbc.update(sql, id);
    }
}
