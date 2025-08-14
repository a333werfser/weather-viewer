package edu.example;

import edu.example.model.User;
import edu.example.service.UserService;
import jakarta.persistence.EntityExistsException;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitWebConfig(classes = { TestConfig.class })
@ActiveProfiles("test")
public class AuthenticationTests {

    @Autowired
    UserService userService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    Flyway flyway;

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @BeforeEach
    void resetDatabase() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void givenNoUsers_whenRegisteringUser_thenUserPersists() {
        User user = new User("username", "password123@");
        userService.register(user);

        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "Users"));
    }

    @Test
    void givenCleanDatabase_whenCountingUsers_thenReturnsZero() {
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, "Users"));
    }

    @Test
    void whenUsernameNotUnique_shouldThrowException() {
        User user = new User("username", "password123@");
        userService.register(user);

        assertThrows(EntityExistsException.class, ()  -> userService.register(user));
    }

    @Test
    void whenUserLogIn_thenSetCookieAndCreateSession() throws Exception {
        User user = new User("username", "password123@");
        userService.register(user);

        mockMvc.perform(post("/auth/login").param("username", "username")
                .param("password", "password123@"))
                .andExpect(cookie().exists("id"));

        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "Sessions"));
    }

    @Test
    void whenCrazyBigValue_thenBadRequest() throws Exception {
        char[] chars = new char[100_000];
        Arrays.fill(chars, '@');
        String crazy = String.valueOf(chars);

        mockMvc.perform(post("/auth/login").param("username", crazy)
                        .param("password", crazy))
                .andExpect(status().isBadRequest());
    }

}
