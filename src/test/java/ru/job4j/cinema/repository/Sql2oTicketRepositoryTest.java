package ru.job4j.cinema.repository;
import org.junit.jupiter.api.*;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.*;

import org.sql2o.Sql2o;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Sql2oTicketRepositoryTest {

    private static Sql2o sql2o;
    private static Sql2oGenreRepository genreRepository;
    private static Sql2oFileRepository fileRepository;
    private static Sql2oFilmRepository filmRepository;
    private static Sql2oHallRepository hallRepository;
    private static Sql2oFilmSessionRepository filmSessionRepository;
    private static Sql2oTicketRepository ticketRepository;
    private static Sql2oUserRepository userRepository;

    private static Genre genre;
    private static File file;
    private static Film film;
    private static Hall hall;
    private static FilmSession session;

    @BeforeAll
    static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var input = Sql2oTicketRepositoryTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(input);
        }
        var config = new DatasourceConfiguration();
        var datasource = config.connectionPool(
                properties.getProperty("datasource.url"),
                properties.getProperty("datasource.username"),
                properties.getProperty("datasource.password")
        );
        sql2o = config.databaseClient(datasource);

        genreRepository = new Sql2oGenreRepository(sql2o);
        fileRepository = new Sql2oFileRepository(sql2o);
        filmRepository = new Sql2oFilmRepository(sql2o);
        hallRepository = new Sql2oHallRepository(sql2o);
        filmSessionRepository = new Sql2oFilmSessionRepository(sql2o);
        ticketRepository = new Sql2oTicketRepository(sql2o);

        genre = genreRepository.save(new Genre(0, "Test Genre")).orElseThrow();
        file = fileRepository.save(new File(0, "test.png", "/images/test.png")).orElseThrow();
        film = filmRepository.save(new Film(0, "Test Film", "Desc", 2025, genre.getId(), 18, 120, file.getId())).orElseThrow();
        hall = hallRepository.save(new Hall(0, "Test Hall", 5, 10, "Desc")).orElseThrow();

        var startTime = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MINUTES);
        var endTime = startTime.plusHours(2);
        session = filmSessionRepository.save(
                new FilmSession(0, film.getId(), hall.getId(), startTime, endTime, 500)
        );
    }

    @AfterEach
    void clearTickets() {
        ticketRepository.findAll().forEach(t -> ticketRepository.deleteById(t.getId()));
    }

    @AfterAll
    static void clearAll() {
        ticketRepository.findAll().forEach(t -> ticketRepository.deleteById(t.getId()));
        filmSessionRepository.findAll().forEach(fs -> filmSessionRepository.deleteById(fs.getId()));
        filmRepository.findAll().forEach(f -> filmRepository.deleteById(f.getId()));
        hallRepository.findAll().forEach(h -> hallRepository.deleteById(h.getId()));
        genreRepository.findAll().forEach(g -> genreRepository.deleteById(g.getId()));
        fileRepository.findAll().forEach(f -> fileRepository.deleteById(f.getId()));
    }

    @Test
    @Order(2)
    void whenSaveMultipleThenFindAllReturnsThem() {
        ticketRepository.save(new Ticket(0, session.getId(), 1, 1, 1));
        ticketRepository.save(new Ticket(0, session.getId(), 2, 2, 2));

        Collection<Ticket> all = ticketRepository.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @Order(3)
    void whenDeleteByIdThenNotFound() {
        var ticket = ticketRepository.save(new Ticket(0, session.getId(), 3, 3, 1));

        boolean deleted = ticketRepository.deleteById(ticket.getId());
        assertThat(deleted).isTrue();

        assertThat(ticketRepository.findById(ticket.getId())).isEmpty();
    }

    @Test
    @Order(4)
    void whenSaveSameSeatInSameSessionThenException() {
        var ticket1 = ticketRepository.save(new Ticket(0, session.getId(), 4, 4, 1));
        assertThat(ticket1).isNotNull();

        assertThatThrownBy(() ->
                ticketRepository.save(new Ticket(0, session.getId(), 4, 4, 2))
        ).isInstanceOf(Exception.class);
    }
}