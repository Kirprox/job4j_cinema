package ru.job4j.cinema.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.mapper.UserRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;

@Repository
public class JdbcUserRepository implements UserRepository {
    private final JdbcTemplate jdbc;

    public JdbcUserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<User> save(User user) {
        String sql = """
                INSERT INTO users (full_name, email, password)
                VALUES (?, ?, ?)""";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key != null) {
            user.setId(key.intValue());
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        return jdbc.update(sql, id) > 0;
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try {
            User user = jdbc.queryForObject(sql, new UserRowMapper(), email, password);
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            User user = jdbc.queryForObject(sql, new UserRowMapper(), id);
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbc.query(sql, new UserRowMapper());
    }
}
