package ru.job4j.cinema.repository;

import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.File;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Collection;
import java.util.Optional;
import java.util.Properties;
import static org.assertj.core.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JdbcFileRepositoryTest {

    private static JdbcFileRepository fileRepository;

    @BeforeAll
    public static void initRepositories() throws Exception {
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

        fileRepository = new JdbcFileRepository(jdbcTemplate);
    }

    @AfterEach
    public void clearFiles() {
        for (File file : fileRepository.findAll()) {
            fileRepository.deleteById(file.getId());
        }
    }

    @Test
    @Order(1)
    public void whenFindNotExistThenGetEmptyOptional() {
        Optional<File> file = fileRepository.findById(100);
        assertThat(file).isEmpty();
    }

    @Test
    @Order(2)
    public void whenSaveThenFindByIdReturnsSame() {
        File file = new File(0, "test.png", "/images/test.png");
        File saved = fileRepository.save(file).get();

        Optional<File> found = fileRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get()).usingRecursiveComparison().isEqualTo(saved);
    }

    @Test
    @Order(3)
    public void whenSaveMultipleThenFindAllReturnsThem() {
        fileRepository.save(new File(0, "image1.png", "/images/image1.png"));
        fileRepository.save(new File(0, "image2.png", "/images/image2.png"));

        Collection<File> files = fileRepository.findAll();
        assertThat(files).hasSizeGreaterThanOrEqualTo(2);
        assertThat(files).extracting(File::getName)
                .contains("image1.png", "image2.png");
    }
}
