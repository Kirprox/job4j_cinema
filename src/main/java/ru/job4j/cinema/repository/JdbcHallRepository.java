package ru.job4j.cinema.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.mapper.HallRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;

@Repository
public class JdbcHallRepository implements HallRepository {
    private final JdbcTemplate jdbc;

    public JdbcHallRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Hall> findById(int id) {
        String sql = "SELECT * FROM halls WHERE id = ?";
        try {
            Hall hall = jdbc.queryForObject(sql, new HallRowMapper(), id);
            return Optional.of(hall);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Hall> save(Hall hall) {
        String sql = """
                INSERT INTO halls (name, row_count, place_count, description)
                VALUES(?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, hall.getName());
            ps.setInt(2, hall.getRowCount());
            ps.setInt(3, hall.getPlaceCount());
            ps.setString(4, hall.getDescription());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key != null) {
            hall.setId(key.intValue());
            return Optional.of(hall);
        }
        return Optional.empty();
    }

    @Override
    public Collection<Hall> findAll() {
        String sql = "SELECT * FROM halls";
        return jdbc.query(sql, new HallRowMapper());
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM halls WHERE id = ?";
        jdbc.update(sql, id);
    }
}
