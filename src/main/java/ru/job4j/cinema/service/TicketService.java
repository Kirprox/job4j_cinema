package ru.job4j.cinema.service;

import ru.job4j.cinema.model.Ticket;

import java.util.Collection;
import java.util.Optional;

public interface TicketService {
    Optional<Ticket> getTicketById(int id);

    Collection<Ticket> getAllTickets();

    Ticket save(Ticket ticket);

    boolean deleteById(int id);
}
