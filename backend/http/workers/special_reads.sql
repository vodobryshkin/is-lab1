INSERT INTO users (username, password_hash) VALUES ('fixture_owner', 'unused');

INSERT INTO coordinates (x, y) VALUES (1, 1);

INSERT INTO locations (x, y, z) VALUES (1, 1, 1);

INSERT INTO addresses (street, town_id) VALUES ('Official', NULL);

INSERT INTO organizations (official_address_id, annual_turnover, employees_count, full_name, rating,
                           postal_address_id)
VALUES (1, 1000, 3, 'Employer', 5, 1);

INSERT INTO workers (user_id, name, coordinates_id, salary, rating, start_date, organization_id, status)
VALUES (1, 'First', 1, 100, 5, now(), 1, 'HIRED'),
       (1, 'Second', 1, 100, 1, now(), NULL, 'HIRED'),
       (1, 'Third', 1, 100, 10, now(), NULL, 'RECOMMENDED_FOR_PROMOTION'),
       (1, 'Fourth', 1, 100, 100, now(), NULL, 'REGULAR'),
       (1, 'Fifth', 1, 100, 2147483647, now(), NULL, NULL);

INSERT INTO persons (worker_id, hair_color, location_id, birthday, weight, nationality)
VALUES (1, 'BROWN', 1, now(), 60, 'RUSSIA');
