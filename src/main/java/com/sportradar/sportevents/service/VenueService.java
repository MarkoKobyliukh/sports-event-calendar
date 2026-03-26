package com.sportradar.sportevents.service;

import com.sportradar.sportevents.domain.City;
import com.sportradar.sportevents.domain.Venue;
import com.sportradar.sportevents.dto.request.VenueRequest;
import com.sportradar.sportevents.dto.response.VenueResponse;
import com.sportradar.sportevents.exception.ResourceNotFoundException;
import com.sportradar.sportevents.mapper.VenueMapper;
import com.sportradar.sportevents.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VenueService {

    private final VenueRepository venueRepository;
    private final VenueMapper venueMapper;
    private final CityService cityService;

    public List<VenueResponse> findAll() {
        return venueRepository.findAll().stream()
                .map(venueMapper::toResponse)
                .toList();
    }

    public VenueResponse findById(Long id) {
        return venueMapper.toResponse(getVenueOrThrow(id));
    }

    public List<VenueResponse> findByCity(Long cityId) {
        cityService.getCityOrThrow(cityId);
        return venueRepository.findByCityId(cityId).stream()
                .map(venueMapper::toResponse)
                .toList();
    }

    @Transactional
    public VenueResponse create(VenueRequest request) {
        City city = cityService.getCityOrThrow(request.cityId());
        Venue venue = Venue.builder()
                .name(request.name())
                .address(request.address())
                .capacity(request.capacity())
                .city(city)
                .build();
        return venueMapper.toResponse(venueRepository.save(venue));
    }

    @Transactional
    public VenueResponse update(Long id, VenueRequest request) {
        Venue venue = getVenueOrThrow(id);
        City city = cityService.getCityOrThrow(request.cityId());
        venue.setName(request.name());
        venue.setAddress(request.address());
        venue.setCapacity(request.capacity());
        venue.setCity(city);
        return venueMapper.toResponse(venueRepository.save(venue));
    }

    @Transactional
    public void delete(Long id) {
        getVenueOrThrow(id);
        venueRepository.deleteById(id);
    }

    public Venue getVenueOrThrow(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue", id));
    }
}
