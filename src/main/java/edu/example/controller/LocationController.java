package edu.example.controller;

import edu.example.model.AuthSession;
import edu.example.model.Location;
import edu.example.model.User;
import edu.example.repository.AuthSessionRepository;
import edu.example.repository.LocationRepository;
import edu.example.service.LocationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.UUID;

@Controller
@RequestMapping("/location")
public class LocationController {

    private final AuthSessionRepository authSessionRepository;

    private final LocationRepository locationRepository;

    private final LocationService locationService;

    public LocationController(
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

    @PostMapping
    public String addLocation(@RequestParam("cityName") String cityName, HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        locationRepository.save(new Location(locationService.createLocationDTO(cityName), user));
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String deleteLocation(@RequestParam("locationId") String locationId, HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        locationRepository.deleteLocation(Long.parseLong(locationId), user);
        return "redirect:/";
    }

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleException(HttpClientErrorException e, Model model) {
        model.addAttribute("message", "Oops! Location not found...");
        return "error-page";
    }

}
