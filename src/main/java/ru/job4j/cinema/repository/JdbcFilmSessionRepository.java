package ru.job4j.cinema.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.repository.mapper.FilmSessionRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Optional;

@Repository
public class JdbcFilmSessionRepository implements FilmSessionRepository {
    private final JdbcTemplate jdbc;

    public JdbcFilmSessionRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<FilmSession> findById(int id) {
        String sql = "Select * FROM film_sessions WHERE id = ?";
        try {
            FilmSession session = jdbc.queryForObject(sql, new FilmSessionRowMapper(), id);
            return Optional.of(session);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<FilmSession> findAll() {
        String sql = "SELECT * FROM film_sessions";
        return jdbc.query(sql, new FilmSessionRowMapper());
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM film_sessions WHERE id = ?";
        jdbc.update(sql, id);
    }

    @Override
    public FilmSession save(FilmSession filmSession) {
        String sql = """
                    INSERT INTO film_sessions (
                        film_id, halls_id, start_time, end_time, price)
                    VALUES (?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, filmSession.getFilmId());
            ps.setInt(2, filmSession.getHallId());
            ps.setTimestamp(3, Timestamp.valueOf(filmSession.getStartTime()));
            ps.setTimestamp(4, Timestamp.valueOf(filmSession.getEndTime()));
            ps.setInt(5, filmSession.getPrice());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Generated key is null when saving FilmSession");
        }
        filmSession.setId(key.intValue());
        return filmSession;
    }
}
