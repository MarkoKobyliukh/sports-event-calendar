package com.sportradar.sportevents.mapper;

import com.sportradar.sportevents.domain.EventTeam;
import com.sportradar.sportevents.dto.response.EventTeamResponse;
import org.springframework.stereotype.Component;

@Component
public class EventTeamMapper {

    public EventTeamResponse toResponse(EventTeam eventTeam) {
        return new EventTeamResponse(
                eventTeam.getId(),
                eventTeam.getTeam().getId(),
                eventTeam.getTeam().getName(),
                eventTeam.isHome(),
                eventTeam.getScore()
        );
    }
}
