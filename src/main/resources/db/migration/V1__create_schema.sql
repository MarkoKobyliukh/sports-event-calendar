-- ─────────────────────────────────────────────────────────────────────────────
-- V1 — Sports Event Calendar: full schema (3NF)
-- FK columns prefixed with _ per exercise specification
-- ─────────────────────────────────────────────────────────────────────────────

-- sport
CREATE TABLE sport (
    id   BIGSERIAL    PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- country
CREATE TABLE country (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(3)   NOT NULL UNIQUE
);

-- city  (→ country)
CREATE TABLE city (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    _country_id BIGINT       NOT NULL,
    CONSTRAINT fk_city_country FOREIGN KEY (_country_id) REFERENCES country (id)
);

-- venue  (→ city)
CREATE TABLE venue (
    id       BIGSERIAL    PRIMARY KEY,
    name     VARCHAR(150) NOT NULL,
    address  VARCHAR(255),
    capacity INTEGER,
    _city_id BIGINT NOT NULL,
    CONSTRAINT fk_venue_city FOREIGN KEY (_city_id) REFERENCES city (id)
);

-- team  (→ sport, city)
CREATE TABLE team (
    id        BIGSERIAL    PRIMARY KEY,
    name      VARCHAR(150) NOT NULL,
    _sport_id BIGINT       NOT NULL,
    _city_id  BIGINT       NOT NULL,
    CONSTRAINT fk_team_sport FOREIGN KEY (_sport_id) REFERENCES sport   (id),
    CONSTRAINT fk_team_city  FOREIGN KEY (_city_id)  REFERENCES city    (id)
);

-- event  (→ sport, venue)
CREATE TABLE event (
    id         BIGSERIAL    PRIMARY KEY,
    title      VARCHAR(255) NOT NULL,
    event_date DATE         NOT NULL,
    event_time TIME         NOT NULL,
    status     VARCHAR(20)  NOT NULL DEFAULT 'SCHEDULED',
    _sport_id  BIGINT       NOT NULL,
    _venue_id  BIGINT       NOT NULL,
    CONSTRAINT fk_event_sport  FOREIGN KEY (_sport_id) REFERENCES sport  (id),
    CONSTRAINT fk_event_venue  FOREIGN KEY (_venue_id) REFERENCES venue  (id),
    CONSTRAINT chk_event_status CHECK (status IN ('SCHEDULED', 'LIVE', 'FINISHED'))
);

-- event_team  junction table: event ↔ team  (→ event, team)
CREATE TABLE event_team (
    id        BIGSERIAL PRIMARY KEY,
    _event_id BIGINT    NOT NULL,
    _team_id  BIGINT    NOT NULL,
    is_home   BOOLEAN   NOT NULL DEFAULT FALSE,
    score     INTEGER,
    CONSTRAINT fk_event_team_event FOREIGN KEY (_event_id) REFERENCES event (id) ON DELETE CASCADE,
    CONSTRAINT fk_event_team_team  FOREIGN KEY (_team_id)  REFERENCES team  (id),
    CONSTRAINT uq_event_team       UNIQUE (_event_id, _team_id)
);
