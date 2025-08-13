package edu.example.service;

import edu.example.dto.LocationDTO;
import edu.example.model.Location;
import edu.example.model.User;
import edu.example.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    private final RestTemplate restTemplate;

    @Value("${w.api.key}")
    private String apikey;

    public LocationService(LocationRepository locationRepository, RestTemplate restTemplate) {
        this.locationRepository = locationRepository;
        this.restTemplate = restTemplate;
    }

    public List<LocationDTO> fetchAllUserLocations(User user) {
        List<Location> locations = locationRepository.findAllUserLocations(user);
        List<LocationDTO> locationDTOs = new ArrayList<>();
        for (Location loc : locations) {
            locationDTOs.add(fetchLocationWeatherWithId(loc));
        }
        return locationDTOs;
    }

    public LocationDTO fetchLocationWeather(String cityName) throws HttpClientErrorException {
        String url = String.format(
                "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric",
                cityName, apikey);
        LocationDTO locationDTO = null;
        locationDTO = restTemplate.getForObject(url, LocationDTO.class);

        String weather = locationDTO.getWeather().get(0).getMain();
        String imgPath = composeWeatherImagePath(weather);
        locationDTO.setImgPath(imgPath);

        return formatWeatherData(locationDTO);
    }

    /**
     * это костыль чтобы реализовать фичу удаления локации по ID
     * @return Location DTO с айди из базы
     */
    public LocationDTO fetchLocationWeatherWithId(Location location) {
        LocationDTO locationDTO = fetchLocationWeather(location.getName());
        locationDTO.setId(location.getId().toString());
        return locationDTO;
    }

    public LocationDTO formatWeatherData(LocationDTO locationDTO) {
        String temperatureString = locationDTO.getWeatherConditions().getTemperature();
        String feelsLikeString = locationDTO.getWeatherConditions().getFeelsLike();

        temperatureString = temperatureString.substring(0, temperatureString.indexOf("."));
        feelsLikeString = feelsLikeString.substring(0, feelsLikeString.indexOf("."));

        locationDTO.getWeatherConditions().setTemperature(temperatureString);
        locationDTO.getWeatherConditions().setFeelsLike(feelsLikeString);

        if (locationDTO.getCityName().contains("’")) {
            String cityName = locationDTO.getCityName();
            locationDTO.setCityName(cityName.substring(0, cityName.length() - 1));
        }

        return locationDTO;
    }

    private String composeWeatherImagePath(String weather) {
        String filename = switch (weather.toLowerCase()) {
            case "thunderstorm" -> "thunderstorm.png";
            case "drizzle" -> "drizzle.png";
            case "rain" -> "rain.png";
            case "snow" -> "snow.png";
            case "atmosphere" -> "atmosphere.png";
            case "clear" -> "clear.png";
            case "clouds" -> "clouds.png";
            default -> "default.png";
        };
        return "/img/" + filename;
    }

}
