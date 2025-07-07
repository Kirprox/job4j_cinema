package ru.job4j.cinema.repository;

import org.junit.jupiter.api.*;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Sql2oFilmSessionRepositoryTest {

    private static Sql2oFilmRepository filmRepository;
    private static Sql2oGenreRepository genreRepository;
    private static Sql2oFileRepository fileRepository;
    private static Sql2oHallRepository hallRepository;
    private static Sql2oFilmSessionRepository filmSessionRepository;
    private static Sql2o sql2o;
    private static Genre savedGenre;
    private static File savedFile;
    private static Film savedFilm;
    private static Hall savedHall;

    @BeforeAll
    static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oFilmSessionRepositoryTest.class.getClassLoader()
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

        filmRepository = new Sql2oFilmRepository(sql2o);
        genreRepository = new Sql2oGenreRepository(sql2o);
        fileRepository = new Sql2oFileRepository(sql2o);
        hallRepository = new Sql2oHallRepository(sql2o);
        filmSessionRepository = new Sql2oFilmSessionRepository(sql2o);

        savedGenre = genreRepository.save(new Genre(0, "Test Genre")).orElseThrow();
        savedFile = fileRepository.save(new File(0, "test.png", "/images/test.png")).orElseThrow();
        savedFilm = filmRepository.save(new Film(0, "Test Film", "Desc", 2025,
                savedGenre.getId(), 18, 120, savedFile.getId())).orElseThrow();
        savedHall = hallRepository.findAll().stream().findFirst().orElseGet(() -> {
            Hall hall = new Hall(0, "Test Hall", 5, 10, "Desc");
            return hallRepository.save(hall).orElseThrow();
        });
    }

    @AfterEach
    void clearData() {
        filmSessionRepository.findAll()
                .forEach(fs -> filmSessionRepository.deleteById(fs.getId()));
    }

    @AfterAll
    static void cleanUpAfterAll() {
        filmSessionRepository.findAll().forEach(fs -> filmSessionRepository.deleteById(fs.getId()));
        filmRepository.findAll().forEach(f -> filmRepository.deleteById(f.getId()));
        hallRepository.findAll().forEach(h -> hallRepository.deleteById(h.getId()));
        genreRepository.findAll().forEach(g -> genreRepository.deleteById(g.getId()));
        fileRepository.findAll().forEach(f -> fileRepository.deleteById(f.getId()));
    }

    @Test
    @Order(1)
    void whenSaveThenFindByIdReturnsSame() {
        var startTime = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MINUTES);
        var endTime = startTime.plusHours(2);
        var filmSession = new FilmSession(0, savedFilm.getId(), savedHall.getId(), startTime, endTime, 300);

        var saved = filmSessionRepository.save(filmSession);

        var found = filmSessionRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get()).usingRecursiveComparison().isEqualTo(saved);
    }

    @Test
    @Order(2)
    void whenSaveMultipleThenFindAllReturnsThem() {
        var start1 = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MINUTES);
        var end1 = start1.plusHours(2);
        var start2 = LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.MINUTES);
        var end2 = start2.plusHours(3);

        filmSessionRepository.save(new FilmSession(0, savedFilm.getId(), savedHall.getId(), start1, end1, 300));
        filmSessionRepository.save(new FilmSession(0, savedFilm.getId(), savedHall.getId(), start2, end2, 400));

        var all = filmSessionRepository.findAll();

        assertThat(all).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @Order(3)
    void whenDeleteThenNotFound() {
        var start = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MINUTES);
        var end = start.plusHours(2);
        var filmSession = new FilmSession(0, savedFilm.getId(), savedHall.getId(), start, end, 300);

        var saved = filmSessionRepository.save(filmSession);

        filmSessionRepository.deleteById(saved.getId());

        assertThat(filmSessionRepository.findById(saved.getId())).isEmpty();
    }
}