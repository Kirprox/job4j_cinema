package ru.job4j.cinema.repository;

import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.Hall;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JdbcHallRepositoryTest {

    private static JdbcHallRepository hallRepository;
    private static JdbcFilmSessionRepository filmSessionRepository;

    @BeforeAll
    static void initRepositories() throws Exception {
        Properties properties = new Properties();
        try (InputStream inputStream = JdbcHallRepository.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        String url = properties.getProperty("datasource.url");
        String username = properties.getProperty("datasource.username");
        String password = properties.getProperty("datasource.password");

        DatasourceConfiguration configuration = new DatasourceConfiguration();
        DataSource dataSource = configuration.connectionPool(url, username, password);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        hallRepository = new JdbcHallRepository(jdbcTemplate);
        filmSessionRepository = new JdbcFilmSessionRepository(jdbcTemplate);
    }

    @AfterEach
    void clearData() {
        // Сначала удаляем связанные film_sessions
        filmSessionRepository.findAll()
                .forEach(fs -> filmSessionRepository.deleteById(fs.getId()));
        // Затем очищаем halls
        hallRepository.findAll()
                .forEach(h -> hallRepository.deleteById(h.getId()));
    }

    @Test
    @Order(1)
    void whenSaveThenFindByIdReturnsSame() {
        var hall = new Hall(0, "Test Hall", 5, 10, "Test Description");
        var saved = hallRepository.save(hall).orElseThrow();

        var found = hallRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get()).usingRecursiveComparison().isEqualTo(saved);
    }

    @Test
    @Order(2)
    void whenSaveMultipleThenFindAllReturnsThem() {
        var hall1 = new Hall(0, "Hall 1", 5, 10, "Desc 1");
        var hall2 = new Hall(0, "Hall 2", 6, 12, "Desc 2");

        hallRepository.save(hall1);
        hallRepository.save(hall2);

        var all = hallRepository.findAll();

        assertThat(all).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @Order(3)
    void whenDeleteThenNotFound() {
        var hall = new Hall(0, "To Delete", 4, 8, "For test");
        var saved = hallRepository.save(hall).orElseThrow();

        hallRepository.deleteById(saved.getId());

        assertThat(hallRepository.findById(saved.getId())).isEmpty();
    }
}