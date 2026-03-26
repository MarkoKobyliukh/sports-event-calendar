package com.sportradar.sportevents.dto.response;

public record EventTeamResponse(
        Long id,
        Long teamId,
        String teamName,
        boolean home,
        Integer score
) {}
