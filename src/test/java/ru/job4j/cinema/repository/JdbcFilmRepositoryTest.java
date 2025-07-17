package ru.job4j.cinema.repository;

import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.MethodName.class)
class JdbcFilmRepositoryTest {

    private static JdbcFilmRepository jdbcFilmRepository;
    private static JdbcGenreRepository genreRepository;
    private static JdbcFileRepository fileRepository;

    @BeforeAll
    static void initRepositories() throws Exception {
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
        fileRepository = new JdbcFileRepository(jdbcTemplate);
        jdbcFilmRepository = new JdbcFilmRepository(jdbcTemplate);
    }

    @BeforeEach
    void prepareForeignKeys() {
        jdbcFilmRepository.findAll().forEach(f -> jdbcFilmRepository.deleteById(f.getId()));
        genreRepository.findAll().forEach(g -> genreRepository.deleteById(g.getId()));
        fileRepository.findAll().forEach(f -> fileRepository.deleteById(f.getId()));

        if (genreRepository.findAll().stream().noneMatch(g -> g.getName().equals("Thriller"))) {
            genreRepository.save(new Genre(0, "Thriller"));
        }
        if (fileRepository.findAll().stream().noneMatch(f -> f.getName().equals("poster.png"))) {
            fileRepository.save(new ru.job4j.cinema.model.File(0, "poster.png", "/images/poster.png"));
        }
    }

    @AfterEach
    void clearData() {
        jdbcFilmRepository.findAll().forEach(f -> jdbcFilmRepository.deleteById(f.getId()));
        genreRepository.findAll().forEach(g -> genreRepository.deleteById(g.getId()));
        fileRepository.findAll().forEach(f -> fileRepository.deleteById(f.getId()));
    }

    private int getGenreId() {
        return genreRepository.findAll().stream()
                .filter(g -> g.getName().equals("Thriller"))
                .findFirst()
                .map(Genre::getId)
                .orElseThrow();
    }

    private int getFileId() {
        return fileRepository.findAll().stream()
                .filter(f -> f.getName().equals("poster.png"))
                .findFirst()
                .map(ru.job4j.cinema.model.File::getId)
                .orElseThrow();
    }

    @Test
    void whenSaveThenFindByIdReturnsSame() {
        var genreId = getGenreId();
        var fileId = getFileId();

        var film = new Film(0, "Inception", "Thriller", 2010,
                genreId, 16, 120, fileId);

        var saved = jdbcFilmRepository.save(film).get();

        var found = jdbcFilmRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get()).usingRecursiveComparison().isEqualTo(saved);
    }

    @Test
    void whenSaveAndDeleteThenFindReturnsEmpty() {
        var genreId = getGenreId();
        var fileId = getFileId();

        var film = new Film(0, "Test", "Test", 2024,
                genreId, 18, 110, fileId);
        var saved = jdbcFilmRepository.save(film).get();

        jdbcFilmRepository.deleteById(saved.getId());

        assertThat(jdbcFilmRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void whenSaveMultipleThenFindAllReturnsThem() {
        var genreId = getGenreId();
        var fileId = getFileId();

        jdbcFilmRepository.save(new Film(0, "A", "A", 2020,
                genreId, 16, 100, fileId));
        jdbcFilmRepository.save(new Film(0, "B", "B", 2021,
                genreId, 18, 110, fileId));

        var all = jdbcFilmRepository.findAll();

        assertThat(all).hasSize(2);
        assertThat(all).extracting(Film::getName).containsExactlyInAnyOrder("A", "B");
    }
}
