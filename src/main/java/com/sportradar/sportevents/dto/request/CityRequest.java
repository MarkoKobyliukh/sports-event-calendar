package com.sportradar.sportevents.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CityRequest(

        @NotBlank(message = "City name must not be blank")
        @Size(max = 100, message = "City name must not exceed 100 characters")
        String name,

        @NotNull(message = "Country ID must not be null")
        Long countryId
) {}
