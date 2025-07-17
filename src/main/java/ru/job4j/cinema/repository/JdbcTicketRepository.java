package ru.job4j.cinema.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.repository.mapper.TicketRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;

@Repository
public class JdbcTicketRepository implements TicketRepository {
    private final JdbcTemplate jdbc;

    public JdbcTicketRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Ticket save(Ticket ticket) {
        String sql = """
                INSERT INTO tickets (session_id, row_number, place_number, user_id)
                VALUES (?, ?, ?, ?)""";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, ticket.getSessionId());
            ps.setInt(2, ticket.getRowNumber());
            ps.setInt(3, ticket.getPlaceNumber());
            ps.setInt(4, ticket.getUserId());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw  new IllegalStateException("Generated key is null when saving ticket");
        }
        ticket.setId(key.intValue());
        return ticket;
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM tickets WHERE id = ?";
        return jdbc.update(sql, id) > 0;
    }

    @Override
    public Optional<Ticket> findById(int id) {
        String sql = "SELECT * FROM tickets WHERE id = ?";
        try {
            Ticket ticket = jdbc.queryForObject(
                    sql,
                    new TicketRowMapper(),
                    id
            );
            return Optional.of(ticket);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Ticket> findAll() {
        String sql = "SELECT * FROM tickets";
        return jdbc.query(sql, new TicketRowMapper());
    }
}
