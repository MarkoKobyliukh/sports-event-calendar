# Sports Event Calendar

A full-stack sports event calendar application built for the **Sportradar Coding Academy Back-End Exercise**.
It allows events to be created, displayed, filtered, and managed — categorized by sport, venue, teams, and status.

---

## Tech Stack

| Layer | Technology | Why |
|---|---|---|
| Language | Java 21 | LTS release, modern records and sealed classes |
| Framework | Spring Boot 3.2 | Industry standard, rapid setup, auto-configuration |
| ORM | Spring Data JPA + Hibernate | Clean entity mapping, no raw SQL boilerplate |
| Database | PostgreSQL 17 | Robust relational DB, full FK and constraint support |
| Migrations | Flyway | Versioned, reproducible schema management |
| Frontend | Thymeleaf + Tailwind CSS (CDN) | Native Spring integration, no Node.js build tooling required |
| Utilities | Lombok | Eliminates boilerplate (getters, builders, constructors) |
| Testing | JUnit 5 + Mockito + Spring Boot Test | Standard Java test stack |
| Build | Maven | Spring Boot default, declarative dependency management |

### Why Thymeleaf over React / Vue?
Server-side rendering is the right choice at this scale. Thymeleaf renders HTML directly from Spring MVC — no separate frontend build, no API versioning concerns, no CORS setup. Tailwind CSS via CDN delivers a modern, responsive UI with zero build tooling. The result: one `mvn spring-boot:run` command and the entire application is live.

---

## Database Modeling

The schema is designed in **Third Normal Form (3NF)**. The full ERD with all tables, fields, data types, primary keys, and foreign keys is available in `docs/SportEvents_ERD_Diagram.pdf`.

### Schema Overview

```
sport       — id (PK), name
country     — id (PK), name, code
city        — id (PK), name, _country_id (FK → country)
venue       — id (PK), name, address, capacity, _city_id (FK → city)
team        — id (PK), name, _sport_id (FK → sport), _city_id (FK → city)
event       — id (PK), title, event_date, event_time, status, _sport_id (FK → sport), _venue_id (FK → venue)
event_team  — id (PK), _event_id (FK → event), _team_id (FK → team), is_home, score
```

### Why 3NF is satisfied

- **1NF:** All columns are atomic — no arrays, no nested values.
- **2NF:** Every table uses a single-column surrogate PK — no partial dependencies possible.
- **3NF:** No transitive dependencies:
  - Country data lives in `country`, not repeated in `city` or `venue`.
  - Sport name is normalized into `sport`, not stored in `team` or `event`.
  - Address location hierarchy: `city → country` — no city data in `venue`.

### FK Naming Convention
All foreign key columns are prefixed with underscore as required by the exercise specification:
`_sport_id`, `_venue_id`, `_city_id`, `_country_id`, `_event_id`, `_team_id`.

---

## Project Structure

```
sports-event-calendar/
├── src/
│   ├── main/
│   │   ├── java/com/sportradar/sportevents/
│   │   │   ├── SportEventsApplication.java              # Entry point (@SpringBootApplication)
│   │   │   │
│   │   │   ├── controller/                              # REST API controllers (@RestController)
│   │   │   │   ├── SportController.java                 # GET/POST/PUT/DELETE /api/sports
│   │   │   │   ├── CountryController.java               # GET/POST/PUT/DELETE /api/countries
│   │   │   │   ├── CityController.java                  # GET/POST/PUT/DELETE /api/cities
│   │   │   │   ├── VenueController.java                 # GET/POST/PUT/DELETE /api/venues
│   │   │   │   ├── TeamController.java                  # GET/POST/PUT/DELETE /api/teams
│   │   │   │   ├── EventController.java                 # Full CRUD + status/score/team endpoints
│   │   │   │   └── view/                                # Thymeleaf MVC controllers (@Controller)
│   │   │   │       ├── DashboardController.java         # GET / → live scoreboard
│   │   │   │       ├── EventViewController.java         # /events CRUD + form handlers
│   │   │   │       ├── SportViewController.java         # GET /sports
│   │   │   │       └── TeamViewController.java          # GET /teams
│   │   │   │
│   │   │   ├── domain/                                  # JPA entities
│   │   │   │   ├── Sport.java
│   │   │   │   ├── Country.java
│   │   │   │   ├── City.java
│   │   │   │   ├── Venue.java
│   │   │   │   ├── Team.java
│   │   │   │   ├── Event.java
│   │   │   │   ├── EventTeam.java                       # Join entity: event ↔ team with score
│   │   │   │   └── EventStatus.java                     # Enum: SCHEDULED, LIVE, FINISHED
│   │   │   │
│   │   │   ├── dto/
│   │   │   │   ├── request/                             # Incoming payloads with bean validation
│   │   │   │   │   ├── SportRequest.java
│   │   │   │   │   ├── CountryRequest.java
│   │   │   │   │   ├── CityRequest.java
│   │   │   │   │   ├── VenueRequest.java
│   │   │   │   │   ├── TeamRequest.java
│   │   │   │   │   ├── EventRequest.java
│   │   │   │   │   ├── EventTeamRequest.java
│   │   │   │   │   ├── UpdateScoreRequest.java
│   │   │   │   │   └── UpdateStatusRequest.java
│   │   │   │   └── response/                            # Outgoing JSON/view data (Java records)
│   │   │   │       ├── SportResponse.java
│   │   │   │       ├── CountryResponse.java
│   │   │   │       ├── CityResponse.java
│   │   │   │       ├── VenueResponse.java
│   │   │   │       ├── TeamResponse.java
│   │   │   │       ├── EventResponse.java
│   │   │   │       └── EventTeamResponse.java
│   │   │   │
│   │   │   ├── exception/
│   │   │   │   ├── ResourceNotFoundException.java       # 404
│   │   │   │   ├── DuplicateResourceException.java      # 409
│   │   │   │   ├── InvalidOperationException.java       # 400
│   │   │   │   ├── ErrorResponse.java                   # Consistent error JSON shape
│   │   │   │   └── GlobalExceptionHandler.java          # @RestControllerAdvice
│   │   │   │
│   │   │   ├── mapper/                                  # Manual entity ↔ DTO converters
│   │   │   │   ├── SportMapper.java
│   │   │   │   ├── CountryMapper.java
│   │   │   │   ├── CityMapper.java
│   │   │   │   ├── VenueMapper.java
│   │   │   │   ├── TeamMapper.java
│   │   │   │   ├── EventMapper.java
│   │   │   │   └── EventTeamMapper.java
│   │   │   │
│   │   │   ├── repository/                              # Spring Data JPA repositories
│   │   │   │   ├── SportRepository.java
│   │   │   │   ├── CountryRepository.java
│   │   │   │   ├── CityRepository.java
│   │   │   │   ├── VenueRepository.java
│   │   │   │   ├── TeamRepository.java
│   │   │   │   ├── EventRepository.java                 # Custom JPQL: by status, sport, date, team
│   │   │   │   └── EventTeamRepository.java
│   │   │   │
│   │   │   └── service/                                 # Business logic layer (@Service)
│   │   │       ├── SportService.java
│   │   │       ├── CountryService.java
│   │   │       ├── CityService.java
│   │   │       ├── VenueService.java
│   │   │       ├── TeamService.java
│   │   │       └── EventService.java                    # Full CRUD + team mgmt + score/status
│   │   │
│   │   └── resources/
│   │       ├── application.properties                   # DB, JPA, Flyway, Thymeleaf config
│   │       ├── db/migration/
│   │       │   ├── V1__create_schema.sql                # All 7 tables with PKs, FKs, constraints
│   │       │   └── V2__seed_data.sql                    # Sports, countries, cities, venues, teams, events
│   │       └── templates/
│   │           ├── fragments/
│   │           │   ├── head.html                        # Tailwind CDN, Inter font, meta
│   │           │   └── nav.html                         # Sticky navbar with active links
│   │           ├── events/
│   │           │   ├── list.html                        # Events table with filters
│   │           │   ├── detail.html                      # Event detail + manage panel
│   │           │   └── form.html                        # Create event form
│   │           ├── sports/
│   │           │   └── list.html                        # Sports card grid
│   │           ├── teams/
│   │           │   └── list.html                        # Teams card grid with sport filter
│   │           └── index.html                           # Dashboard — live scoreboard
│   │
│   └── test/
│       └── java/com/sportradar/sportevents/
│           ├── controller/
│           │   ├── EventControllerTest.java             # @WebMvcTest — HTTP codes, JSON, validation
│           │   └── SportControllerTest.java
│           ├── repository/
│           │   └── EventRepositoryTest.java             # @DataJpaTest + H2 — custom JPQL queries
│           └── service/
│               ├── SportServiceTest.java                # Mockito — CRUD, duplicate, not found
│               ├── TeamServiceTest.java
│               └── EventServiceTest.java                # Status, score, addTeam, removeTeam
│
├── docs/
│   ├── SportEvents_Database_Design.pdf                  # Task 1 deliverable: ERD + 3NF analysis
│   └── SportEvents_ERD_Diagram.pdf                      # ERD with data types
├── pom.xml
└── README.md
```

---

## API Endpoints

### Events — `/api/events`

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/events` | Get all events |
| `GET` | `/api/events?status=LIVE` | Filter by status (`SCHEDULED`, `LIVE`, `FINISHED`) |
| `GET` | `/api/events?sportId=1` | Filter by sport |
| `GET` | `/api/events?teamId=1` | Filter by team |
| `GET` | `/api/events?date=2026-03-26` | Filter by exact date |
| `GET` | `/api/events?from=2026-03-01&to=2026-03-31` | Filter by date range |
| `GET` | `/api/events/{id}` | Get single event by ID |
| `POST` | `/api/events` | Create new event |
| `PUT` | `/api/events/{id}` | Update event |
| `PATCH` | `/api/events/{id}/status` | Update event status only |
| `POST` | `/api/events/{id}/teams` | Add team to event |
| `PATCH` | `/api/events/{id}/teams/{teamId}/score` | Update team score |
| `DELETE` | `/api/events/{id}/teams/{teamId}` | Remove team from event |
| `DELETE` | `/api/events/{id}` | Delete event |

### Other REST Resources

| Method | Path | Description |
|---|---|---|
| `GET/POST/PUT/DELETE` | `/api/sports/{id?}` | Full CRUD for sports |
| `GET/POST/PUT/DELETE` | `/api/countries/{id?}` | Full CRUD for countries |
| `GET/POST/PUT/DELETE` | `/api/cities/{id?}` | Full CRUD for cities (`?countryId=` filter) |
| `GET/POST/PUT/DELETE` | `/api/venues/{id?}` | Full CRUD for venues (`?cityId=` filter) |
| `GET/POST/PUT/DELETE` | `/api/teams/{id?}` | Full CRUD for teams (`?sportId=` filter) |

### Web UI Routes

| Method | Path | Description |
|---|---|---|
| `GET` | `/` | Dashboard — live scoreboard sorted by status |
| `GET` | `/events` | Events list with sport, status, and date range filters |
| `GET` | `/events/new` | Create event form |
| `POST` | `/events/new` | Submit new event (with home/away teams) |
| `GET` | `/events/{id}` | Event detail — scores, teams, manage panel |
| `POST` | `/events/{id}/status` | Update event status (form submit) |
| `POST` | `/events/{id}/teams/{teamId}/score` | Update team score (form submit) |
| `POST` | `/events/{id}/delete` | Delete event (form submit) |
| `GET` | `/sports` | Sports list |
| `GET` | `/teams` | Teams list with sport filter |

### Error Response Shape

All REST errors return a consistent JSON body:

```json
{
  "timestamp": "2026-03-26T20:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Event not found with id: 99",
  "fieldErrors": null
}
```

---

## Setup Instructions

### Prerequisites

- Java 21+
- Maven 3.9+
- PostgreSQL (tested on 17)

### 1. Clone the repository

```bash
git clone https://github.com/MarkoKobyliukh/sports-event-calendar.git
cd sports-event-calendar
```

### 2. Create the database

```sql
CREATE DATABASE sport_events;
```

Or via CLI:

```bash
createdb sport_events
```

### 3. Configure credentials

Open `src/main/resources/application.properties` and update:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/sport_events
spring.datasource.username=YOUR_POSTGRES_USERNAME
spring.datasource.password=YOUR_POSTGRES_PASSWORD
```

### 4. Run the application

```bash
mvn spring-boot:run
```

On first startup, **Flyway automatically runs both migrations**:
- `V1__create_schema.sql` — creates all 7 tables with constraints
- `V2__seed_data.sql` — seeds sports, countries, cities, venues, teams, and events

### 5. Open in browser

```
http://localhost:8080
```

The dashboard shows the live scoreboard. Navigate to `/events` to browse, filter, create, and manage events.

### 6. Run tests

```bash
mvn test
```

**54 tests** across 6 test classes — all pass with an in-memory H2 database (no PostgreSQL required for tests).

---

## Features

| Feature | Details |
|---|---|
| **Event CRUD** | Create, read, update, delete events via both REST API and web UI |
| **Team assignment** | Home and away teams selected on event creation, linked via `event_team` |
| **Score management** | Update scores per team from the event detail page |
| **Status lifecycle** | `SCHEDULED → LIVE → FINISHED` with color-coded badges |
| **Filters** | By sport, status, exact date, or date range (from/to) |
| **Dashboard** | Live scoreboard — events sorted by status (LIVE first) |
| **REST API** | Full CRUD for all 6 entities with consistent JSON responses |
| **Validation** | Bean validation on all request DTOs (`@NotBlank`, `@NotNull`, `@Min`) |
| **Error handling** | `GlobalExceptionHandler` returns structured error JSON for all error types |
| **Tests** | Unit (Mockito), controller (@WebMvcTest), repository (@DataJpaTest + H2) |
| **3NF schema** | 7 tables, all in Third Normal Form, FK prefix convention followed |
| **Seed data** | 3 sports, 5 countries, 8 cities, 6 venues, 10 teams, 6 events pre-loaded |

---

## Design Decisions

- **Manual mappers over MapStruct** — Explicit `entity → DTO` conversion is easier to read and debug at this scale. No annotation processing overhead.
- **Java records for DTOs** — Immutable, concise, no Lombok needed for data carriers.
- **`PATCH` for partial updates** — `PATCH /api/events/{id}/status` and `PATCH /api/events/{id}/teams/{teamId}/score` follow REST semantics correctly. `PUT` is reserved for full resource replacement.
- **Flyway over `ddl-auto=create`** — Schema changes are versioned, auditable, and reproducible. The database state is always deterministic.
- **No N+1 queries** — `EventRepository` uses JPQL with `JOIN` for team-based queries. Services do not call repositories inside loops.
- **`@Transactional(readOnly = true)`** at class level — All service classes default to read-only transactions. Write methods override with `@Transactional`.
- **H2 for tests** — Repository and controller tests use an in-memory H2 database. No external PostgreSQL instance required to run the test suite.
