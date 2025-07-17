package ru.job4j.cinema.repository.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.job4j.cinema.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setYear(rs.getInt("year"));
        film.setGenreId(rs.getInt("genre_id"));
        film.setMinimalAge(rs.getInt("minimal_age"));
        film.setDurationInMinutes(rs.getInt("duration_in_minutes"));
        film.setFileId(rs.getInt("file_id"));
        return film;
    }
}
