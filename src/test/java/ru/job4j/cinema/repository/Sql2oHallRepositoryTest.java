package ru.job4j.cinema.repository;

import org.junit.jupiter.api.*;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.Hall;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Sql2oHallRepositoryTest {

    private static Sql2oHallRepository hallRepository;
    private static Sql2o sql2o;

    @BeforeAll
    static void initRepositories() throws Exception {
        Properties properties = new Properties();
        try (InputStream input = Sql2oHallRepositoryTest.class.getClassLoader()
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

        hallRepository = new Sql2oHallRepository(sql2o);
    }

    @AfterEach
    void clearHalls() {
        hallRepository.findAll().forEach(h -> hallRepository.deleteById(h.getId()));
    }

    @Test
    @Order(1)
    void whenFindByIdNotExistingThenReturnEmpty() {
        var result = hallRepository.findById(-1);
        assertThat(result).isEmpty();
    }

    @Test
    @Order(2)
    void whenSaveThenFindByIdReturnsSame() {
        var hall = new Hall(0, "Red Hall", 10, 20, "For VIP screenings");
        var saved = hallRepository.save(hall).orElseThrow();

        var found = hallRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get()).usingRecursiveComparison().isEqualTo(saved);
    }

    @Test
    @Order(3)
    void whenSaveMultipleThenFindAllReturnsThem() {
        var hall1 = hallRepository.save(new Hall(0, "Blue Hall", 8, 15, "2D")).orElseThrow();
        var hall2 = hallRepository.save(new Hall(0, "Green Hall", 12, 25, "3D")).orElseThrow();

        var all = hallRepository.findAll();

        assertThat(all).hasSizeGreaterThanOrEqualTo(2);
        assertThat(all).extracting(Hall::getName).contains("Blue Hall", "Green Hall");
    }

    @Test
    @Order(4)
    void whenDeleteThenFindByIdReturnsEmpty() {
        var hall = hallRepository.save(new Hall(0, "Test Hall", 5, 10, "Test description")).orElseThrow();
        hallRepository.deleteById(hall.getId());

        var found = hallRepository.findById(hall.getId());
        assertThat(found).isEmpty();
    }
}