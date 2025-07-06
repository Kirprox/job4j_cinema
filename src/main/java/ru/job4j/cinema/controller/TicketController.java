package ru.job4j.cinema.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.*;

@Controller
@RequestMapping("/buy")
public class TicketController {
    private final TicketService ticketService;
    private final FilmSessionService filmSessionService;
    private final HallService hallService;
    private final FilmService filmService;

    public TicketController(TicketService ticketService, FilmSessionService filmSessionService, HallService hallService, FilmService filmService) {
        this.ticketService = ticketService;
        this.filmSessionService = filmSessionService;
        this.hallService = hallService;
        this.filmService = filmService;
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id, HttpSession httpSession) {
        var sessionOptional = filmSessionService.getFilmSessionById(id);
        if (sessionOptional.isEmpty()) {
            model.addAttribute("message", "Сеанс не найден");
            return "errors/404";
        }
        var session = sessionOptional.get();
        model.addAttribute("session", session);
        var hall = hallService.getHallById(session.getHallId()).get();
        model.addAttribute("hall", hall);
        var film = filmService.getFilmById(session.getFilmId()).get();
        model.addAttribute("film", film);
        var user = (User) httpSession.getAttribute("user");
        model.addAttribute("userId", user.getId());

        if (!model.containsAttribute("ticket")) {
            Ticket ticket = new Ticket();
            ticket.setSessionId(session.getId());
            ticket.setUserId(user.getId());
            model.addAttribute("ticket", ticket);
        }

        return "tickets/buy";
    }

    @PostMapping()
    public String saveTicket(@ModelAttribute Ticket ticket, RedirectAttributes redirectAttributes) {
        try {
            ticketService.save(ticket);
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("errorMessage", "Это место занято,"
                    + "выберите другое");
            redirectAttributes.addFlashAttribute("ticket", ticket);
            return "redirect:/buy/" + ticket.getSessionId();
        }
        return "tickets/success";
    }
}
