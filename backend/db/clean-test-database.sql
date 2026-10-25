DO $$
BEGIN
    IF current_database() !~ '_test$' THEN
        RAISE EXCEPTION 'Refusing to clean database "%": the name must end with _test',
            current_database();
    END IF;
END
$$;

TRUNCATE TABLE persons, workers, organizations, addresses, locations, coordinates, users
    RESTART IDENTITY;
