package edu.example.controller;

import edu.example.model.AuthSession;
import edu.example.model.Location;
import edu.example.model.User;
import edu.example.repository.AuthSessionRepository;
import edu.example.repository.LocationRepository;
import edu.example.service.LocationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
public class WeatherController {

    private final AuthSessionRepository authSessionRepository;

    private final LocationRepository locationRepository;

    private final LocationService locationService;

    public WeatherController(
            AuthSessionRepository authSessionRepository,
            LocationRepository locationRepository,
            LocationService locationService
    ) {
        this.authSessionRepository = authSessionRepository;
        this.locationRepository = locationRepository;
        this.locationService = locationService;
    }

    @GetMapping("/search")
    public String search(@CookieValue(value = "id", required = false) String authSessionId,
                         @RequestParam("cityName") String cityName,
                         Model model
    ) {
        if (authSessionId != null) {
            AuthSession authSession = authSessionRepository.findAuthSessionById(UUID.fromString(authSessionId));
            model.addAttribute("username", authSession.getUser().getLogin());
        }
        model.addAttribute("location", locationService.createLocationDTO(cityName));
        return "search-results";
    }

    @PostMapping("/addLocation")
    public String addLocation(@CookieValue(value = "id", required = false) String authSessionId,
                              @RequestParam("cityName") String cityName
    ) {
        if (authSessionId != null) {
            AuthSession authSession = authSessionRepository.findAuthSessionById(UUID.fromString(authSessionId));
            User user = authSession.getUser();
            locationRepository.save(new Location(locationService.createLocationDTO(cityName), user));
            return "redirect:/";
        }
        else {
            return "redirect:/login";
        }
    }

    @PostMapping("/deleteLocation")
    public String deleteLocation(@CookieValue(value = "id", required = false) String authSessionId,
                                 @RequestParam("locationId") String locationId
    ) {
        if (authSessionId != null) {
            AuthSession authSession = authSessionRepository.findAuthSessionById(UUID.fromString(authSessionId));
            locationRepository.deleteLocation(locationId);
            return "redirect:/";
        }
        else {
            return "redirect:/login";
        }
    }

}
