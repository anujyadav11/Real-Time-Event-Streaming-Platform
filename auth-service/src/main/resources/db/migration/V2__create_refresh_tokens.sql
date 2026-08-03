CREATE TABLE refresh_tokens
(
    id UUID PRIMARY KEY,
    token UUID NOT NULL UNIQUE,
    user_id UUID NOT NULL,
    expiry TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_refresh_user
        FOREIGN KEY(user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);