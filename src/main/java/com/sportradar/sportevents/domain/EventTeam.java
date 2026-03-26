package com.sportradar.sportevents.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "event_team",
    uniqueConstraints = @UniqueConstraint(columnNames = {"_event_id", "_team_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "_event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "_team_id", nullable = false)
    private Team team;

    @Column(name = "is_home", nullable = false)
    private boolean home;

    @Column
    private Integer score;
}
