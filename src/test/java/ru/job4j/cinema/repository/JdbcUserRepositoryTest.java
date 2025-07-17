package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.User;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JdbcUserRepositoryTest {
    private static JdbcUserRepository userRepository;

    @BeforeAll
    public static void initRepository() throws Exception {
        Properties properties = new Properties();
        try (InputStream inputStream = JdbcFileRepository.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        String url = properties.getProperty("datasource.url");
        String username = properties.getProperty("datasource.username");
        String password = properties.getProperty("datasource.password");
        DatasourceConfiguration configuration = new DatasourceConfiguration();
        DataSource datasource = configuration.connectionPool(url, username, password);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        userRepository = new JdbcUserRepository(jdbcTemplate);
    }

    @AfterEach
    public void clearUsers() {
        var users = userRepository.findAll();
        for (var user : users) {
            userRepository.deleteById(user.getId());
        }
    }

    @Test
    void whenSaveAtTheSameUser() {
        userRepository.save(new User(0,
                "fullName", "email@mail.com", "12344"));
        assertThrows(RuntimeException.class, () -> {
            userRepository.save(new User(0,
                    "fullName", "email@mail.com", "12344"));

        });
    }

    @Test
    void whebSaveThenGetSame() {
        var user = userRepository.save(new User(
                0, "fullName", "mail@mail.com", "12344"));
        var savedUser = userRepository.findByEmailAndPassword(
                "mail@mail.com", "12344");
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        var user1 = userRepository.save(new User(
                0, "name1", "mail@mail.com1", "12344")).get();
        var user2 = userRepository.save(new User(
                0, "name2", "mail@mail.com2", "22222")).get();
        var user3 = userRepository.save(new User(
                0, "name3", "mail@mail.com3", "13333")).get();
        var result = userRepository.findAll();
        assertThat(result).isEqualTo(List.of(user1, user2, user3));
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(userRepository.findAll()).isEqualTo(emptyList());
        assertThat(userRepository.findById(0)).isEqualTo(empty());
    }
}