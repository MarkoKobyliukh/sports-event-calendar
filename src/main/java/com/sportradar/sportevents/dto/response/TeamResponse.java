package com.sportradar.sportevents.dto.response;

public record TeamResponse(
        Long id,
        String name,
        Long sportId,
        String sportName,
        Long cityId,
        String cityName
) {}
