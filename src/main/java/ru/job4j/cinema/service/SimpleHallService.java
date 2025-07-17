package ru.job4j.cinema.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.HallRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class SimpleHallService implements HallService {
    private final HallRepository hallRepository;

    public SimpleHallService(@Qualifier("jdbcHallRepository") HallRepository hallRepository) {
        this.hallRepository = hallRepository;
    }

    @Override
    public Optional<Hall> getHallById(int id) {
        return hallRepository.findById(id);
    }

    @Override
    public Collection<Hall> getAllHalls() {
        return hallRepository.findAll();
    }
}
