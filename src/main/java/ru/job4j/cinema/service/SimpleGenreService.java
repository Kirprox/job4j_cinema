package ru.job4j.cinema.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.GenreRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class SimpleGenreService implements GenreService {
    private final GenreRepository genreRepository;

    public SimpleGenreService(@Qualifier("jdbcGenreRepository") GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public Optional<Genre> getGenreById(int id) {
        return genreRepository.findById(id);
    }

    @Override
    public Collection<Genre> getAllGenres() {
        return genreRepository.findAll();
    }
}
