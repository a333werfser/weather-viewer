package edu.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.example.model.User;
import edu.example.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

@Controller
public class AuthenticationController {

    private static final int MAX_LOGIN_LENGTH = 25;

    private static final int MIN_LOGIN_LENGTH = 4;

    private static final int MAX_PASSWORD_LENGTH = 25;

    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserService userService;

    @Autowired
    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
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
            model.addAttribute("errorMessage", "Error: username - 4 to 25 characters | password - 8 to 25 characters");
        }

        else if (!doPasswordMatch(password, repeatedPassword)) {
            model.addAttribute("errorMessage", "Passwords don't match");
        }

        else if (!isUsernameUnique(username)) {
            model.addAttribute("errorMessage", "Login already exists");
        }

        httpServletResponse.setStatus(400);
        return "register-with-exception";
    }

    public boolean isValidUserData(String username, String password, String repeatedPassword) {
        return doPasswordMatch(password, repeatedPassword) &&
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
        return !userService.isUsernameAlreadyExist(username);
    }

    public boolean doPasswordMatch(String password, String repeatedPassword) {
        return password.equals(repeatedPassword);
    }
}
