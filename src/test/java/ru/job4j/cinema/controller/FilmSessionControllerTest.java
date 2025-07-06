package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.service.FilmService;
import ru.job4j.cinema.service.FilmSessionService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FilmSessionControllerTest {
    private FilmSessionService filmSessionService;
    private FilmSessionController filmSessionController;

    @BeforeEach
    public void initServices() {
        filmSessionService = mock(FilmSessionService.class);
        filmSessionController = new FilmSessionController(filmSessionService);
    }

    @Test
    void whenGetSessionPageThenReturnSessionsListWithData() {
        var session1 = new FilmSessionDto(1, 1, "Фильм 1", 1, "Зал 1",
                LocalDateTime.now(), LocalDateTime.now().plusHours(2), 350);
        var session2 = new FilmSessionDto(2, 2, "Фильм 2", 2, "Зал 2",
                LocalDateTime.now(), LocalDateTime.now().plusHours(2), 400);
        var expectedSessions = List.of(session1, session2);

        when(filmSessionService.getAllFilmSessions()).thenReturn(expectedSessions);

        var model = new ConcurrentModel();
        var viewName = filmSessionController.getSessionPage(model);

        assertThat(viewName).isEqualTo("sessions/list");
        assertThat(model.getAttribute("sessions")).isEqualTo(expectedSessions);
        assertThat(model.getAttribute("activePage")).isEqualTo("sessions");
    }
}