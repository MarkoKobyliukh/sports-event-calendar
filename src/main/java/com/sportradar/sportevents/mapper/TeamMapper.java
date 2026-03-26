package com.sportradar.sportevents.mapper;

import com.sportradar.sportevents.domain.Team;
import com.sportradar.sportevents.dto.response.TeamResponse;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper {

    public TeamResponse toResponse(Team team) {
        return new TeamResponse(
                team.getId(),
                team.getName(),
                team.getSport().getId(),
                team.getSport().getName(),
                team.getCity().getId(),
                team.getCity().getName()
        );
    }
}
