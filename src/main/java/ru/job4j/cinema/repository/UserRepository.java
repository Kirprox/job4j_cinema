package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    boolean deleteById(int id);

    Optional<User> findById(int id);

    Collection<User> findAll();
}
