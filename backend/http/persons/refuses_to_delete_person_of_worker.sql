INSERT INTO users (username, password_hash) VALUES ('fixture_owner', 'unused');

INSERT INTO coordinates (x, y) VALUES (1, 1);

INSERT INTO workers (user_id, name, coordinates_id, salary, rating, start_date)
VALUES (1, 'Fixture Worker', 1, 1000, 1, now());

INSERT INTO locations (x, y, z) VALUES (1, 1, 1);

INSERT INTO persons (worker_id, hair_color, location_id, birthday, weight, nationality)
VALUES (1, 'BLACK', 1, '2000-05-06T07:08:09Z', 70.5, 'RUSSIA'),
       (NULL, 'WHITE', 1, '2001-05-06T07:08:09Z', 60, 'SPAIN');
