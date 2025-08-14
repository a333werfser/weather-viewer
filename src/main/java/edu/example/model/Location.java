package edu.example.model;

import edu.example.dto.LocationDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "Locations")
public class Location {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "Name")
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId")
    private User user;

    @Column(name = "Latitude")
    private BigDecimal latitude;

    @Column(name = "Longitude")
    private BigDecimal longitude;

    public Location(LocationDTO locationDTO, User user) {
        this.name = locationDTO.getCityName();
        this.user = user;
        this.latitude = new BigDecimal(locationDTO.getCoordinates().getLat());
        this.longitude = new BigDecimal(locationDTO.getCoordinates().getLon());
    }

}
