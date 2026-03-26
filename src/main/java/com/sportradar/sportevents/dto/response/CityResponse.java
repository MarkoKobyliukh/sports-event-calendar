package com.sportradar.sportevents.dto.response;

public record CityResponse(
        Long id,
        String name,
        Long countryId,
        String countryName
) {}
