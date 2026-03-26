package com.sportradar.sportevents.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateScoreRequest(

        @NotNull(message = "Score must not be null")
        @Min(value = 0, message = "Score must be zero or positive")
        Integer score
) {}
