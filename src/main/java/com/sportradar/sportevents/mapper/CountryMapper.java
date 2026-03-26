package com.sportradar.sportevents.mapper;

import com.sportradar.sportevents.domain.Country;
import com.sportradar.sportevents.dto.response.CountryResponse;
import org.springframework.stereotype.Component;

@Component
public class CountryMapper {

    public CountryResponse toResponse(Country country) {
        return new CountryResponse(
                country.getId(),
                country.getName(),
                country.getCode()
        );
    }
}
