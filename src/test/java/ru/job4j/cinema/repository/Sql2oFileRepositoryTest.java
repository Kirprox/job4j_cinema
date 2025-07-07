package ru.job4j.cinema.repository;

import org.junit.jupiter.api.*;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.File;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Collection;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Sql2oFileRepositoryTest {
    private static Sql2oFileRepository sql2oFileRepository;
    private static Sql2o sql2o;

    @BeforeAll
    public static void initRepositories() throws Exception {
        Properties properties = new Properties();
        try (InputStream inputStream = Sql2oFileRepository.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        String url = properties.getProperty("datasource.url");
        String username = properties.getProperty("datasource.username");
        String password = properties.getProperty("datasource.password");

        DatasourceConfiguration configuration = new DatasourceConfiguration();
        DataSource datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);

        sql2oFileRepository = new Sql2oFileRepository(sql2o);
    }

    @AfterEach
    public void clearFiles() {
        Collection<File> files = sql2oFileRepository.findAll();
        for (File file : files) {
            sql2oFileRepository.deleteById(file.getId());
        }
    }

    @Test
    @Order(1)
    public void whenFindNotExistThenGetEmptyOptional() {
        Optional<File> file = sql2oFileRepository.findById(100);
        assertThat(file).isEmpty();
    }

    @Test
    @Order(2)
    public void whenSaveThenFindByIdReturnsSame() {
        File file = new File(0, "test.png", "/images/test.png");
        File saved = sql2oFileRepository.save(file).get();

        Optional<File> found = sql2oFileRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get()).usingRecursiveComparison().isEqualTo(saved);
    }

    @Test
    @Order(3)
    public void whenSaveMultipleThenFindAllReturnsThem() {
        sql2oFileRepository.save(new File(0, "image1.png", "/images/image1.png"));
        sql2oFileRepository.save(new File(0, "image2.png", "/images/image2.png"));

        Collection<File> files = sql2oFileRepository.findAll();
        assertThat(files).hasSizeGreaterThanOrEqualTo(2);
        assertThat(files).extracting(File::getName)
                .contains("image1.png", "image2.png");
    }
}