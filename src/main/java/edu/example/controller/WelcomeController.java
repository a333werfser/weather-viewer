package edu.example.controller;

import edu.example.dto.LocationDTO;
import edu.example.model.AuthSession;
import edu.example.repository.AuthSessionRepository;
import edu.example.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.UUID;

@Controller
public class WelcomeController {

    private final AuthSessionRepository authSessionRepository;

    private final LocationService locationService;

    @Autowired
    public WelcomeController(
            AuthSessionRepository authSessionRepository,
            LocationService locationService
    ) {
        this.authSessionRepository = authSessionRepository;
        this.locationService = locationService;
    }


    @GetMapping("/")
    public String welcomePage(@CookieValue(value = "id", required = false) String authSessionId,
                              Model model
    ) {
        if (authSessionId != null) {
            AuthSession authSession = authSessionRepository.findAuthSessionById(UUID.fromString(authSessionId));
            List<LocationDTO> locations = locationService.getAllUserLocations(authSession.getUser());

            model.addAttribute("username", authSession.getUser().getLogin());
            model.addAttribute("locations", locations);
        }
        return "index";
    }

    @GetMapping("/success")
    public String successPage() {
        return "success";
    }

}
