package ru.job4j.cinema.repository;

import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.*;

import javax.sql.DataSource;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JdbcTicketRepositoryTest {

    private static JdbcGenreRepository genreRepository;
    private static JdbcFileRepository fileRepository;
    private static JdbcFilmRepository filmRepository;
    private static JdbcHallRepository hallRepository;
    private static JdbcFilmSessionRepository filmSessionRepository;
    private static JdbcTicketRepository ticketRepository;

    private static Genre genre;
    private static File file;
    private static Film film;
    private static Hall hall;
    private static FilmSession session;

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
        filmRepository = new JdbcFilmRepository(jdbcTemplate);
        hallRepository = new JdbcHallRepository(jdbcTemplate);
        filmSessionRepository = new JdbcFilmSessionRepository(jdbcTemplate);
        ticketRepository = new JdbcTicketRepository(jdbcTemplate);

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