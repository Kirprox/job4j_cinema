package ru.job4j.cinema.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.job4j.cinema.model.Genre;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;

@Repository
public class JdbcGenreRepository implements GenreRepository {
    private final JdbcTemplate jdbc;

    public JdbcGenreRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Genre> findById(int id) {
        String sql = "SELECT * FROM genres WHERE id = ?";
        try {
            Genre genre = jdbc.queryForObject(
                    sql,
                    new BeanPropertyRowMapper<>(Genre.class),
                    id
            );
            return Optional.of(genre);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Genre> save(Genre genre) {
        String sql = """
                INSERT INTO genres(name)
                VALUES(?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, genre.getName());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key != null) {
            genre.setId(key.intValue());
            return Optional.of(genre);
        }
        return Optional.empty();
    }

    @Override
    public Collection<Genre> findAll() {
        String sql = "SELECT * FROM genres";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Genre.class));
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM genres WHERE id = ?";
        jdbc.update(sql, id);
    }
}
