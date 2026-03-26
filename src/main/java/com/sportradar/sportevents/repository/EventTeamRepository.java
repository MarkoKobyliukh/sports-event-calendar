package com.sportradar.sportevents.repository;

import com.sportradar.sportevents.domain.EventTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventTeamRepository extends JpaRepository<EventTeam, Long> {

    List<EventTeam> findByEventId(Long eventId);

    List<EventTeam> findByTeamId(Long teamId);

    Optional<EventTeam> findByEventIdAndTeamId(Long eventId, Long teamId);

    boolean existsByEventIdAndTeamId(Long eventId, Long teamId);
}
