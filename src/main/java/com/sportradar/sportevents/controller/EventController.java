package com.sportradar.sportevents.controller;

import com.sportradar.sportevents.domain.EventStatus;
import com.sportradar.sportevents.dto.request.EventRequest;
import com.sportradar.sportevents.dto.request.EventTeamRequest;
import com.sportradar.sportevents.dto.request.UpdateScoreRequest;
import com.sportradar.sportevents.dto.request.UpdateStatusRequest;
import com.sportradar.sportevents.dto.response.EventResponse;
import com.sportradar.sportevents.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventResponse>> findAll(
            @RequestParam(required = false) EventStatus status,
            @RequestParam(required = false) Long sportId,
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        if (status != null)           return ResponseEntity.ok(eventService.findByStatus(status));
        if (sportId != null)          return ResponseEntity.ok(eventService.findBySport(sportId));
        if (teamId != null)           return ResponseEntity.ok(eventService.findByTeam(teamId));
        if (date != null)             return ResponseEntity.ok(eventService.findByDate(date));
        if (from != null && to != null) return ResponseEntity.ok(eventService.findByDateRange(from, to));

        return ResponseEntity.ok(eventService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.findById(id));
    }

    @PostMapping
    public ResponseEntity<EventResponse> create(@Valid @RequestBody EventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody EventRequest request) {
        return ResponseEntity.ok(eventService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<EventResponse> updateStatus(@PathVariable Long id,
                                                      @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(eventService.updateStatus(id, request));
    }

    @PostMapping("/{id}/teams")
    public ResponseEntity<EventResponse> addTeam(@PathVariable Long id,
                                                 @Valid @RequestBody EventTeamRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.addTeam(id, request));
    }

    @PatchMapping("/{id}/teams/{teamId}/score")
    public ResponseEntity<EventResponse> updateScore(@PathVariable Long id,
                                                     @PathVariable Long teamId,
                                                     @Valid @RequestBody UpdateScoreRequest request) {
        return ResponseEntity.ok(eventService.updateScore(id, teamId, request));
    }

    @DeleteMapping("/{id}/teams/{teamId}")
    public ResponseEntity<Void> removeTeam(@PathVariable Long id,
                                           @PathVariable Long teamId) {
        eventService.removeTeam(id, teamId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
