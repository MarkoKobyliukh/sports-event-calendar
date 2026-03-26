package com.sportradar.sportevents.service;

import com.sportradar.sportevents.domain.City;
import com.sportradar.sportevents.domain.Country;
import com.sportradar.sportevents.domain.Sport;
import com.sportradar.sportevents.domain.Team;
import com.sportradar.sportevents.dto.request.TeamRequest;
import com.sportradar.sportevents.dto.response.TeamResponse;
import com.sportradar.sportevents.exception.DuplicateResourceException;
import com.sportradar.sportevents.exception.ResourceNotFoundException;
import com.sportradar.sportevents.mapper.TeamMapper;
import com.sportradar.sportevents.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeamService unit tests")
class TeamServiceTest {

    @Mock private TeamRepository teamRepository;
    @Mock private TeamMapper teamMapper;
    @Mock private SportService sportService;
    @Mock private CityService cityService;
    @InjectMocks private TeamService teamService;

    private Sport sport;
    private City city;
    private Team team;
    private TeamResponse teamResponse;

    @BeforeEach
    void setUp() {
        sport = Sport.builder().id(1L).name("Football").build();
        Country country = Country.builder().id(1L).name("England").code("ENG").build();
        city = City.builder().id(1L).name("London").country(country).build();
        team = Team.builder().id(1L).name("Arsenal").sport(sport).city(city).build();
        teamResponse = new TeamResponse(1L, "Arsenal", 1L, "Football", 1L, "London");
    }

    @Test
    @DisplayName("findAll returns all teams as response list")
    void findAll_returnsAllTeams() {
        when(teamRepository.findAll()).thenReturn(List.of(team));
        when(teamMapper.toResponse(team)).thenReturn(teamResponse);

        List<TeamResponse> result = teamService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Arsenal");
    }

    @Test
    @DisplayName("findById returns response when team exists")
    void findById_whenTeamExists_returnsResponse() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(teamMapper.toResponse(team)).thenReturn(teamResponse);

        TeamResponse result = teamService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Arsenal");
    }

    @Test
    @DisplayName("findById throws ResourceNotFoundException when team not found")
    void findById_whenTeamNotFound_throwsResourceNotFoundException() {
        when(teamRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teamService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Team not found with id: 99");
    }

    @Test
    @DisplayName("findBySport validates sport exists and returns matching teams")
    void findBySport_whenSportExists_returnsTeams() {
        when(sportService.getSportOrThrow(1L)).thenReturn(sport);
        when(teamRepository.findBySportId(1L)).thenReturn(List.of(team));
        when(teamMapper.toResponse(team)).thenReturn(teamResponse);

        List<TeamResponse> result = teamService.findBySport(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).sportName()).isEqualTo("Football");
        verify(sportService).getSportOrThrow(1L);
    }

    @Test
    @DisplayName("create saves and returns new team when name is unique")
    void create_whenNameIsUnique_returnsSavedTeam() {
        TeamRequest request = new TeamRequest("Chelsea", 1L, 1L);
        Team saved = Team.builder().id(2L).name("Chelsea").sport(sport).city(city).build();
        TeamResponse response = new TeamResponse(2L, "Chelsea", 1L, "Football", 1L, "London");

        when(teamRepository.existsByNameIgnoreCase("Chelsea")).thenReturn(false);
        when(sportService.getSportOrThrow(1L)).thenReturn(sport);
        when(cityService.getCityOrThrow(1L)).thenReturn(city);
        when(teamRepository.save(any(Team.class))).thenReturn(saved);
        when(teamMapper.toResponse(saved)).thenReturn(response);

        TeamResponse result = teamService.create(request);

        assertThat(result.name()).isEqualTo("Chelsea");
        verify(teamRepository).save(any(Team.class));
    }

    @Test
    @DisplayName("create throws DuplicateResourceException when name already exists")
    void create_whenNameAlreadyExists_throwsDuplicateResourceException() {
        TeamRequest request = new TeamRequest("Arsenal", 1L, 1L);
        when(teamRepository.existsByNameIgnoreCase("Arsenal")).thenReturn(true);

        assertThatThrownBy(() -> teamService.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Arsenal");

        verify(teamRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete removes team when it exists")
    void delete_whenTeamExists_deletesTeam() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        teamService.delete(1L);

        verify(teamRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete throws ResourceNotFoundException when team not found")
    void delete_whenTeamNotFound_throwsResourceNotFoundException() {
        when(teamRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teamService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
