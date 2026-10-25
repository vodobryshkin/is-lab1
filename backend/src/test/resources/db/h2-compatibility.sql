CREATE DOMAIN IF NOT EXISTS timestamptz AS TIMESTAMP WITH TIME ZONE;

CREATE SEQUENCE IF NOT EXISTS users_user_id_seq;
CREATE SEQUENCE IF NOT EXISTS coordinates_coordinates_id_seq;
CREATE SEQUENCE IF NOT EXISTS locations_location_id_seq;
CREATE SEQUENCE IF NOT EXISTS addresses_address_id_seq;
CREATE SEQUENCE IF NOT EXISTS organizations_organization_id_seq;
CREATE SEQUENCE IF NOT EXISTS workers_worker_id_seq;
CREATE SEQUENCE IF NOT EXISTS persons_person_id_seq;
