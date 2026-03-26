package com.sportradar.sportevents.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportradar.sportevents.domain.EventStatus;
import com.sportradar.sportevents.dto.request.EventTeamRequest;
import com.sportradar.sportevents.dto.request.UpdateScoreRequest;
import com.sportradar.sportevents.dto.request.UpdateStatusRequest;
import com.sportradar.sportevents.dto.response.EventResponse;
import com.sportradar.sportevents.dto.response.EventTeamResponse;
import com.sportradar.sportevents.exception.DuplicateResourceException;
import com.sportradar.sportevents.exception.GlobalExceptionHandler;
import com.sportradar.sportevents.exception.ResourceNotFoundException;
import com.sportradar.sportevents.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("EventController integration tests")
class EventControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean  private EventService eventService;

    private EventResponse eventResponse;

    @BeforeEach
    void setUp() {
        EventTeamResponse homeTeam = new EventTeamResponse(1L, 10L, "Arsenal",  true,  2);
        EventTeamResponse awayTeam = new EventTeamResponse(2L, 11L, "Chelsea",  false, 1);

        eventResponse = new EventResponse(
                1L, "Arsenal vs Chelsea",
                LocalDate.of(2026, 3, 26), LocalTime.of(20, 0),
                EventStatus.LIVE, 1L, "Football", 1L, "Emirates Stadium",
                List.of(homeTeam, awayTeam)
        );
    }

    @Test
    @DisplayName("GET /api/events returns 200 with all events")
    void getAllEvents_returns200WithList() throws Exception {
        when(eventService.findAll()).thenReturn(List.of(eventResponse));

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Arsenal vs Chelsea"))
                .andExpect(jsonPath("$[0].status").value("LIVE"));
    }

    @Test
    @DisplayName("GET /api/events?status=LIVE delegates to findByStatus")
    void getAllEvents_withStatusFilter_callsFindByStatus() throws Exception {
        when(eventService.findByStatus(EventStatus.LIVE)).thenReturn(List.of(eventResponse));

        mockMvc.perform(get("/api/events").param("status", "LIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("LIVE"));

        verify(eventService).findByStatus(EventStatus.LIVE);
        verify(eventService, never()).findAll();
    }

    @Test
    @DisplayName("GET /api/events/{id} returns 200 with event detail")
    void getEventById_whenExists_returns200() throws Exception {
        when(eventService.findById(1L)).thenReturn(eventResponse);

        mockMvc.perform(get("/api/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Arsenal vs Chelsea"))
                .andExpect(jsonPath("$.teams.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/events/{id} returns 404 when event not found")
    void getEventById_whenNotFound_returns404() throws Exception {
        when(eventService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Event", 99L));

        mockMvc.perform(get("/api/events/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("PATCH /api/events/{id}/status returns 200 with updated status")
    void updateStatus_withValidRequest_returns200() throws Exception {
        UpdateStatusRequest request = new UpdateStatusRequest(EventStatus.FINISHED);
        EventResponse finished = new EventResponse(
                1L, "Arsenal vs Chelsea",
                LocalDate.of(2026, 3, 26), LocalTime.of(20, 0),
                EventStatus.FINISHED, 1L, "Football", 1L, "Emirates Stadium", List.of()
        );

        when(eventService.updateStatus(eq(1L), any(UpdateStatusRequest.class))).thenReturn(finished);

        mockMvc.perform(patch("/api/events/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FINISHED"));
    }

    @Test
    @DisplayName("PATCH /api/events/{id}/status returns 400 when status is null")
    void updateStatus_withNullStatus_returns400() throws Exception {
        mockMvc.perform(patch("/api/events/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": null}"))
                .andExpect(status().isBadRequest());

        verify(eventService, never()).updateStatus(any(), any());
    }

    @Test
    @DisplayName("POST /api/events/{id}/teams returns 409 when team already in event")
    void addTeam_whenTeamAlreadyInEvent_returns409() throws Exception {
        EventTeamRequest request = new EventTeamRequest(10L, true, null);

        when(eventService.addTeam(eq(1L), any(EventTeamRequest.class)))
                .thenThrow(new DuplicateResourceException("Team 10 is already part of event 1"));

        mockMvc.perform(post("/api/events/1/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("PATCH /api/events/{id}/teams/{teamId}/score returns 404 when team not in event")
    void updateScore_whenTeamNotInEvent_returns404() throws Exception {
        UpdateScoreRequest request = new UpdateScoreRequest(3);

        when(eventService.updateScore(eq(1L), eq(99L), any(UpdateScoreRequest.class)))
                .thenThrow(new ResourceNotFoundException("Team 99 is not part of event 1"));

        mockMvc.perform(patch("/api/events/1/teams/99/score")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/events/{id}/teams/{teamId}/score returns 400 when score is negative")
    void updateScore_withNegativeScore_returns400() throws Exception {
        mockMvc.perform(patch("/api/events/1/teams/2/score")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"score\": -1}"))
                .andExpect(status().isBadRequest());

        verify(eventService, never()).updateScore(any(), any(), any());
    }

    @Test
    @DisplayName("DELETE /api/events/{id} returns 204 when event deleted")
    void deleteEvent_whenExists_returns204() throws Exception {
        doNothing().when(eventService).delete(1L);

        mockMvc.perform(delete("/api/events/1"))
                .andExpect(status().isNoContent());
    }
}
