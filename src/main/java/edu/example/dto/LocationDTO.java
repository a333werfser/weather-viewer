package edu.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LocationDTO {

    @JsonProperty("name")
    private String cityName;

    @JsonProperty("coord")
    private Coordinates coordinates;

    @JsonProperty("main")
    private WeatherConditions weatherConditions;

    private List<Weather> weather;

    @JsonProperty("sys")
    private SystemInformation systemInformation;

}
