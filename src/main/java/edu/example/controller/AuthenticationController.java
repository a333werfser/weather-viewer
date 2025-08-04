package edu.example.controller;

import edu.example.model.AuthSession;
import edu.example.repository.UserRepository;
import edu.example.service.AuthSessionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import edu.example.model.User;
import edu.example.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;

@Controller
public class AuthenticationController {

    private static final int MAX_LOGIN_LENGTH = 25;

    private static final int MIN_LOGIN_LENGTH = 4;

    private static final int MAX_PASSWORD_LENGTH = 25;

    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserService userService;
    private final AuthSessionService authSessionService;

    @Autowired
    public AuthenticationController(UserService userService, AuthSessionService authSessionService) {
        this.userService = userService;
        this.authSessionService = authSessionService;
    }

    @PostMapping("/register")
    public String processRegistration(
            @RequestParam("username") String username,
            @RequestParam("password") String password, @RequestParam("r_password") String repeatedPassword,
            Model model,
            HttpServletResponse httpServletResponse
    ) {
        if (isValidUserData(username, password, repeatedPassword)) {
            userService.register(new User(username, password));
            return "redirect:/success";
        }

        if (!isValidPasswordLength(password) || !isValidUsernameLength(username)) {
            model.addAttribute("errorMessage", "Username must be between 4 and 25 characters |" +
                    "Password must be between 8 and 25");
        }

        else if (!doPasswordsMatch(password, repeatedPassword)) {
            model.addAttribute("errorMessage", "Passwords don't match");
        }

        else if (!isUsernameUnique(username)) {
            model.addAttribute("errorMessage", "Login already exists");
        }

        httpServletResponse.setStatus(400);
        return "register-with-exception";
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam("username") String username, @RequestParam("password") String password,
            Model model,
            HttpServletResponse httpServletResponse
    ) {
        if (userService.areValidCredentials(username, password)) {
            AuthSession authSession = authSessionService.createAuthSession(username);
            httpServletResponse.setHeader("Set-Cookie", String.format(
                    "id=%s; Expires=%s; path=/; HttpOnly",
                    authSession.getId(),
                    format(authSession.getExpiresAt())
            ));
            return "redirect:/";
        }

        else if (!isValidPasswordLength(password) || !isValidUsernameLength(username)) {
            model.addAttribute("errorMessage", "Username must be between 4 and 25 characters, " +
                    "Password must be between 8 and 25");
        }

        else {
            model.addAttribute("errorMessage", "Invalid username or password");
        }

        return "login-with-exception";
    }

    @GetMapping("/register")
    public String getRegisterPage() {
        return "register";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    public String format(LocalDateTime localDateTime) {
        ZonedDateTime moscowDateTime = localDateTime.atZone(ZoneId.of("Europe/Moscow"));
        ZonedDateTime gmtDateTime = moscowDateTime.withZoneSameInstant(ZoneId.of("GMT"));
        return gmtDateTime.format(DateTimeFormatter.RFC_1123_DATE_TIME);
    }

    public boolean isValidUserData(String username, String password, String repeatedPassword) {
        return doPasswordsMatch(password, repeatedPassword) &&
                isUsernameUnique(username) &&
                isValidUsernameLength(username) &&
                isValidPasswordLength(password);
    }

    public boolean isValidUsernameLength(String username) {
        return username.length() >= MIN_LOGIN_LENGTH && username.length() <= MAX_LOGIN_LENGTH;
    }

    public boolean isValidPasswordLength(String password) {
        return password.length() >= MIN_PASSWORD_LENGTH && password.length() <= MAX_PASSWORD_LENGTH;
    }

    public boolean isUsernameUnique(String username) {
        return !userService.doesUsernameAlreadyExist(username);
    }

    public boolean doPasswordsMatch(String password, String repeatedPassword) {
        return password.equals(repeatedPassword);
    }
}
