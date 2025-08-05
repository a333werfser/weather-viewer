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
            locationDTOs.add(createLocationDTOWithId(loc));
        }
        return locationDTOs;
    }

    public LocationDTO createLocationDTO(String cityName) {
        String url = String.format(
                "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric",
                cityName, apikey);
        RestTemplate restTemplate = new RestTemplate();
        LocationDTO locationDTO = restTemplate.getForObject(url, LocationDTO.class);

        String weather = locationDTO.getWeather().get(0).getMain();
        String imgPath = composeImgPath(weather);
        locationDTO.setImgPath(imgPath);

        return locationDTO;
    }

    public LocationDTO createLocationDTOWithId(Location location) {
        LocationDTO locationDTO = createLocationDTO(location.getName());
        locationDTO.setId(location.getId().toString());
        return locationDTO;
    }

    private String composeImgPath(String weather) {
        String filename;
        switch (weather.toLowerCase()) {
            case "thunderstorm":
                filename = "thunderstorm.png";
                break;
            case "drizzle":
                filename = "drizzle.png";
                break;
            case "rain":
                filename = "rain.png";
                break;
            case "snow":
                filename = "snow.png";
                break;
            case "atmosphere":
                filename = "atmosphere.png";
                break;
            case "clear":
                filename = "clear.png";
                break;
            case "clouds":
                filename = "clouds.png";
                break;
            default:
                filename = "default.png";
                break;
        }
        return "/img/" + filename;
    }

}
