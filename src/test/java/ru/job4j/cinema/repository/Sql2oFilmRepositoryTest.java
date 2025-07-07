package ru.job4j.cinema.repository;

import org.junit.jupiter.api.*;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;

import java.util.Properties;

import static org.assertj.core.api.Assertions.*;
@TestMethodOrder(MethodOrderer.MethodName.class)
class Sql2oFilmRepositoryTest {

    private static Sql2oFilmRepository sql2oFilmRepository;
    private static Sql2oGenreRepository genreRepository;
    private static Sql2oFileRepository fileRepository;
    private static Sql2o sql2o;

    @BeforeAll
    static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oFilmRepositoryTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(
                properties.getProperty("datasource.url"),
                properties.getProperty("datasource.username"),
                properties.getProperty("datasource.password")
        );

        sql2o = configuration.databaseClient(datasource);

        genreRepository = new Sql2oGenreRepository(sql2o);
        fileRepository = new Sql2oFileRepository(sql2o);
        sql2oFilmRepository = new Sql2oFilmRepository(sql2o);
    }

    @BeforeEach
    void prepareForeignKeys() {
        sql2oFilmRepository.findAll().forEach(f -> sql2oFilmRepository.deleteById(f.getId()));
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
        sql2oFilmRepository.findAll().forEach(f -> sql2oFilmRepository.deleteById(f.getId()));
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

        var saved = sql2oFilmRepository.save(film).get();

        var found = sql2oFilmRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get()).usingRecursiveComparison().isEqualTo(saved);
    }

    @Test
    void whenSaveAndDeleteThenFindReturnsEmpty() {
        var genreId = getGenreId();
        var fileId = getFileId();

        var film = new Film(0, "Test", "Test", 2024,
                genreId, 18, 110, fileId);
        var saved = sql2oFilmRepository.save(film).get();

        sql2oFilmRepository.deleteById(saved.getId());

        assertThat(sql2oFilmRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void whenSaveMultipleThenFindAllReturnsThem() {
        var genreId = getGenreId();
        var fileId = getFileId();

        sql2oFilmRepository.save(new Film(0, "A", "A", 2020,
                genreId, 16, 100, fileId));
        sql2oFilmRepository.save(new Film(0, "B", "B", 2021,
                genreId, 18, 110, fileId));

        var all = sql2oFilmRepository.findAll();

        assertThat(all).hasSize(2);
        assertThat(all).extracting(Film::getName).containsExactlyInAnyOrder("A", "B");
    }
}
