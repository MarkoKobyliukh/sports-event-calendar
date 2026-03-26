package com.sportradar.sportevents.service;

import com.sportradar.sportevents.domain.City;
import com.sportradar.sportevents.domain.Country;
import com.sportradar.sportevents.dto.request.CityRequest;
import com.sportradar.sportevents.dto.response.CityResponse;
import com.sportradar.sportevents.exception.ResourceNotFoundException;
import com.sportradar.sportevents.mapper.CityMapper;
import com.sportradar.sportevents.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;
    private final CityMapper cityMapper;
    private final CountryService countryService;

    public List<CityResponse> findAll() {
        return cityRepository.findAll().stream()
                .map(cityMapper::toResponse)
                .toList();
    }

    public CityResponse findById(Long id) {
        return cityMapper.toResponse(getCityOrThrow(id));
    }

    public List<CityResponse> findByCountry(Long countryId) {
        countryService.getCountryOrThrow(countryId);
        return cityRepository.findByCountryId(countryId).stream()
                .map(cityMapper::toResponse)
                .toList();
    }

    @Transactional
    public CityResponse create(CityRequest request) {
        Country country = countryService.getCountryOrThrow(request.countryId());
        City city = City.builder()
                .name(request.name())
                .country(country)
                .build();
        return cityMapper.toResponse(cityRepository.save(city));
    }

    @Transactional
    public CityResponse update(Long id, CityRequest request) {
        City city = getCityOrThrow(id);
        Country country = countryService.getCountryOrThrow(request.countryId());
        city.setName(request.name());
        city.setCountry(country);
        return cityMapper.toResponse(cityRepository.save(city));
    }

    @Transactional
    public void delete(Long id) {
        getCityOrThrow(id);
        cityRepository.deleteById(id);
    }

    public City getCityOrThrow(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City", id));
    }
}
