package com.sportradar.sportevents.service;

import com.sportradar.sportevents.domain.Country;
import com.sportradar.sportevents.dto.request.CountryRequest;
import com.sportradar.sportevents.dto.response.CountryResponse;
import com.sportradar.sportevents.exception.DuplicateResourceException;
import com.sportradar.sportevents.exception.ResourceNotFoundException;
import com.sportradar.sportevents.mapper.CountryMapper;
import com.sportradar.sportevents.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    public List<CountryResponse> findAll() {
        return countryRepository.findAll().stream()
                .map(countryMapper::toResponse)
                .toList();
    }

    public CountryResponse findById(Long id) {
        return countryMapper.toResponse(getCountryOrThrow(id));
    }

    @Transactional
    public CountryResponse create(CountryRequest request) {
        if (countryRepository.existsByCodeIgnoreCase(request.code())) {
            throw new DuplicateResourceException("Country already exists with code: " + request.code());
        }
        Country country = Country.builder()
                .name(request.name())
                .code(request.code().toUpperCase())
                .build();
        return countryMapper.toResponse(countryRepository.save(country));
    }

    @Transactional
    public CountryResponse update(Long id, CountryRequest request) {
        Country country = getCountryOrThrow(id);
        countryRepository.findByCodeIgnoreCase(request.code())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Country already exists with code: " + request.code());
                });
        country.setName(request.name());
        country.setCode(request.code().toUpperCase());
        return countryMapper.toResponse(countryRepository.save(country));
    }

    @Transactional
    public void delete(Long id) {
        getCountryOrThrow(id);
        countryRepository.deleteById(id);
    }

    public Country getCountryOrThrow(Long id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country", id));
    }
}
