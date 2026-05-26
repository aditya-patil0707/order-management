CREATE TABLE IF NOT EXISTS users
(
    id BIGINT NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    mobile_no VARCHAR(20),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE SEQUENCE IF NOT EXISTS users_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE users
ALTER COLUMN id
SET DEFAULT nextval('users_seq');

ALTER SEQUENCE users_seq
OWNED BY users.id;

