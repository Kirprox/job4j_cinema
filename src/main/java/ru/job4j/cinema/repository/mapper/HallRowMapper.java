package ru.job4j.cinema.repository.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.job4j.cinema.model.Hall;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HallRowMapper implements RowMapper<Hall> {
    @Override
    public Hall mapRow(ResultSet rs, int rowNum) throws SQLException {
        Hall hall = new Hall();
        hall.setId(rs.getInt("id"));
        hall.setName(rs.getString("name"));
        hall.setRowCount(rs.getInt("row_count"));
        hall.setPlaceCount(rs.getInt("place_count"));
        hall.setDescription(rs.getString("description"));
        return hall;
    }
}
