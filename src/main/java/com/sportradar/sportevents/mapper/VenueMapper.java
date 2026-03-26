package com.sportradar.sportevents.mapper;

import com.sportradar.sportevents.domain.Venue;
import com.sportradar.sportevents.dto.response.VenueResponse;
import org.springframework.stereotype.Component;

@Component
public class VenueMapper {

    public VenueResponse toResponse(Venue venue) {
        return new VenueResponse(
                venue.getId(),
                venue.getName(),
                venue.getAddress(),
                venue.getCapacity(),
                venue.getCity().getId(),
                venue.getCity().getName()
        );
    }
}
