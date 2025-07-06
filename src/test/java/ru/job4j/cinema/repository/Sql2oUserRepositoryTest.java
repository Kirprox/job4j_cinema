package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2oException;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.User;

import java.util.List;
import java.util.Properties;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Sql2oUserRepositoryTest {
    private static Sql2oUserRepository sql2oUserRepository;

    @BeforeAll
    public static void initRepository() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepositoryTest.class
                .getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearUsers() {
        var users = sql2oUserRepository.findAll();
        for (var user : users) {
            sql2oUserRepository.deleteById(user.getId());
        }
    }

    @Test
    void whenSaveAtTheSameUser() {
        sql2oUserRepository.save(new User(0,
                "fullName", "email@mail.com", "12344"));
        assertThrows(Sql2oException.class, () -> {
            sql2oUserRepository.save(new User(0,
                    "fullName", "email@mail.com", "12344"));

        });
    }

    @Test
    void whebSaveThenGetSame() {
        var user = sql2oUserRepository.save(new User(
                0, "fullName", "mail@mail.com", "12344"));
        var savedUser = sql2oUserRepository.findByEmailAndPassword(
                "mail@mail.com", "12344");
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        var user1 = sql2oUserRepository.save(new User(
                0, "name1", "mail@mail.com1", "12344")).get();
        var user2 = sql2oUserRepository.save(new User(
                0, "name2", "mail@mail.com2", "22222")).get();
        var user3 = sql2oUserRepository.save(new User(
                0, "name3", "mail@mail.com3", "13333")).get();
        var result = sql2oUserRepository.findAll();
        assertThat(result).isEqualTo(List.of(user1, user2, user3));
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oUserRepository.findAll()).isEqualTo(emptyList());
        assertThat(sql2oUserRepository.findById(0)).isEqualTo(empty());
    }
}