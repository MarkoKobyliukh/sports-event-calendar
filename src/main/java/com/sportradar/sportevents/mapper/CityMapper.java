package com.sportradar.sportevents.mapper;

import com.sportradar.sportevents.domain.City;
import com.sportradar.sportevents.dto.response.CityResponse;
import org.springframework.stereotype.Component;

@Component
public class CityMapper {

    public CityResponse toResponse(City city) {
        return new CityResponse(
                city.getId(),
                city.getName(),
                city.getCountry().getId(),
                city.getCountry().getName()
        );
    }
}
