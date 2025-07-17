package ru.job4j.cinema.repository.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.job4j.cinema.model.FilmSession;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmSessionRowMapper implements RowMapper<FilmSession> {
    @Override
    public FilmSession mapRow(ResultSet rs, int rowNum) throws SQLException {
        FilmSession session = new FilmSession();
        session.setId(rs.getInt("id"));
        session.setFilmId(rs.getInt("film_id"));
        session.setHallsId(rs.getInt("halls_id"));
        session.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
        session.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
        session.setPrice(rs.getInt("price"));
        return session;
    }
}
