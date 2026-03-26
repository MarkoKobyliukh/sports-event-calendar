package com.sportradar.sportevents.service;

import com.sportradar.sportevents.domain.Sport;
import com.sportradar.sportevents.dto.request.SportRequest;
import com.sportradar.sportevents.dto.response.SportResponse;
import com.sportradar.sportevents.exception.DuplicateResourceException;
import com.sportradar.sportevents.exception.ResourceNotFoundException;
import com.sportradar.sportevents.mapper.SportMapper;
import com.sportradar.sportevents.repository.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SportService {

    private final SportRepository sportRepository;
    private final SportMapper sportMapper;

    public List<SportResponse> findAll() {
        return sportRepository.findAll().stream()
                .map(sportMapper::toResponse)
                .toList();
    }

    public SportResponse findById(Long id) {
        return sportMapper.toResponse(getSportOrThrow(id));
    }

    @Transactional
    public SportResponse create(SportRequest request) {
        if (sportRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("Sport already exists with name: " + request.name());
        }
        Sport sport = Sport.builder()
                .name(request.name())
                .build();
        return sportMapper.toResponse(sportRepository.save(sport));
    }

    @Transactional
    public SportResponse update(Long id, SportRequest request) {
        Sport sport = getSportOrThrow(id);
        sportRepository.findByNameIgnoreCase(request.name())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Sport already exists with name: " + request.name());
                });
        sport.setName(request.name());
        return sportMapper.toResponse(sportRepository.save(sport));
    }

    @Transactional
    public void delete(Long id) {
        getSportOrThrow(id);
        sportRepository.deleteById(id);
    }

    public Sport getSportOrThrow(Long id) {
        return sportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sport", id));
    }
}
