package edu.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherConditions {

    @JsonProperty("temp")
    String temperature;

    @JsonProperty("feels_like")
    String feelsLike;

    String pressure;

    String humidity;

}
