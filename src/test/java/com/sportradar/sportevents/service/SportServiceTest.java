package com.sportradar.sportevents.service;

import com.sportradar.sportevents.domain.Sport;
import com.sportradar.sportevents.dto.request.SportRequest;
import com.sportradar.sportevents.dto.response.SportResponse;
import com.sportradar.sportevents.exception.DuplicateResourceException;
import com.sportradar.sportevents.exception.ResourceNotFoundException;
import com.sportradar.sportevents.mapper.SportMapper;
import com.sportradar.sportevents.repository.SportRepository;
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
@DisplayName("SportService unit tests")
class SportServiceTest {

    @Mock private SportRepository sportRepository;
    @Mock private SportMapper sportMapper;
    @InjectMocks private SportService sportService;

    private Sport sport;
    private SportResponse sportResponse;

    @BeforeEach
    void setUp() {
        sport = Sport.builder().id(1L).name("Football").build();
        sportResponse = new SportResponse(1L, "Football");
    }

    @Test
    @DisplayName("findAll returns all sports as response list")
    void findAll_whenSportsExist_returnsAllSports() {
        when(sportRepository.findAll()).thenReturn(List.of(sport));
        when(sportMapper.toResponse(sport)).thenReturn(sportResponse);

        List<SportResponse> result = sportService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Football");
        verify(sportRepository).findAll();
    }

    @Test
    @DisplayName("findById returns response when sport exists")
    void findById_whenSportExists_returnsResponse() {
        when(sportRepository.findById(1L)).thenReturn(Optional.of(sport));
        when(sportMapper.toResponse(sport)).thenReturn(sportResponse);

        SportResponse result = sportService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Football");
    }

    @Test
    @DisplayName("findById throws ResourceNotFoundException when sport not found")
    void findById_whenSportNotFound_throwsResourceNotFoundException() {
        when(sportRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sportService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Sport not found with id: 99");
    }

    @Test
    @DisplayName("create saves and returns new sport when name is unique")
    void create_whenNameIsUnique_returnsSavedSport() {
        SportRequest request = new SportRequest("Tennis");
        Sport saved = Sport.builder().id(2L).name("Tennis").build();
        SportResponse response = new SportResponse(2L, "Tennis");

        when(sportRepository.existsByNameIgnoreCase("Tennis")).thenReturn(false);
        when(sportRepository.save(any(Sport.class))).thenReturn(saved);
        when(sportMapper.toResponse(saved)).thenReturn(response);

        SportResponse result = sportService.create(request);

        assertThat(result.name()).isEqualTo("Tennis");
        verify(sportRepository).save(any(Sport.class));
    }

    @Test
    @DisplayName("create throws DuplicateResourceException when name already exists")
    void create_whenNameAlreadyExists_throwsDuplicateResourceException() {
        SportRequest request = new SportRequest("Football");
        when(sportRepository.existsByNameIgnoreCase("Football")).thenReturn(true);

        assertThatThrownBy(() -> sportService.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Football");

        verify(sportRepository, never()).save(any());
    }

    @Test
    @DisplayName("update throws DuplicateResourceException when name taken by another sport")
    void update_whenNameTakenByOtherSport_throwsDuplicateResourceException() {
        Sport otherSport = Sport.builder().id(2L).name("Football").build();
        SportRequest request = new SportRequest("Football");

        when(sportRepository.findById(1L)).thenReturn(Optional.of(sport));
        when(sportRepository.findByNameIgnoreCase("Football")).thenReturn(Optional.of(otherSport));

        assertThatThrownBy(() -> sportService.update(1L, request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Football");
    }

    @Test
    @DisplayName("update allows same sport to keep its own name")
    void update_whenSameSportKeepsName_updatesSuccessfully() {
        SportRequest request = new SportRequest("Football");

        when(sportRepository.findById(1L)).thenReturn(Optional.of(sport));
        when(sportRepository.findByNameIgnoreCase("Football")).thenReturn(Optional.of(sport));
        when(sportRepository.save(sport)).thenReturn(sport);
        when(sportMapper.toResponse(sport)).thenReturn(sportResponse);

        SportResponse result = sportService.update(1L, request);

        assertThat(result.name()).isEqualTo("Football");
        verify(sportRepository).save(sport);
    }

    @Test
    @DisplayName("delete removes sport when it exists")
    void delete_whenSportExists_deletesSport() {
        when(sportRepository.findById(1L)).thenReturn(Optional.of(sport));

        sportService.delete(1L);

        verify(sportRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete throws ResourceNotFoundException when sport not found")
    void delete_whenSportNotFound_throwsResourceNotFoundException() {
        when(sportRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sportService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
