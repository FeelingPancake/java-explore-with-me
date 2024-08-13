DROP TABLE IF EXISTS participation_requests;
DROP TABLE IF EXISTS locations;
DROP TABLE IF EXISTS compilation_events;
DROP TABLE IF EXISTS compilations;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;

-- Пользователи

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL
);

-- Категории
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- События
CREATE TABLE IF NOT EXISTS events (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    title VARCHAR(120) NOT NULL UNIQUE,
    annotation VARCHAR(2000),
    description TEXT,
    location_x DECIMAL(18, 15),
    location_y DECIMAL(18, 15),
    event_limit INTEGER NOT NULL DEFAULT 0,
    request_moderation BOOLEAN DEFAULT TRUE,
    paid BOOLEAN NOT NULL,
    state VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    confirmed_requests BIGINT,
    category_id BIGINT REFERENCES categories(id),
    user_id BIGINT REFERENCES users(id),
    event_date TIMESTAMP NOT NULL,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    published_on TIMESTAMP
);

-- Компиляции
CREATE TABLE IF NOT EXISTS compilations (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    title VARCHAR(255) NOT NULL UNIQUE,
    pinned BOOLEAN DEFAULT FALSE,
    event_id BIGINT REFERENCES events(id)
);

-- Запросы на участие
CREATE TABLE IF NOT EXISTS participation_requests (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT REFERENCES users(id),
    event_id BIGINT REFERENCES events(id),
    status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS compilation_events (
    compilation_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    PRIMARY KEY (compilation_id, event_id),
    FOREIGN KEY (compilation_id) REFERENCES compilations(id),
    FOREIGN KEY (event_id) REFERENCES events(id)
);

CREATE TABLE IF NOT EXISTS locations (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(100) NOT NULL UNIQUE,
    latitude DECIMAL(18, 15) NOT NULL,
    longitude DECIMAL(18, 15) NOT NULL,
    radius NUMERIC NOT NULL
);

CREATE INDEX idx_event_category ON events(category_id);

CREATE INDEX idx_event_user ON events(user_id);

CREATE INDEX idx_request_user ON participation_requests(user_id);

CREATE INDEX idx_request_event ON participation_requests(event_id);