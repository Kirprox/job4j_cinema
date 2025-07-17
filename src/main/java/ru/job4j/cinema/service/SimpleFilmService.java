package ru.job4j.cinema.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.FilmRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SimpleFilmService implements FilmService {
    private final FilmRepository filmRepository;
    private final GenreService genreService;

    public SimpleFilmService(@Qualifier("jdbcFilmRepository") FilmRepository filmRepository, GenreService genreService) {
        this.filmRepository = filmRepository;
        this.genreService = genreService;
    }

    @Override
    public Optional<FilmDto> getFilmById(int id) {
        Optional<Film> filmOptional = filmRepository.findById(id);
        if (filmOptional.isEmpty()) {
            return Optional.empty();
        }
        var film = filmOptional.get();

        Optional<Genre> genre = genreService.getGenreById(film.getGenreId());
        String genreName = genre.map(Genre::getName).orElse("Жанр не найден!");
        return Optional.of(new FilmDto(film.getId(), film.getName(),
                film.getDescription(), film.getYear(), film.getMinimalAge(),
                film.getDurationInMinutes(), genreName));
    }

    @Override
    public Collection<FilmDto> getAllFilms() {
        var filmsCollection = filmRepository.findAll();
        Collection<FilmDto> resultCollection = new ArrayList<>();
        if (filmsCollection.isEmpty()) {
            return Collections.emptyList();
        }

        var genreCollection = genreService.getAllGenres();
        Map<Integer, Genre> genreMap = genreCollection.stream()
                .collect(Collectors.toMap(Genre::getId, genre -> genre));
        for (Film film : filmsCollection) {
            String genreName = genreMap.containsKey(film.getGenreId())
                    ? genreMap.get(film.getGenreId()).getName()
                    : "Жанр не найден!";
            resultCollection.add(new FilmDto(film.getId(), film.getName(),
                    film.getDescription(), film.getYear(), film.getMinimalAge(),
                    film.getDurationInMinutes(), genreName));
        }
        return resultCollection;
    }
}
