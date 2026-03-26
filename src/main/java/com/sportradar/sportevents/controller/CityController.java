package com.sportradar.sportevents.controller;

import com.sportradar.sportevents.dto.request.CityRequest;
import com.sportradar.sportevents.dto.response.CityResponse;
import com.sportradar.sportevents.service.CityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @GetMapping
    public ResponseEntity<List<CityResponse>> findAll(@RequestParam(required = false) Long countryId) {
        if (countryId != null) {
            return ResponseEntity.ok(cityService.findByCountry(countryId));
        }
        return ResponseEntity.ok(cityService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CityResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(cityService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CityResponse> create(@Valid @RequestBody CityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cityService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CityResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody CityRequest request) {
        return ResponseEntity.ok(cityService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
