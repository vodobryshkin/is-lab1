CREATE TABLE IF NOT EXISTS users (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(64) UNIQUE NOT NULL CHECK (username ~ '^[a-zA-Z0-9_]{3,64}$'),
    password_hash VARCHAR(255) NOT NULL,
    creation_date TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS coordinates (
    coordinates_id BIGSERIAL PRIMARY KEY,
    x INTEGER NOT NULL CHECK (x > -954),
    y BIGINT NOT NULL CHECK (y > -392)
);

CREATE TABLE IF NOT EXISTS locations (
    location_id BIGSERIAL PRIMARY KEY,
    x INTEGER NOT NULL,
    y INTEGER NOT NULL,
    z BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS addresses (
    address_id BIGSERIAL PRIMARY KEY,
    street TEXT NOT NULL CHECK (street ~ '\S'),
    town_id BIGINT REFERENCES locations
);

CREATE TABLE IF NOT EXISTS organizations (
    organization_id BIGSERIAL PRIMARY KEY,
    official_address_id BIGINT REFERENCES addresses NOT NULL,
    annual_turnover REAL NOT NULL CHECK (annual_turnover > 0 AND annual_turnover < 'Infinity'::REAL),
    employees_count BIGINT NOT NULL DEFAULT 1 CHECK (employees_count > 0),
    full_name TEXT UNIQUE NOT NULL CHECK (full_name ~ '\S'),
    rating INTEGER NOT NULL CHECK (rating > 0),
    type VARCHAR(255) CHECK (type IN ('PUBLIC', 'GOVERNMENT', 'TRUST', 'PRIVATE_LIMITED_COMPANY', 'OPEN_JOINT_STOCK_COMPANY')),
    postal_address_id BIGINT REFERENCES addresses NOT NULL
);

CREATE TABLE IF NOT EXISTS workers (
    worker_id SERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users NOT NULL,
    name VARCHAR(255) NOT NULL CHECK (name ~ '\S'),
    coordinates_id BIGINT REFERENCES coordinates NOT NULL,
    creation_date TIMESTAMPTZ NOT NULL DEFAULT now(),
    organization_id BIGINT REFERENCES organizations,
    salary DOUBLE PRECISION NOT NULL CHECK (salary > 0 AND salary < 'Infinity'::DOUBLE PRECISION),
    rating INTEGER NOT NULL CHECK (rating > 0),
    start_date TIMESTAMPTZ NOT NULL,
    position VARCHAR(255) CHECK (position IN ('DIRECTOR', 'HEAD_OF_DIVISION', 'LEAD_DEVELOPER')),
    status VARCHAR(255) CHECK (status IN ('HIRED', 'RECOMMENDED_FOR_PROMOTION', 'REGULAR'))
);

CREATE TABLE IF NOT EXISTS persons (
    person_id BIGSERIAL PRIMARY KEY,
    worker_id INTEGER REFERENCES workers UNIQUE,
    eye_color VARCHAR(255) CHECK (eye_color IN ('BLACK', 'BLUE', 'ORANGE', 'WHITE', 'BROWN')),
    hair_color VARCHAR(255) NOT NULL CHECK (hair_color IN ('BLACK', 'BLUE', 'ORANGE', 'WHITE', 'BROWN')),
    birthday TIMESTAMPTZ NOT NULL,
    weight REAL NOT NULL CHECK (weight > 0 AND weight < 'Infinity'::REAL),
    location_id BIGINT REFERENCES locations NOT NULL,
    nationality VARCHAR(255) NOT NULL CHECK (nationality IN ('RUSSIA', 'UNITED_KINGDOM', 'SPAIN', 'ITALY'))
);
