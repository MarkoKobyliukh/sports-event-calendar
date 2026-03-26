package com.sportradar.sportevents.repository;

import com.sportradar.sportevents.domain.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {

    List<Venue> findByCityId(Long cityId);
}
