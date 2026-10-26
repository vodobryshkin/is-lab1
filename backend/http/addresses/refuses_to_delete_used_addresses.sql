INSERT INTO addresses (street, town_id) VALUES ('Official', NULL), ('Postal', NULL), ('Free', NULL);

INSERT INTO organizations (official_address_id, annual_turnover, employees_count, full_name, rating,
                           postal_address_id)
VALUES (1, 1000, 3, 'Fixture Organization', 5, 2);
