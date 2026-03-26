package com.sportradar.sportevents.repository;

import com.sportradar.sportevents.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findBySportId(Long sportId);

    List<Team> findByCityId(Long cityId);

    boolean existsByNameIgnoreCase(String name);
}
