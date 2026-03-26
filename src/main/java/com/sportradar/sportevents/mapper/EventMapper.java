package com.sportradar.sportevents.mapper;

import com.sportradar.sportevents.domain.Event;
import com.sportradar.sportevents.dto.response.EventResponse;
import com.sportradar.sportevents.dto.response.EventTeamResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventMapper {

    private final EventTeamMapper eventTeamMapper;

    public EventResponse toResponse(Event event) {
        List<EventTeamResponse> teams = event.getEventTeams().stream()
                .map(eventTeamMapper::toResponse)
                .toList();

        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getEventDate(),
                event.getEventTime(),
                event.getStatus(),
                event.getSport().getId(),
                event.getSport().getName(),
                event.getVenue().getId(),
                event.getVenue().getName(),
                teams
        );
    }
}
