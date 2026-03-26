package com.sportradar.sportevents.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportradar.sportevents.dto.request.SportRequest;
import com.sportradar.sportevents.dto.response.SportResponse;
import com.sportradar.sportevents.exception.DuplicateResourceException;
import com.sportradar.sportevents.exception.GlobalExceptionHandler;
import com.sportradar.sportevents.exception.ResourceNotFoundException;
import com.sportradar.sportevents.service.SportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SportController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("SportController integration tests")
class SportControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean  private SportService sportService;

    @Test
    @DisplayName("GET /api/sports returns 200 with list of sports")
    void getAllSports_returns200WithList() throws Exception {
        when(sportService.findAll()).thenReturn(List.of(
                new SportResponse(1L, "Football"),
                new SportResponse(2L, "Basketball")
        ));

        mockMvc.perform(get("/api/sports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Football"));
    }

    @Test
    @DisplayName("GET /api/sports/{id} returns 200 when sport exists")
    void getSportById_whenExists_returns200() throws Exception {
        when(sportService.findById(1L)).thenReturn(new SportResponse(1L, "Football"));

        mockMvc.perform(get("/api/sports/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Football"));
    }

    @Test
    @DisplayName("GET /api/sports/{id} returns 404 when sport not found")
    void getSportById_whenNotFound_returns404() throws Exception {
        when(sportService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Sport", 99L));

        mockMvc.perform(get("/api/sports/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Sport not found with id: 99"));
    }

    @Test
    @DisplayName("POST /api/sports returns 201 with created sport")
    void createSport_withValidRequest_returns201() throws Exception {
        SportRequest request = new SportRequest("Tennis");
        SportResponse response = new SportResponse(3L, "Tennis");

        when(sportService.create(any(SportRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/sports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Tennis"));
    }

    @Test
    @DisplayName("POST /api/sports returns 400 when name is blank")
    void createSport_withBlankName_returns400() throws Exception {
        mockMvc.perform(post("/api/sports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(sportService, never()).create(any());
    }

    @Test
    @DisplayName("POST /api/sports returns 409 when name already exists")
    void createSport_whenNameDuplicate_returns409() throws Exception {
        SportRequest request = new SportRequest("Football");

        when(sportService.create(any(SportRequest.class)))
                .thenThrow(new DuplicateResourceException("Sport already exists with name: Football"));

        mockMvc.perform(post("/api/sports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("PUT /api/sports/{id} returns 200 with updated sport")
    void updateSport_withValidRequest_returns200() throws Exception {
        SportRequest request = new SportRequest("Rugby");
        SportResponse response = new SportResponse(1L, "Rugby");

        when(sportService.update(eq(1L), any(SportRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/sports/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Rugby"));
    }

    @Test
    @DisplayName("DELETE /api/sports/{id} returns 204 when sport deleted")
    void deleteSport_whenExists_returns204() throws Exception {
        doNothing().when(sportService).delete(1L);

        mockMvc.perform(delete("/api/sports/1"))
                .andExpect(status().isNoContent());

        verify(sportService).delete(1L);
    }

    @Test
    @DisplayName("DELETE /api/sports/{id} returns 404 when sport not found")
    void deleteSport_whenNotFound_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Sport", 99L))
                .when(sportService).delete(99L);

        mockMvc.perform(delete("/api/sports/99"))
                .andExpect(status().isNotFound());
    }
}
