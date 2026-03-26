package com.sportradar.sportevents.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VenueRequest(

        @NotBlank(message = "Venue name must not be blank")
        @Size(max = 150, message = "Venue name must not exceed 150 characters")
        String name,

        @Size(max = 255, message = "Address must not exceed 255 characters")
        String address,

        @Min(value = 1, message = "Capacity must be a positive number")
        Integer capacity,

        @NotNull(message = "City ID must not be null")
        Long cityId
) {}
