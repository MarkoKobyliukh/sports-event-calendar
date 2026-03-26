package com.sportradar.sportevents.service;

import com.sportradar.sportevents.domain.*;
import com.sportradar.sportevents.dto.request.EventTeamRequest;
import com.sportradar.sportevents.dto.request.UpdateScoreRequest;
import com.sportradar.sportevents.dto.request.UpdateStatusRequest;
import com.sportradar.sportevents.dto.response.EventResponse;
import com.sportradar.sportevents.exception.DuplicateResourceException;
import com.sportradar.sportevents.exception.ResourceNotFoundException;
import com.sportradar.sportevents.mapper.EventMapper;
import com.sportradar.sportevents.repository.EventRepository;
import com.sportradar.sportevents.repository.EventTeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventService unit tests")
class EventServiceTest {

    @Mock private EventRepository eventRepository;
    @Mock private EventTeamRepository eventTeamRepository;
    @Mock private EventMapper eventMapper;
    @Mock private SportService sportService;
    @Mock private VenueService venueService;
    @Mock private TeamService teamService;
    @InjectMocks private EventService eventService;

    private Event event;
    private EventResponse eventResponse;
    private Sport sport;
    private Team team;

    @BeforeEach
    void setUp() {
        sport = Sport.builder().id(1L).name("Football").build();
        Country country = Country.builder().id(1L).name("England").code("ENG").build();
        City city = City.builder().id(1L).name("London").country(country).build();
        Venue venue = Venue.builder().id(1L).name("Wembley").address("Wembley Way").capacity(90000).city(city).build();
        team = Team.builder().id(2L).name("Arsenal").sport(sport).city(city).build();

        event = Event.builder()
                .id(1L)
                .title("England vs France")
                .eventDate(LocalDate.of(2026, 3, 26))
                .eventTime(LocalTime.of(20, 0))
                .status(EventStatus.SCHEDULED)
                .sport(sport)
                .venue(venue)
                .build();

        eventResponse = new EventResponse(
                1L, "England vs France",
                LocalDate.of(2026, 3, 26), LocalTime.of(20, 0),
                EventStatus.SCHEDULED, 1L, "Football", 1L, "Wembley", List.of()
        );
    }

    @Test
    @DisplayName("findAll returns all events as response list")
    void findAll_returnsAllEvents() {
        when(eventRepository.findAll()).thenReturn(List.of(event));
        when(eventMapper.toResponse(event)).thenReturn(eventResponse);

        List<EventResponse> result = eventService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("England vs France");
    }

    @Test
    @DisplayName("findById returns response when event exists")
    void findById_whenEventExists_returnsResponse() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventMapper.toResponse(event)).thenReturn(eventResponse);

        EventResponse result = eventService.findById(1L);

        assertThat(result.title()).isEqualTo("England vs France");
        assertThat(result.status()).isEqualTo(EventStatus.SCHEDULED);
    }

    @Test
    @DisplayName("findById throws ResourceNotFoundException when event not found")
    void findById_whenEventNotFound_throwsResourceNotFoundException() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Event not found with id: 99");
    }

    @Test
    @DisplayName("findByStatus returns only events matching given status")
    void findByStatus_returnsMatchingEvents() {
        when(eventRepository.findByStatus(EventStatus.SCHEDULED)).thenReturn(List.of(event));
        when(eventMapper.toResponse(event)).thenReturn(eventResponse);

        List<EventResponse> result = eventService.findByStatus(EventStatus.SCHEDULED);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo(EventStatus.SCHEDULED);
    }

    @Test
    @DisplayName("updateStatus changes event status and persists it")
    void updateStatus_whenEventExists_updatesStatus() {
        UpdateStatusRequest request = new UpdateStatusRequest(EventStatus.LIVE);
        EventResponse liveResponse = new EventResponse(
                1L, "England vs France",
                LocalDate.of(2026, 3, 26), LocalTime.of(20, 0),
                EventStatus.LIVE, 1L, "Football", 1L, "Wembley", List.of()
        );

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toResponse(event)).thenReturn(liveResponse);

        EventResponse result = eventService.updateStatus(1L, request);

        assertThat(result.status()).isEqualTo(EventStatus.LIVE);
        verify(eventRepository).save(event);
    }

    @Test
    @DisplayName("updateStatus throws ResourceNotFoundException when event not found")
    void updateStatus_whenEventNotFound_throwsResourceNotFoundException() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.updateStatus(99L, new UpdateStatusRequest(EventStatus.LIVE)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("addTeam throws DuplicateResourceException when team already in event")
    void addTeam_whenTeamAlreadyInEvent_throwsDuplicateResourceException() {
        EventTeamRequest request = new EventTeamRequest(2L, true, null);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(teamService.getTeamOrThrow(2L)).thenReturn(team);
        when(eventTeamRepository.existsByEventIdAndTeamId(1L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> eventService.addTeam(1L, request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already part of event");

        verify(eventTeamRepository, never()).save(any());
    }

    @Test
    @DisplayName("addTeam saves EventTeam when team is new to event")
    void addTeam_whenTeamNotInEvent_savesEventTeam() {
        EventTeamRequest request = new EventTeamRequest(2L, true, null);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event)).thenReturn(Optional.of(event));
        when(teamService.getTeamOrThrow(2L)).thenReturn(team);
        when(eventTeamRepository.existsByEventIdAndTeamId(1L, 2L)).thenReturn(false);
        when(eventTeamRepository.save(any(EventTeam.class))).thenReturn(new EventTeam());
        when(eventMapper.toResponse(event)).thenReturn(eventResponse);

        eventService.addTeam(1L, request);

        verify(eventTeamRepository).save(any(EventTeam.class));
    }

    @Test
    @DisplayName("updateScore updates score when team is in event")
    void updateScore_whenValid_updatesScore() {
        UpdateScoreRequest request = new UpdateScoreRequest(2);
        EventTeam eventTeam = EventTeam.builder().id(1L).event(event).team(team).home(true).score(0).build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventTeamRepository.findByEventIdAndTeamId(1L, 2L)).thenReturn(Optional.of(eventTeam));
        when(eventTeamRepository.save(eventTeam)).thenReturn(eventTeam);
        when(eventMapper.toResponse(event)).thenReturn(eventResponse);

        eventService.updateScore(1L, 2L, request);

        assertThat(eventTeam.getScore()).isEqualTo(2);
        verify(eventTeamRepository).save(eventTeam);
    }

    @Test
    @DisplayName("updateScore throws ResourceNotFoundException when team not in event")
    void updateScore_whenTeamNotInEvent_throwsResourceNotFoundException() {
        UpdateScoreRequest request = new UpdateScoreRequest(3);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventTeamRepository.findByEventIdAndTeamId(1L, 99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.updateScore(1L, 99L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("removeTeam deletes EventTeam entry when team is in event")
    void removeTeam_whenTeamInEvent_removesEventTeam() {
        EventTeam eventTeam = EventTeam.builder().id(1L).event(event).team(team).build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventTeamRepository.findByEventIdAndTeamId(1L, 2L)).thenReturn(Optional.of(eventTeam));

        eventService.removeTeam(1L, 2L);

        verify(eventTeamRepository).delete(eventTeam);
    }

    @Test
    @DisplayName("delete removes event when it exists")
    void delete_whenEventExists_deletesEvent() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        eventService.delete(1L);

        verify(eventRepository).deleteById(1L);
    }
}
