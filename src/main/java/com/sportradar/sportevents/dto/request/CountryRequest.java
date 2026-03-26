package com.sportradar.sportevents.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CountryRequest(

        @NotBlank(message = "Country name must not be blank")
        @Size(max = 100, message = "Country name must not exceed 100 characters")
        String name,

        @NotBlank(message = "Country code must not be blank")
        @Size(min = 2, max = 3, message = "Country code must be 2–3 characters")
        String code
) {}
