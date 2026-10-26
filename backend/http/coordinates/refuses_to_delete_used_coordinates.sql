INSERT INTO users (username, password_hash) VALUES ('fixture_owner', 'unused');

INSERT INTO coordinates (x, y) VALUES (10, 20);

INSERT INTO workers (user_id, name, coordinates_id, salary, rating, start_date)
VALUES (1, 'Fixture Worker', 1, 1000, 1, now());
