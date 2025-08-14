package edu.example.util;

import edu.example.dto.LocationDTO;
import edu.example.dto.Weather;
import edu.example.dto.WeatherConditions;

import java.util.ArrayList;
import java.util.List;

public class TestDataFactory {

    public static LocationDTO locationDTO(String main, String temp, String feelsLike, String cityName) {
        LocationDTO locationDTO = new LocationDTO();
        WeatherConditions weatherConditions = new WeatherConditions();
        Weather weather = new Weather();
        List<Weather> weatherList = new ArrayList<>();

        weather.setMain(main);
        weatherList.add(weather);

        weatherConditions.setTemperature(temp);
        weatherConditions.setFeelsLike(feelsLike);

        locationDTO.setCityName(cityName);
        locationDTO.setWeatherConditions(weatherConditions);
        locationDTO.setWeather(weatherList);

        return locationDTO;
    }

}
