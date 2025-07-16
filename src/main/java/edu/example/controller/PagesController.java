package edu.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PagesController {

    @GetMapping("/")
    public String welcomePage(
            @CookieValue(name = "id", required = false, defaultValue = "") String cookie,
            Model model
    ) {
        String username;
        if (!cookie.isEmpty()) {
            username = cookie;
        } else {
            username = null;
        }

        model.addAttribute("username", username);

        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
}
