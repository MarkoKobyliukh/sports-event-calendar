package com.sportradar.sportevents.repository;

import com.sportradar.sportevents.domain.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    Optional<Country> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);
}
