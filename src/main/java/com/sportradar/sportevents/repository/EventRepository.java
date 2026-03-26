package com.sportradar.sportevents.repository;

import com.sportradar.sportevents.domain.Event;
import com.sportradar.sportevents.domain.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByStatus(EventStatus status);

    List<Event> findBySportId(Long sportId);

    List<Event> findByEventDateBetween(LocalDate from, LocalDate to);

    List<Event> findByEventDate(LocalDate date);

    @Query("SELECT e FROM Event e JOIN e.eventTeams et WHERE et.team.id = :teamId")
    List<Event> findByTeamId(@Param("teamId") Long teamId);

    @Query("SELECT e FROM Event e WHERE e.status = :status AND e.sport.id = :sportId")
    List<Event> findByStatusAndSportId(@Param("status") EventStatus status, @Param("sportId") Long sportId);
}
