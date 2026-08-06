CREATE TABLE inbox_events (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL UNIQUE,
    event_type VARCHAR(255) NOT NULL,
    topic VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    replay_count INT NOT NULL DEFAULT 0,
    received_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP NOT NULL
);