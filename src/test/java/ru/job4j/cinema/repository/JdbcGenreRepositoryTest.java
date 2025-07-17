package ru.job4j.cinema.repository;

import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.Genre;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JdbcGenreRepositoryTest {

    private JdbcGenreRepository genreRepository;
    private JdbcFilmRepository filmRepository;
    private JdbcFilmSessionRepository filmSessionRepository;

    @BeforeAll
    void initRepositories() throws Exception {
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

        genreRepository = new JdbcGenreRepository(jdbcTemplate);
        filmRepository = new JdbcFilmRepository(jdbcTemplate);
        filmSessionRepository = new JdbcFilmSessionRepository(jdbcTemplate);
    }

    @AfterEach
    void clearDatabase() {
        filmSessionRepository.findAll()
                .forEach(s -> filmSessionRepository.deleteById(s.getId()));

        filmRepository.findAll()
                .forEach(f -> filmRepository.deleteById(f.getId()));

        genreRepository.findAll()
                .forEach(g -> genreRepository.deleteById(g.getId()));
    }

    @Test
    @Order(1)
    void whenFindByIdNotExistingThenReturnEmpty() {
        var result = genreRepository.findById(-1);
        assertThat(result).isEmpty();
    }

    @Test
    @Order(2)
    void whenSaveThenFindByIdReturnsSame() {
        var genre = new Genre(0, "Sci-Fi");
        var saved = genreRepository.save(genre).orElseThrow();

        var found = genreRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get()).usingRecursiveComparison().isEqualTo(saved);
    }

    @Test
    @Order(3)
    void whenSaveMultipleThenFindAllReturnsThem() {
        var genre1 = genreRepository.save(new Genre(0, "Action")).orElseThrow();
        var genre2 = genreRepository.save(new Genre(0, "Comedy")).orElseThrow();

        Collection<Genre> all = genreRepository.findAll();

        assertThat(all).hasSizeGreaterThanOrEqualTo(2);
        assertThat(all).extracting(Genre::getName).contains("Action", "Comedy");
    }

    @Test
    @Order(4)
    void whenDeleteByIdThenNotFound() {
        var genre = genreRepository.save(new Genre(0, "Horror")).orElseThrow();
        genreRepository.deleteById(genre.getId());

        var found = genreRepository.findById(genre.getId());
        assertThat(found).isEmpty();
    }
}