package com.sportradar.sportevents.dto.response;

import com.sportradar.sportevents.domain.EventStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record EventResponse(
        Long id,
        String title,
        LocalDate eventDate,
        LocalTime eventTime,
        EventStatus status,
        Long sportId,
        String sportName,
        Long venueId,
        String venueName,
        List<EventTeamResponse> teams
) {}
