package ru.job4j.cinema.controller;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.FilmService;
import ru.job4j.cinema.service.FilmSessionService;
import ru.job4j.cinema.service.HallService;
import ru.job4j.cinema.service.TicketService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketControllerTest {

    private TicketService ticketService;
    private FilmSessionService filmSessionService;
    private HallService hallService;
    private FilmService filmService;
    private TicketController ticketController;

    @BeforeEach
    public void initServices() {
        ticketService = mock(TicketService.class);
        filmSessionService = mock(FilmSessionService.class);
        hallService = mock(HallService.class);
        filmService = mock(FilmService.class);
        ticketController = new TicketController(ticketService, filmSessionService, hallService, filmService);
    }

    @Test
    void whenGetByIdThenReturnBuyPage() {
        int sessionId = 1;
        var user = new User(1, "user", "email", "password");

        var sessionDto = new FilmSessionDto(
                sessionId,
                2,
                "Фильм",
                3,
                "Red",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2),
                350
        );

        var hall = new Hall(3, "Red", 5, 10, "description");
        var film = new FilmDto(2, "Фильм", "Описание", 2024, 16, 120, "Комедия");

        when(filmSessionService.getFilmSessionById(sessionId)).thenReturn(Optional.of(sessionDto));
        when(hallService.getHallById(sessionDto.getHallId())).thenReturn(Optional.of(hall));
        when(filmService.getFilmById(sessionDto.getFilmId())).thenReturn(Optional.of(film));

        var model = new ConcurrentModel();
        var httpSession = mock(HttpSession.class);
        when(httpSession.getAttribute("user")).thenReturn(user);

        var view = ticketController.getById(model, sessionId, httpSession);

        assertThat(view).isEqualTo("tickets/buy");
        assertThat(model.getAttribute("session")).isEqualTo(sessionDto);
        assertThat(model.getAttribute("hall")).isEqualTo(hall);
        assertThat(model.getAttribute("film")).isEqualTo(film);
        assertThat(((Ticket) model.getAttribute("ticket")).getUserId()).isEqualTo(user.getId());
    }

    @Test
    void whenSaveTicketFailsThenRedirectWithError() {
        var ticket = new Ticket(0, 1, 1, 3, 4);
        doThrow(new RuntimeException("место занято")).when(ticketService).save(ticket);
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        var view = ticketController.saveTicket(ticket, redirectAttributes);
        assertThat(view).isEqualTo("redirect:/buy/" + ticket.getSessionId());
        Map<String, Object> flashAttributes = (Map<String, Object>) redirectAttributes.getFlashAttributes();
        assertThat(flashAttributes)
                .containsEntry("errorMessage", "Это место занято,выберите другое")
                .containsEntry("ticket", ticket);
    }

    @Test
    void whenSaveTicketSucceedsThenReturnSuccessPage() {
        var ticket = new Ticket(0, 1, 1, 3, 4);
        var redirectAttributes = new RedirectAttributesModelMap();
        var view = ticketController.saveTicket(ticket, redirectAttributes);
        verify(ticketService).save(ticket);
        assertThat(view).isEqualTo("tickets/success");
    }
}