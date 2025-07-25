package edu.example.controller;

import edu.example.model.AuthSession;
import edu.example.repository.AuthSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;

@Controller
public class WelcomeController {

    private final AuthSessionRepository authSessionRepository;

    @Autowired
    public WelcomeController(AuthSessionRepository authSessionRepository) {
        this.authSessionRepository = authSessionRepository;
    }


    @GetMapping("/")
    public String welcomePage(@CookieValue(value = "id", required = false) String authSessionId,
                              Model model
    ) {
        if (authSessionId != null) {
            AuthSession authSession = authSessionRepository.findAuthSessionById(UUID.fromString(authSessionId));
            model.addAttribute("username", authSession.getUser().getLogin());
        }
        return "index";
    }

    @GetMapping("/success")
    public String successPage() {
        return "success";
    }

}
