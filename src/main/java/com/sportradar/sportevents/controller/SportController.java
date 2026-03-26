package com.sportradar.sportevents.controller;

import com.sportradar.sportevents.dto.request.SportRequest;
import com.sportradar.sportevents.dto.response.SportResponse;
import com.sportradar.sportevents.service.SportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sports")
@RequiredArgsConstructor
public class SportController {

    private final SportService sportService;

    @GetMapping
    public ResponseEntity<List<SportResponse>> findAll() {
        return ResponseEntity.ok(sportService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SportResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(sportService.findById(id));
    }

    @PostMapping
    public ResponseEntity<SportResponse> create(@Valid @RequestBody SportRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sportService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SportResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody SportRequest request) {
        return ResponseEntity.ok(sportService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sportService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
