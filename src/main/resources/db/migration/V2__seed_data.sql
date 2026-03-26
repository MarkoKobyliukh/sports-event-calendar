-- ─────────────────────────────────────────────────────────────────────────────
-- V2 — Seed data: sports, countries, cities, venues, teams, events, event_teams
-- ─────────────────────────────────────────────────────────────────────────────

-- ── Sports ───────────────────────────────────────────────────────────────────
INSERT INTO sport (name) VALUES
    ('Football'),
    ('Basketball'),
    ('Tennis');

-- ── Countries ────────────────────────────────────────────────────────────────
INSERT INTO country (name, code) VALUES
    ('England', 'ENG'),
    ('Spain',   'ESP'),
    ('United States', 'USA'),
    ('Germany', 'GER'),
    ('France',  'FRA');

-- ── Cities ───────────────────────────────────────────────────────────────────
-- 1=London(ENG), 2=Madrid(ESP), 3=Barcelona(ESP), 4=New York(USA), 5=Munich(GER), 6=Paris(FRA)
INSERT INTO city (name, _country_id) VALUES
    ('London',    1),
    ('Madrid',    2),
    ('Barcelona', 2),
    ('New York',  3),
    ('Munich',    4),
    ('Paris',     5);

-- ── Venues ───────────────────────────────────────────────────────────────────
-- city FKs: 1=London, 2=Madrid, 3=Barcelona, 4=New York, 5=Munich, 6=Paris
INSERT INTO venue (name, address, capacity, _city_id) VALUES
    ('Wembley Stadium',      'Wembley Way, London',                          90000, 1),
    ('Santiago Bernabéu',    'Av. de Concha Espina 1, Madrid',               81044, 2),
    ('Camp Nou',             'C/ d''Aristides Maillol, Barcelona',           99354, 3),
    ('Madison Square Garden','4 Pennsylvania Plaza, New York',               20789, 4),
    ('Allianz Arena',        'Werner-Heissenberg-Allee 25, Munich',          75024, 5),
    ('Roland Garros',        '2 Av. Gordon Bennett, Paris',                  15059, 6),
    ('Parc des Princes',     '24 Rue du Commandant Guilbaud, Paris',         47929, 6);

-- ── Teams ────────────────────────────────────────────────────────────────────
-- sport FKs: 1=Football, 2=Basketball, 3=Tennis
-- city FKs:  1=London, 2=Madrid, 3=Barcelona, 4=New York, 5=Munich, 6=Paris
INSERT INTO team (name, _sport_id, _city_id) VALUES
    ('Chelsea FC',           1, 1),   -- 1
    ('Real Madrid CF',       1, 2),   -- 2
    ('FC Barcelona',         1, 3),   -- 3
    ('FC Bayern Munich',     1, 5),   -- 4
    ('Paris Saint-Germain',  1, 6),   -- 5
    ('New York Knicks',      2, 4),   -- 6
    ('Brooklyn Nets',        2, 4),   -- 7
    ('Carlos Alcaraz',       3, 2),   -- 8
    ('Jannik Sinner',        3, 5),   -- 9
    ('Novak Djokovic',       3, 1);   -- 10

-- ── Events ───────────────────────────────────────────────────────────────────
-- sport FKs: 1=Football, 2=Basketball, 3=Tennis
-- venue FKs: 1=Wembley, 2=Bernabéu, 3=Camp Nou, 4=MSG, 5=Allianz, 6=Roland Garros, 7=Parc des Princes
INSERT INTO event (title, event_date, event_time, status, _sport_id, _venue_id) VALUES
    ('UEFA Champions League Final: Chelsea vs Real Madrid',    '2024-05-01', '20:00', 'FINISHED',  1, 1),  -- 1
    ('El Clásico: Real Madrid vs FC Barcelona',                '2024-10-26', '21:00', 'FINISHED',  1, 2),  -- 2
    ('UCL Quarter-Final: FC Barcelona vs Bayern Munich',       '2025-04-08', '21:00', 'LIVE',      1, 3),  -- 3
    ('UCL Semi-Final: Bayern Munich vs Paris Saint-Germain',   '2025-04-29', '21:00', 'SCHEDULED', 1, 5),  -- 4
    ('Ligue 1: Paris Saint-Germain vs Chelsea FC',             '2025-05-10', '21:00', 'SCHEDULED', 1, 7),  -- 5
    ('NBA Regular Season: New York Knicks vs Brooklyn Nets',   '2025-01-15', '19:30', 'FINISHED',  2, 4),  -- 6
    ('NBA Playoffs: New York Knicks vs Brooklyn Nets',         '2025-05-01', '20:00', 'SCHEDULED', 2, 4),  -- 7
    ('Roland Garros Final: Alcaraz vs Sinner',                 '2024-06-09', '15:00', 'FINISHED',  3, 6),  -- 8
    ('Wimbledon Final: Djokovic vs Alcaraz',                   '2025-07-13', '14:00', 'SCHEDULED', 3, 1),  -- 9
    ('ATP Masters: Sinner vs Djokovic',                        '2025-03-30', '17:00', 'LIVE',      3, 6);  -- 10

-- ── Event Teams ──────────────────────────────────────────────────────────────
-- event 1: Chelsea (home) vs Real Madrid
INSERT INTO event_team (_event_id, _team_id, is_home, score) VALUES (1, 1, true,  1);
INSERT INTO event_team (_event_id, _team_id, is_home, score) VALUES (1, 2, false, 2);

-- event 2: Real Madrid (home) vs FC Barcelona
INSERT INTO event_team (_event_id, _team_id, is_home, score) VALUES (2, 2, true,  3);
INSERT INTO event_team (_event_id, _team_id, is_home, score) VALUES (2, 3, false, 1);

-- event 3: FC Barcelona (home) vs Bayern Munich — LIVE, no final score yet
INSERT INTO event_team (_event_id, _team_id, is_home, score) VALUES (3, 3, true,  1);
INSERT INTO event_team (_event_id, _team_id, is_home, score) VALUES (3, 4, false, 1);

-- event 4: Bayern Munich (home) vs PSG — SCHEDULED
INSERT INTO event_team (_event_id, _team_id, is_home, score) VALUES (4, 4, true,  NULL);
INSERT INTO event_team (_event_id, _team_id, is_home, score) VALUES (4, 5, false, NULL);

-- event 5: PSG (home) vs Chelsea — SCHEDULED
INSERT INTO event_team (_event_id, _team_id, is_home, score) VALUES (5, 5, true,  NULL);
INSERT INTO event_team (_event_id, _team_id, is_home, score) VALUES (5, 1, false, NULL);

-- event 6: NY Knicks (home) vs Brooklyn Nets — FINISHED
INSERT INTO event_team (_event_id, _team_id, is_home, score) VALUES (6, 6, true,  112);
INSERT INTO event_team (_event_id, _team_id, is_home, score) VALUES (6, 7, false, 98);

-- event 7: NY Knicks (home) vs Brooklyn Nets — SCHEDULED
INSERT INTO event_team (_event_id, _team_id, is_home, score) VALUES (7, 6, true,  NULL);
INSERT INTO event_team (_event_id, _team_id, is_home, score) VALUES (7, 7, false, NULL);

-- event 8: Alcaraz vs Sinner — FINISHED (tennis: is_home = false for both)
INSERT INTO event_team (_event_id, _team_id, is_home, score) VALUES (8, 8, false, 3);
INSERT INTO event_team (_event_id, _team_id, is_home, score) VALUES (8, 9, false, 2);

-- event 9: Djokovic vs Alcaraz — SCHEDULED
INSERT INTO event_team (_event_id, _team_id, is_home, score) VALUES (9, 10, false, NULL);
INSERT INTO event_team (_event_id, _team_id, is_home, score) VALUES (9, 8,  false, NULL);

-- event 10: Sinner vs Djokovic — LIVE
INSERT INTO event_team (_event_id, _team_id, is_home, score) VALUES (10, 9,  false, 1);
INSERT INTO event_team (_event_id, _team_id, is_home, score) VALUES (10, 10, false, 0);
