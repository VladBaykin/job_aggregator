CREATE TABLE IF NOT EXISTS post(
    id SERIAL PRIMARY KEY,
    title TEXT,
    link TEXT UNIQUE,
    description TEXT,
    created TIMESTAMP
);