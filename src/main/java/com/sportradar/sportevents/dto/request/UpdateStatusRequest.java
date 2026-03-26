package com.sportradar.sportevents.dto.request;

import com.sportradar.sportevents.domain.EventStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(

        @NotNull(message = "Status must not be null")
        EventStatus status
) {}
