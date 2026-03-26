package com.sportradar.sportevents.controller;

import com.sportradar.sportevents.dto.request.CountryRequest;
import com.sportradar.sportevents.dto.response.CountryResponse;
import com.sportradar.sportevents.service.CountryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    @GetMapping
    public ResponseEntity<List<CountryResponse>> findAll() {
        return ResponseEntity.ok(countryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CountryResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(countryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CountryResponse> create(@Valid @RequestBody CountryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(countryService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CountryResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody CountryRequest request) {
        return ResponseEntity.ok(countryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        countryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
