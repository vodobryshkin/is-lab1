INSERT INTO addresses (street, town_id) VALUES ('Official', NULL), ('Postal', NULL);

INSERT INTO organizations (official_address_id, annual_turnover, employees_count, full_name, rating,
                           postal_address_id)
VALUES (1, 1000, 3, 'Employer', 5, 2);

INSERT INTO users (username, password_hash) VALUES ('fixture_owner', 'unused');

INSERT INTO coordinates (x, y) VALUES (1, 1);

INSERT INTO workers (user_id, name, coordinates_id, salary, rating, start_date, organization_id)
VALUES (1, 'Fixture Worker', 1, 1000, 1, now(), 1);
