package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.service.FilmService;
import ru.job4j.cinema.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FilmControllerTest {
    private FilmService filmService;
    private FilmController filmController;

    @BeforeEach
    public void initServices() {
        filmService = mock(FilmService.class);
        filmController = new FilmController(filmService);
    }

    @Test
    void whenGetAllThenReturnsFilmsListPage() {
        var film1 = new FilmDto(1, "Фильм 1", "Описание", 2023, 12, 120, "Комедия");
        var film2 = new FilmDto(2, "Фильм 2", "Описание", 2024, 16, 90, "Драма");
        var expectedFilms = List.of(film1, film2);

        when(filmService.getAllFilms()).thenReturn(expectedFilms);

        var model = new ConcurrentModel();
        var viewName = filmController.getAll(model);

        assertThat(viewName).isEqualTo("films/list");
        assertThat(model.getAttribute("films")).isEqualTo(expectedFilms);
        assertThat(model.getAttribute("activePage")).isEqualTo("films");
    }
}