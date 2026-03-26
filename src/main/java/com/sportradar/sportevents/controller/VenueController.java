package com.sportradar.sportevents.controller;

import com.sportradar.sportevents.dto.request.VenueRequest;
import com.sportradar.sportevents.dto.response.VenueResponse;
import com.sportradar.sportevents.service.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
public class VenueController {

    private final VenueService venueService;

    @GetMapping
    public ResponseEntity<List<VenueResponse>> findAll(@RequestParam(required = false) Long cityId) {
        if (cityId != null) {
            return ResponseEntity.ok(venueService.findByCity(cityId));
        }
        return ResponseEntity.ok(venueService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VenueResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(venueService.findById(id));
    }

    @PostMapping
    public ResponseEntity<VenueResponse> create(@Valid @RequestBody VenueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(venueService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VenueResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody VenueRequest request) {
        return ResponseEntity.ok(venueService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        venueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
