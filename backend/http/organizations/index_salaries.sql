INSERT INTO users (username, password_hash) VALUES ('fixture_owner', 'unused');

INSERT INTO coordinates (x, y) VALUES (1, 1);

INSERT INTO addresses (street, town_id) VALUES ('Official', NULL);

INSERT INTO organizations (official_address_id, annual_turnover, employees_count, full_name, rating,
                           postal_address_id)
VALUES (1, 1000, 3, 'Indexed', 5, 1),
       (1, 1000, 3, 'Other', 5, 1),
       (1, 1000, 3, 'Empty', 5, 1),
       (1, 1000, 3, 'Extreme', 5, 1);

INSERT INTO workers (user_id, name, coordinates_id, salary, rating, start_date, organization_id)
VALUES (1, 'First', 1, 100, 1, now(), 1),
       (1, 'Second', 1, 250.5, 1, now(), 1),
       (1, 'Other', 1, 1000, 1, now(), 2),
       (1, 'Ordinary', 1, 100, 1, now(), 4),
       (1, 'Huge', 1, 1e308, 1, now(), 4),
       (1, 'Tiny', 1, 1e-300, 1, now(), 4);
