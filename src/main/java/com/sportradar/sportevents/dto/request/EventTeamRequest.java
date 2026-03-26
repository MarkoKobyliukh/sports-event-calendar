package com.sportradar.sportevents.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record EventTeamRequest(

        @NotNull(message = "Team ID must not be null")
        Long teamId,

        @NotNull(message = "Home flag must not be null")
        Boolean home,

        @Min(value = 0, message = "Score must be zero or positive")
        Integer score
) {}
