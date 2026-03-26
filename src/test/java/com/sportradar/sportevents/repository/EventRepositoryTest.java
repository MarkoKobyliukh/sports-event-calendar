package com.sportradar.sportevents.repository;

import com.sportradar.sportevents.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DisplayName("EventRepository query tests")
class EventRepositoryTest {

    @Autowired private EventRepository eventRepository;
    @Autowired private TestEntityManager entityManager;

    private Sport football;
    private Sport basketball;
    private Venue wembley;
    private Team arsenal;
    private Team chelsea;

    @BeforeEach
    void setUp() {
        Country england = Country.builder().name("England").code("ENG").build();
        entityManager.persist(england);

        City london = City.builder().name("London").country(england).build();
        entityManager.persist(london);

        football   = Sport.builder().name("Football").build();
        basketball = Sport.builder().name("Basketball").build();
        entityManager.persist(football);
        entityManager.persist(basketball);

        wembley = Venue.builder().name("Wembley").address("Wembley Way").capacity(90000).city(london).build();
        entityManager.persist(wembley);

        arsenal = Team.builder().name("Arsenal").sport(football).city(london).build();
        chelsea = Team.builder().name("Chelsea").sport(football).city(london).build();
        entityManager.persist(arsenal);
        entityManager.persist(chelsea);

        entityManager.flush();
    }

    private Event persistEvent(String title, LocalDate date, EventStatus status, Sport sport) {
        Event event = Event.builder()
                .title(title)
                .eventDate(date)
                .eventTime(LocalTime.of(20, 0))
                .status(status)
                .sport(sport)
                .venue(wembley)
                .build();
        return entityManager.persistAndFlush(event);
    }

    @Test
    @DisplayName("findByStatus returns only events with the given status")
    void findByStatus_returnsMatchingEvents() {
        persistEvent("Match A", LocalDate.of(2026, 3, 26), EventStatus.LIVE, football);
        persistEvent("Match B", LocalDate.of(2026, 3, 27), EventStatus.SCHEDULED, football);
        persistEvent("Match C", LocalDate.of(2026, 3, 28), EventStatus.SCHEDULED, football);

        List<Event> liveEvents = eventRepository.findByStatus(EventStatus.LIVE);
        List<Event> scheduledEvents = eventRepository.findByStatus(EventStatus.SCHEDULED);

        assertThat(liveEvents).hasSize(1);
        assertThat(liveEvents.get(0).getTitle()).isEqualTo("Match A");
        assertThat(scheduledEvents).hasSize(2);
    }

    @Test
    @DisplayName("findBySportId returns only events for the given sport")
    void findBySportId_returnsEventsForSport() {
        persistEvent("Football Match", LocalDate.of(2026, 3, 26), EventStatus.SCHEDULED, football);
        persistEvent("Basketball Match", LocalDate.of(2026, 3, 26), EventStatus.SCHEDULED, basketball);

        List<Event> result = eventRepository.findBySportId(football.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSport().getName()).isEqualTo("Football");
    }

    @Test
    @DisplayName("findByEventDate returns only events on the exact date")
    void findByEventDate_returnsEventsOnDate() {
        LocalDate target = LocalDate.of(2026, 4, 1);
        persistEvent("Same Day A", target, EventStatus.SCHEDULED, football);
        persistEvent("Same Day B", target, EventStatus.SCHEDULED, football);
        persistEvent("Different Day", LocalDate.of(2026, 4, 2), EventStatus.SCHEDULED, football);

        List<Event> result = eventRepository.findByEventDate(target);

        assertThat(result).hasSize(2);
        result.forEach(e -> assertThat(e.getEventDate()).isEqualTo(target));
    }

    @Test
    @DisplayName("findByEventDateBetween returns events within the date range inclusive")
    void findByEventDateBetween_returnsEventsInRange() {
        persistEvent("In Range 1",  LocalDate.of(2026, 4, 1), EventStatus.SCHEDULED, football);
        persistEvent("In Range 2",  LocalDate.of(2026, 4, 5), EventStatus.SCHEDULED, football);
        persistEvent("In Range 3",  LocalDate.of(2026, 4, 10), EventStatus.SCHEDULED, football);
        persistEvent("Out of Range", LocalDate.of(2026, 4, 15), EventStatus.SCHEDULED, football);

        List<Event> result = eventRepository.findByEventDateBetween(
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 10)
        );

        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("findByTeamId returns events that contain the specified team")
    void findByTeamId_returnsEventsContainingTeam() {
        Event event = persistEvent("Arsenal vs Chelsea", LocalDate.of(2026, 4, 1), EventStatus.SCHEDULED, football);

        EventTeam homeEntry = EventTeam.builder().event(event).team(arsenal).home(true).score(0).build();
        EventTeam awayEntry = EventTeam.builder().event(event).team(chelsea).home(false).score(0).build();
        entityManager.persist(homeEntry);
        entityManager.persist(awayEntry);
        entityManager.flush();

        List<Event> result = eventRepository.findByTeamId(arsenal.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Arsenal vs Chelsea");
    }

    @Test
    @DisplayName("findByStatus returns empty list when no events match")
    void findByStatus_whenNoMatches_returnsEmptyList() {
        persistEvent("Finished Match", LocalDate.of(2026, 3, 26), EventStatus.FINISHED, football);

        List<Event> result = eventRepository.findByStatus(EventStatus.LIVE);

        assertThat(result).isEmpty();
    }
}
