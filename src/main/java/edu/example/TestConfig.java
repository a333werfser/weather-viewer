package edu.example;

import edu.example.model.AuthSession;
import edu.example.model.Location;
import edu.example.model.User;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernatePersistenceConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;

@EnableWebMvc
@Configuration
@Profile("test")
@ComponentScan(basePackages = {
        "edu.example.model",
        "edu.example.repository",
        "edu.example.service",
        "edu.example.controller"
})
public class TestConfig {

    @Bean
    public Flyway flyway() {
        return Flyway.configure().dataSource("jdbc:h2:mem:test_mem;DB_CLOSE_DELAY=-1", "sa", "")
                .locations("classpath:db/migration_test").cleanDisabled(false).load();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public SessionFactory sessionFactory() {
        return new HibernatePersistenceConfiguration("embedded-h2")
                .managedClass(User.class)
                .managedClass(AuthSession.class)
                .managedClass(Location.class)
                .jdbcUrl("jdbc:h2:mem:test_mem;DB_CLOSE_DELAY=-1")
                .jdbcCredentials("sa", "")
                .showSql(true, true, true)
                .createEntityManagerFactory();
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setUrl("jdbc:h2:mem:test_mem;DB_CLOSE_DELAY=-1");
        ds.setUsername("sa");
        ds.setPassword("");
        return ds;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return  new JdbcTemplate(dataSource);
    }

}
