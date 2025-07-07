package ru.job4j.cinema.repository;

import org.junit.jupiter.api.*;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.Genre;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Sql2oGenreRepositoryTest {

    private static Sql2oGenreRepository genreRepository;
    private static Sql2o sql2o;

    @BeforeAll
    static void initRepositories() throws Exception {
        Properties properties = new Properties();
        try (InputStream input = Sql2oGenreRepositoryTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(input);
        }

        DatasourceConfiguration configuration = new DatasourceConfiguration();
        DataSource dataSource = configuration.connectionPool(
                properties.getProperty("datasource.url"),
                properties.getProperty("datasource.username"),
                properties.getProperty("datasource.password")
        );
        sql2o = configuration.databaseClient(dataSource);

        genreRepository = new Sql2oGenreRepository(sql2o);
    }

    @AfterEach
    void clearGenres() {
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