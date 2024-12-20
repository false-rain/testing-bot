CREATE SCHEMA IF NOT EXISTS subscribers;

DROP TABLE IF EXISTS subscribers.subscribers;

CREATE TABLE IF NOT EXISTS subscribers.subscribers
(
  id UUID PRIMARY KEY,
  user_id VARCHAR(255) NOT NULL,
  current_price DOUBLE PRECISION NULL
);

