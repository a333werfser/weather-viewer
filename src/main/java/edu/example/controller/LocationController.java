package edu.example.controller;

import edu.example.model.Location;
import edu.example.model.User;
import edu.example.repository.LocationRepository;
import edu.example.service.LocationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@Controller
@RequestMapping("/location")
public class LocationController {

    private final LocationRepository locationRepository;

    private final LocationService locationService;

    public LocationController(LocationRepository locationRepository, LocationService locationService) {
        this.locationRepository = locationRepository;
        this.locationService = locationService;
    }

    @GetMapping("/search")
    public String search(@RequestParam("cityName") String cityName, HttpServletRequest request, Model model) {
        User user = (User) request.getAttribute("user");
        model.addAttribute("username", user.getLogin());
        model.addAttribute("location", locationService.fetchLocationWeather(cityName));
        return "search-results";
    }

    @PostMapping
    public String addLocation(@RequestParam("cityName") String cityName, HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        locationRepository.save(new Location(locationService.fetchLocationWeather(cityName), user));
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
