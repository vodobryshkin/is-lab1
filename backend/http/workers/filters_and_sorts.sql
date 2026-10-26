INSERT INTO users (username, password_hash) VALUES ('fixture_owner', 'unused');

INSERT INTO coordinates (x, y) VALUES (1, 1);

INSERT INTO locations (x, y, z) VALUES (1, 1, 1);

INSERT INTO addresses (street, town_id) VALUES ('Невский проспект', NULL), ('Kronverksky', NULL);

INSERT INTO organizations (official_address_id, annual_turnover, employees_count, full_name, rating, type,
                           postal_address_id)
VALUES (1, 1000, 3, 'ООО Магистраль', 5, 'PUBLIC', 2),
       (2, 1000, 3, 'Trust 100%', 5, 'TRUST', 2);

INSERT INTO workers (user_id, name, coordinates_id, salary, rating, start_date, organization_id, position, status)
VALUES (1, 'Anna Smith', 1, 100, 1, now(), 1, 'DIRECTOR', 'HIRED'),
       (1, 'Anna_Bel', 1, 300, 1, now(), 2, NULL, 'REGULAR'),
       (1, 'Boris', 1, 200, 1, now(), NULL, 'LEAD_DEVELOPER', NULL),
       (1, 'Zed', 1, 300, 1, now(), 1, NULL, 'RECOMMENDED_FOR_PROMOTION');

INSERT INTO persons (worker_id, hair_color, location_id, birthday, weight, nationality)
VALUES (1, 'BROWN', 1, now(), 60, 'RUSSIA'),
       (3, 'BLACK', 1, now(), 70, 'ITALY');
