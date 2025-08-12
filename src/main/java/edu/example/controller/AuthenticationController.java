package edu.example.controller;

import edu.example.model.AuthSession;
import edu.example.repository.AuthSessionRepository;
import edu.example.service.AuthSessionService;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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
import java.util.UUID;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {

    private static final int MAX_LOGIN_LENGTH = 25;

    private static final int MIN_LOGIN_LENGTH = 4;

    private static final int MAX_PASSWORD_LENGTH = 25;

    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserService userService;

    private final AuthSessionService authSessionService;

    private final AuthSessionRepository authSessionRepository;

    @Autowired
    public AuthenticationController(UserService userService, AuthSessionService authSessionService,
                                    AuthSessionRepository authSessionRepository) {
        this.userService = userService;
        this.authSessionService = authSessionService;
        this.authSessionRepository = authSessionRepository;
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

        httpServletResponse.setStatus(400);
        return "login-with-exception";
    }

    @PostMapping("/logout")
    public String processLogout(@CookieValue("id") String authSessionId, HttpServletResponse httpServletResponse) {
        if (authSessionId != null) {
            authSessionRepository.deleteAuthSessionById(UUID.fromString(authSessionId));
            httpServletResponse.setHeader("Set-Cookie", String.format("id=%s; Max-Age=0; path=/", authSessionId));
        }
        return "redirect:/";
    }

    @GetMapping("/register")
    public String getRegisterPage() {
        return "register";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleException(Model model) {
        model.addAttribute("errorMessage", "Username already taken");
        return "register-with-exception";
    }

    private String format(LocalDateTime localDateTime) {
        ZonedDateTime moscowDateTime = localDateTime.atZone(ZoneId.of("Europe/Moscow"));
        ZonedDateTime gmtDateTime = moscowDateTime.withZoneSameInstant(ZoneId.of("GMT"));
        return gmtDateTime.format(DateTimeFormatter.RFC_1123_DATE_TIME);
    }

    private boolean isValidUserData(String username, String password, String repeatedPassword) {
        return doPasswordsMatch(password, repeatedPassword) &&
                isValidUsernameLength(username) &&
                isValidPasswordLength(password);
    }

    private boolean isValidUsernameLength(String username) {
        return username.length() >= MIN_LOGIN_LENGTH && username.length() <= MAX_LOGIN_LENGTH;
    }

    private boolean isValidPasswordLength(String password) {
        return password.length() >= MIN_PASSWORD_LENGTH && password.length() <= MAX_PASSWORD_LENGTH;
    }

    private boolean doPasswordsMatch(String password, String repeatedPassword) {
        return password.equals(repeatedPassword);
    }
}
