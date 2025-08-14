package edu.example;

import edu.example.dto.LocationDTO;
import edu.example.service.LocationService;
import edu.example.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTests {

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    LocationService locationService;

    @Test
    void givenTestData_whenServiceObtainsData_thenItFormatsDataCorrectly() {
        String cityName = "Moscow";
        LocationDTO testData = TestDataFactory.locationDTO("Value", "22.111111", "10.222222222@",
                cityName);

        when(restTemplate.getForObject(anyString(), eq(LocationDTO.class))).thenReturn(testData);
        LocationDTO location = locationService.fetchLocationWeather(cityName);

        assertEquals(cityName, location.getCityName());
        assertEquals("22", location.getWeatherConditions().getTemperature());
        assertEquals("10", location.getWeatherConditions().getFeelsLike());
        assertEquals("/img/default.png", location.getImgPath());
    }

}
