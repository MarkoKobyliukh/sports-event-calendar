package com.sportradar.sportevents.mapper;

import com.sportradar.sportevents.domain.Sport;
import com.sportradar.sportevents.dto.response.SportResponse;
import org.springframework.stereotype.Component;

@Component
public class SportMapper {

    public SportResponse toResponse(Sport sport) {
        return new SportResponse(
                sport.getId(),
                sport.getName()
        );
    }
}
