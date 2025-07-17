package ru.job4j.cinema.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.FilmSessionRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SimpleFilmSessionService implements FilmSessionService {
    private final FilmSessionRepository filmSessionRepository;
    private final FilmService filmService;
    private final HallService hallService;

    public SimpleFilmSessionService(@Qualifier("jdbcFilmSessionRepository") FilmSessionRepository filmSessionRepository,
                                    FilmService filmService, HallService hallService) {
        this.filmSessionRepository = filmSessionRepository;
        this.filmService = filmService;
        this.hallService = hallService;
    }

    @Override
    public Optional<FilmSessionDto> getFilmSessionById(int id) {
        var filmSession = filmSessionRepository.findById(id);
        if (filmSession.isEmpty()) {
            return Optional.empty();
        }
        var session = filmSession.get();

        Optional<FilmDto> filmOptional = filmService.getFilmById(session.getFilmId());
        String filmName = filmOptional.map(FilmDto::getName).orElse("Фильм не найден!");
        Optional<Hall> hallOptional = hallService.getHallById(session.getHallId());
        String hallName = hallOptional.map(Hall::getName).orElse("Кинозал не найден!");
        return Optional.of(new FilmSessionDto(session.getId(), session.getFilmId(), filmName, session.getHallId(),
                hallName, session.getStartTime(),
                session.getEndTime(), session.getPrice()));
    }

    @Override
    public Collection<FilmSessionDto> getAllFilmSessions() {
        var filmSessions = filmSessionRepository.findAll();
        Collection<FilmSessionDto> resultCollection = new ArrayList<>();
        if (filmSessions.isEmpty()) {
            return Collections.emptyList();
        }

        var filmCollection = filmService.getAllFilms();
        Map<Integer, FilmDto> filmMap = filmCollection.stream()
                .collect(Collectors.toMap(FilmDto::getId, filmDto -> filmDto));
        var hallCollection = hallService.getAllHalls();
        Map<Integer, Hall> hallMap = hallCollection.stream()
                .collect(Collectors.toMap(Hall::getId, hall -> hall));

        for (FilmSession filmSession : filmSessions) {
            String filmName = filmMap.containsKey(filmSession.getFilmId())
                    ? filmMap.get(filmSession.getFilmId()).getName()
                    : "Фильм не найден!";
            String hallName = hallMap.containsKey(filmSession.getHallId())
                    ? hallMap.get(filmSession.getHallId()).getName()
                    : "Кинозал не найден!";
            resultCollection.add(new FilmSessionDto(filmSession.getId(), filmSession.getFilmId(),
                    filmName, filmSession.getHallId(), hallName, filmSession.getStartTime(),
                    filmSession.getEndTime(), filmSession.getPrice()));
        }
        return resultCollection;
    }
}
