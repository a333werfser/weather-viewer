package edu.example.service;

import edu.example.dto.LocationDTO;
import edu.example.model.Location;
import edu.example.model.User;
import edu.example.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    @Value("${w.api.key}")
    private String apikey;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public List<LocationDTO> getAllUserLocations(User user) {
        List<Location> locations = locationRepository.findAllUserLocations(user);
        List<LocationDTO> locationDTOs = new ArrayList<>();
        for (Location loc : locations) {
            locationDTOs.add(createLocationDTO(loc.getName()));
        }
        return locationDTOs;
    }

    public LocationDTO createLocationDTO(String cityName) {
        String url = String.format(
                "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric",
                cityName, apikey);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, LocationDTO.class);
    }

}
