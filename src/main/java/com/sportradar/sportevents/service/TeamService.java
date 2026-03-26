package com.sportradar.sportevents.service;

import com.sportradar.sportevents.domain.City;
import com.sportradar.sportevents.domain.Sport;
import com.sportradar.sportevents.domain.Team;
import com.sportradar.sportevents.dto.request.TeamRequest;
import com.sportradar.sportevents.dto.response.TeamResponse;
import com.sportradar.sportevents.exception.DuplicateResourceException;
import com.sportradar.sportevents.exception.ResourceNotFoundException;
import com.sportradar.sportevents.mapper.TeamMapper;
import com.sportradar.sportevents.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;
    private final SportService sportService;
    private final CityService cityService;

    public List<TeamResponse> findAll() {
        return teamRepository.findAll().stream()
                .map(teamMapper::toResponse)
                .toList();
    }

    public TeamResponse findById(Long id) {
        return teamMapper.toResponse(getTeamOrThrow(id));
    }

    public List<TeamResponse> findBySport(Long sportId) {
        sportService.getSportOrThrow(sportId);
        return teamRepository.findBySportId(sportId).stream()
                .map(teamMapper::toResponse)
                .toList();
    }

    @Transactional
    public TeamResponse create(TeamRequest request) {
        if (teamRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("Team already exists with name: " + request.name());
        }
        Sport sport = sportService.getSportOrThrow(request.sportId());
        City city = cityService.getCityOrThrow(request.cityId());
        Team team = Team.builder()
                .name(request.name())
                .sport(sport)
                .city(city)
                .build();
        return teamMapper.toResponse(teamRepository.save(team));
    }

    @Transactional
    public TeamResponse update(Long id, TeamRequest request) {
        Team team = getTeamOrThrow(id);
        teamRepository.findAll().stream()
                .filter(t -> t.getName().equalsIgnoreCase(request.name()) && !t.getId().equals(id))
                .findFirst()
                .ifPresent(t -> {
                    throw new DuplicateResourceException("Team already exists with name: " + request.name());
                });
        Sport sport = sportService.getSportOrThrow(request.sportId());
        City city = cityService.getCityOrThrow(request.cityId());
        team.setName(request.name());
        team.setSport(sport);
        team.setCity(city);
        return teamMapper.toResponse(teamRepository.save(team));
    }

    @Transactional
    public void delete(Long id) {
        getTeamOrThrow(id);
        teamRepository.deleteById(id);
    }

    public Team getTeamOrThrow(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", id));
    }
}
