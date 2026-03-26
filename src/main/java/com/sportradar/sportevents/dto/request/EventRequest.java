package com.sportradar.sportevents.dto.request;

import com.sportradar.sportevents.domain.EventStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record EventRequest(

        @NotBlank(message = "Event title must not be blank")
        @Size(max = 255, message = "Event title must not exceed 255 characters")
        String title,

        @NotNull(message = "Event date must not be null")
        @FutureOrPresent(message = "Event date must be today or in the future")
        LocalDate eventDate,

        @NotNull(message = "Event time must not be null")
        LocalTime eventTime,

        EventStatus status,

        @NotNull(message = "Sport ID must not be null")
        Long sportId,

        @NotNull(message = "Venue ID must not be null")
        Long venueId
) {}
