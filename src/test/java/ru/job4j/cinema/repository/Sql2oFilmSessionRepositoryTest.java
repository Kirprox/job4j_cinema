package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import ru.job4j.cinema.configuration.DatasourceConfiguration;

import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

class Sql2oFilmSessionRepositoryTest {
    private static Sql2oFilmSessionRepository sql2oFilmSessionRepository;

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

        sql2oFilmSessionRepository = new Sql2oFilmSessionRepository(sql2o);
    }

    @AfterEach
    public void clearFilmSessions() {
        var filmSessions = sql2oFilmSessionRepository.findAll();
        for (var filmSession : filmSessions) {
            sql2oFilmSessionRepository.deleteById(filmSession.getId());
        }
    }
}