CREATE TABLE IF NOT EXISTS users (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(64) NOT NULL,
    creation_date TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT users_username_key UNIQUE (username),
    CONSTRAINT users_username_check CHECK (username ~ '^[a-zA-Z0-9_]{3,64}$')
);

CREATE TABLE IF NOT EXISTS coordinates (
    coordinates_id BIGSERIAL PRIMARY KEY,
    x INTEGER NOT NULL,
    y BIGINT NOT NULL,
    CONSTRAINT coordinates_x_check CHECK (x > -954),
    CONSTRAINT coordinates_y_check CHECK (y > -392)
);

CREATE TABLE IF NOT EXISTS locations (
    location_id BIGSERIAL PRIMARY KEY,
    x INTEGER NOT NULL,
    y INTEGER NOT NULL,
    z BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS addresses (
    address_id BIGSERIAL PRIMARY KEY,
    street TEXT NOT NULL,
    town_id BIGINT,
    CONSTRAINT addresses_street_check CHECK (street ~ '\S'),
    CONSTRAINT addresses_town_id_fkey FOREIGN KEY (town_id)
        REFERENCES locations (location_id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS organizations (
    organization_id BIGSERIAL PRIMARY KEY,
    official_address_id BIGINT NOT NULL,
    annual_turnover REAL NOT NULL,
    employees_count BIGINT NOT NULL DEFAULT 1,
    full_name TEXT NOT NULL,
    rating INTEGER NOT NULL,
    type VARCHAR(255),
    postal_address_id BIGINT NOT NULL,
    CONSTRAINT organizations_full_name_key UNIQUE (full_name),
    CONSTRAINT organizations_annual_turnover_check
        CHECK (annual_turnover > 0 AND annual_turnover < 'Infinity'::REAL),
    CONSTRAINT organizations_employees_count_check CHECK (employees_count > 0),
    CONSTRAINT organizations_full_name_check CHECK (full_name ~ '\S'),
    CONSTRAINT organizations_rating_check CHECK (rating > 0),
    CONSTRAINT organizations_type_check CHECK (type IN (
        'PUBLIC',
        'GOVERNMENT',
        'TRUST',
        'PRIVATE_LIMITED_COMPANY',
        'OPEN_JOINT_STOCK_COMPANY'
    )),
    CONSTRAINT organizations_official_address_id_fkey FOREIGN KEY (official_address_id)
        REFERENCES addresses (address_id) ON DELETE RESTRICT,
    CONSTRAINT organizations_postal_address_id_fkey FOREIGN KEY (postal_address_id)
        REFERENCES addresses (address_id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS workers (
    worker_id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    coordinates_id BIGINT NOT NULL,
    creation_date TIMESTAMPTZ NOT NULL DEFAULT now(),
    organization_id BIGINT,
    salary DOUBLE PRECISION NOT NULL,
    rating INTEGER NOT NULL,
    start_date TIMESTAMPTZ NOT NULL,
    position VARCHAR(255),
    status VARCHAR(255),
    CONSTRAINT workers_name_check CHECK (name ~ '\S'),
    CONSTRAINT workers_salary_check
        CHECK (salary > 0 AND salary < 'Infinity'::DOUBLE PRECISION),
    CONSTRAINT workers_rating_check CHECK (rating > 0),
    CONSTRAINT workers_position_check CHECK (position IN (
        'DIRECTOR',
        'HEAD_OF_DIVISION',
        'LEAD_DEVELOPER'
    )),
    CONSTRAINT workers_status_check CHECK (status IN (
        'HIRED',
        'RECOMMENDED_FOR_PROMOTION',
        'REGULAR'
    )),
    CONSTRAINT workers_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES users (user_id) ON DELETE RESTRICT,
    CONSTRAINT workers_coordinates_id_fkey FOREIGN KEY (coordinates_id)
        REFERENCES coordinates (coordinates_id) ON DELETE RESTRICT,
    CONSTRAINT workers_organization_id_fkey FOREIGN KEY (organization_id)
        REFERENCES organizations (organization_id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS persons (
    person_id BIGSERIAL PRIMARY KEY,
    worker_id INTEGER,
    eye_color VARCHAR(255),
    hair_color VARCHAR(255) NOT NULL,
    location_id BIGINT NOT NULL,
    birthday TIMESTAMPTZ NOT NULL,
    weight REAL NOT NULL,
    nationality VARCHAR(255) NOT NULL,
    CONSTRAINT persons_worker_id_key UNIQUE (worker_id),
    CONSTRAINT persons_eye_color_check CHECK (eye_color IN (
        'BLACK',
        'BLUE',
        'ORANGE',
        'WHITE',
        'BROWN'
    )),
    CONSTRAINT persons_hair_color_check CHECK (hair_color IN (
        'BLACK',
        'BLUE',
        'ORANGE',
        'WHITE',
        'BROWN'
    )),
    CONSTRAINT persons_weight_check CHECK (weight > 0 AND weight < 'Infinity'::REAL),
    CONSTRAINT persons_nationality_check CHECK (nationality IN (
        'RUSSIA',
        'UNITED_KINGDOM',
        'SPAIN',
        'ITALY'
    )),
    CONSTRAINT persons_worker_id_fkey FOREIGN KEY (worker_id)
        REFERENCES workers (worker_id) ON DELETE RESTRICT,
    CONSTRAINT persons_location_id_fkey FOREIGN KEY (location_id)
        REFERENCES locations (location_id) ON DELETE RESTRICT
);
