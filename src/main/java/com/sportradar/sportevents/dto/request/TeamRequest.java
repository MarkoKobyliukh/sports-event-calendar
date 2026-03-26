package com.sportradar.sportevents.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TeamRequest(

        @NotBlank(message = "Team name must not be blank")
        @Size(max = 150, message = "Team name must not exceed 150 characters")
        String name,

        @NotNull(message = "Sport ID must not be null")
        Long sportId,

        @NotNull(message = "City ID must not be null")
        Long cityId
) {}
