INSERT INTO users (username, password_hash) VALUES ('fixture_owner', 'unused');

INSERT INTO coordinates (x, y) VALUES (1, 1);

INSERT INTO addresses (street, town_id) VALUES ('Official', NULL);

INSERT INTO organizations (official_address_id, annual_turnover, employees_count, full_name, rating,
                           postal_address_id)
VALUES (1, 1000, 3, 'Old Employer', 5, 1),
       (1, 2000, 3, 'New Employer', 5, 1);

INSERT INTO workers (user_id, name, coordinates_id, salary, rating, start_date, organization_id, status)
VALUES (1, 'Employed', 1, 100, 7, '2026-01-01T00:00:00Z', 1, 'REGULAR'),
       (1, 'Unemployed', 1, 100, 8, '2026-01-01T00:00:00Z', NULL, NULL),
       (1, 'Untouched', 1, 100, 9, '2026-01-01T00:00:00Z', 1, 'REGULAR');
