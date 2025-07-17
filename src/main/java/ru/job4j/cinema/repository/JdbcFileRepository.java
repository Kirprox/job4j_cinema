package ru.job4j.cinema.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.job4j.cinema.model.File;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcFileRepository implements FileRepository {
    private final JdbcTemplate jdbc;

    public JdbcFileRepository(JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbcTemplate;
    }

    @Override
    public Optional<File> save(File file) {
        String sql = "INSERT INTO files (name, path) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, file.getName());
            ps.setString(2, file.getPath());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key != null) {
            file.setId(key.intValue());
            return Optional.of(file);
        }
        return Optional.empty();
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM files WHERE id = ?";
        jdbc.update(sql, id);
    }

    @Override
    public Optional<File> findById(int id) {
        String sql = "SELECT * FROM files WHERE id = ?";
        try {
            File file = jdbc.queryForObject(
                    sql,
                    new BeanPropertyRowMapper<>(File.class),
                    id
            );
            return Optional.of(file);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<File> findAll() {
        String sql = "SELECT * FROM files";
        List<File> files = jdbc.query(sql, new BeanPropertyRowMapper<>(File.class));
        return files;
    }
}
