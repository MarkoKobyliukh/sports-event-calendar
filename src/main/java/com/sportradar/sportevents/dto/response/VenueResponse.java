package com.sportradar.sportevents.dto.response;

public record VenueResponse(
        Long id,
        String name,
        String address,
        Integer capacity,
        Long cityId,
        String cityName
) {}
