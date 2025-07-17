package ru.job4j.cinema.repository.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.job4j.cinema.model.Ticket;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TicketRowMapper implements RowMapper<Ticket> {
    @Override
    public Ticket mapRow(ResultSet rs, int rowNum) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setId(rs.getInt("id"));
        ticket.setSessionId(rs.getInt("session_id"));
        ticket.setRowNumber(rs.getInt("row_number"));
        ticket.setPlaceNumber(rs.getInt("place_number"));
        ticket.setUserId(rs.getInt("user_id"));
        return ticket;
    }
}
