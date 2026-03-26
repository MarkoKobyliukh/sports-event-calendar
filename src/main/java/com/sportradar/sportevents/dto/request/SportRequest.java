package com.sportradar.sportevents.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SportRequest(

        @NotBlank(message = "Sport name must not be blank")
        @Size(max = 100, message = "Sport name must not exceed 100 characters")
        String name
) {}
