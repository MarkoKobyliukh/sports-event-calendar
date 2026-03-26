package com.sportradar.sportevents.service;

import com.sportradar.sportevents.domain.*;
import com.sportradar.sportevents.dto.request.EventRequest;
import com.sportradar.sportevents.dto.request.EventTeamRequest;
import com.sportradar.sportevents.dto.request.UpdateScoreRequest;
import com.sportradar.sportevents.dto.request.UpdateStatusRequest;
import com.sportradar.sportevents.dto.response.EventResponse;
import com.sportradar.sportevents.exception.DuplicateResourceException;
import com.sportradar.sportevents.exception.InvalidOperationException;
import com.sportradar.sportevents.exception.ResourceNotFoundException;
import com.sportradar.sportevents.mapper.EventMapper;
import com.sportradar.sportevents.repository.EventRepository;
import com.sportradar.sportevents.repository.EventTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventTeamRepository eventTeamRepository;
    private final EventMapper eventMapper;
    private final SportService sportService;
    private final VenueService venueService;
    private final TeamService teamService;

    public List<EventResponse> findAll() {
        return eventRepository.findAll().stream()
                .map(eventMapper::toResponse)
                .toList();
    }

    public EventResponse findById(Long id) {
        return eventMapper.toResponse(getEventOrThrow(id));
    }

    public List<EventResponse> findByStatus(EventStatus status) {
        return eventRepository.findByStatus(status).stream()
                .map(eventMapper::toResponse)
                .toList();
    }

    public List<EventResponse> findBySport(Long sportId) {
        sportService.getSportOrThrow(sportId);
        return eventRepository.findBySportId(sportId).stream()
                .map(eventMapper::toResponse)
                .toList();
    }

    public List<EventResponse> findByDate(LocalDate date) {
        return eventRepository.findByEventDate(date).stream()
                .map(eventMapper::toResponse)
                .toList();
    }

    public List<EventResponse> findByDateRange(LocalDate from, LocalDate to) {
        return eventRepository.findByEventDateBetween(from, to).stream()
                .map(eventMapper::toResponse)
                .toList();
    }

    public List<EventResponse> findByTeam(Long teamId) {
        teamService.getTeamOrThrow(teamId);
        return eventRepository.findByTeamId(teamId).stream()
                .map(eventMapper::toResponse)
                .toList();
    }

    @Transactional
    public EventResponse create(EventRequest request) {
        Sport sport = sportService.getSportOrThrow(request.sportId());
        Venue venue = venueService.getVenueOrThrow(request.venueId());
        EventStatus status = request.status() != null ? request.status() : EventStatus.SCHEDULED;
        Event event = Event.builder()
                .title(request.title())
                .eventDate(request.eventDate())
                .eventTime(request.eventTime())
                .status(status)
                .sport(sport)
                .venue(venue)
                .build();
        return eventMapper.toResponse(eventRepository.save(event));
    }

    @Transactional
    public EventResponse update(Long id, EventRequest request) {
        Event event = getEventOrThrow(id);
        Sport sport = sportService.getSportOrThrow(request.sportId());
        Venue venue = venueService.getVenueOrThrow(request.venueId());
        event.setTitle(request.title());
        event.setEventDate(request.eventDate());
        event.setEventTime(request.eventTime());
        if (request.status() != null) {
            event.setStatus(request.status());
        }
        event.setSport(sport);
        event.setVenue(venue);
        return eventMapper.toResponse(eventRepository.save(event));
    }

    @Transactional
    public EventResponse updateStatus(Long id, UpdateStatusRequest request) {
        Event event = getEventOrThrow(id);
        event.setStatus(request.status());
        return eventMapper.toResponse(eventRepository.save(event));
    }

    @Transactional
    public EventResponse addTeam(Long eventId, EventTeamRequest request) {
        Event event = getEventOrThrow(eventId);
        Team team = teamService.getTeamOrThrow(request.teamId());

        if (eventTeamRepository.existsByEventIdAndTeamId(eventId, request.teamId())) {
            throw new DuplicateResourceException("Team " + request.teamId() + " is already part of event " + eventId);
        }

        EventTeam eventTeam = EventTeam.builder()
                .event(event)
                .team(team)
                .home(request.home())
                .score(request.score())
                .build();
        eventTeamRepository.save(eventTeam);

        return eventMapper.toResponse(getEventOrThrow(eventId));
    }

    @Transactional
    public EventResponse updateScore(Long eventId, Long teamId, UpdateScoreRequest request) {
        getEventOrThrow(eventId);
        EventTeam eventTeam = eventTeamRepository.findByEventIdAndTeamId(eventId, teamId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Team " + teamId + " is not part of event " + eventId));
        eventTeam.setScore(request.score());
        eventTeamRepository.save(eventTeam);
        return eventMapper.toResponse(getEventOrThrow(eventId));
    }

    @Transactional
    public void removeTeam(Long eventId, Long teamId) {
        getEventOrThrow(eventId);
        EventTeam eventTeam = eventTeamRepository.findByEventIdAndTeamId(eventId, teamId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Team " + teamId + " is not part of event " + eventId));
        eventTeamRepository.delete(eventTeam);
    }

    @Transactional
    public void delete(Long id) {
        getEventOrThrow(id);
        eventRepository.deleteById(id);
    }

    public Event getEventOrThrow(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id));
    }
}
