package edu.example.controller;

import edu.example.model.Session;
import edu.example.model.User;
import edu.example.repository.UserRepository;
import edu.example.service.SessionService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SecondController {

    @PostMapping("/register")
    public void processRegistration(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) {
        UserRepository userRepository = new UserRepository();
        userRepository.addUser(new User(username, password));
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletResponse response
    ) {
        UserRepository userRepository = new UserRepository();
        SessionService sessionService = new SessionService();
        User user = userRepository.findUserByUsername(username);

        if (user == null) {
            return "index";
        }

        Session session = new Session(user.getId());
        sessionService.addSession(session);

        String cookieValue = String.format(
                "id=%s; Max-Age=%d; path=/", session.getSessionId(), session.getMaxAge()
        );

        response.setHeader("Set-Cookie", cookieValue);

        return "redirect:/";
    }

}
