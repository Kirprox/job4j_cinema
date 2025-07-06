package ru.job4j.cinema.repository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.Ticket;

import org.sql2o.Sql2o;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

class Sql2oTicketRepositoryTest {

    private static Sql2oTicketRepository sql2oTicketRepository;

    private static Sql2o sql2o;

    @BeforeAll
    public static void initRepositories() throws Exception {
        Properties properties = new Properties();
        try (InputStream inputStream = Sql2oTicketRepository.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        String url = properties.getProperty("datasource.url");
        String username = properties.getProperty("datasource.username");
        String password = properties.getProperty("datasource.password");

        DatasourceConfiguration configuration = new DatasourceConfiguration();
        DataSource datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);

        sql2oTicketRepository = new Sql2oTicketRepository(sql2o);
    }

    @Test
    public void whenFindNotExistThenGetEmptyOptional() {
        Optional<Ticket> ticket = sql2oTicketRepository.findById(200);
        assertThat(ticket).isEmpty();
    }
}