CREATE TABLE inventory (
    id UUID PRIMARY KEY,
    product_id BIGINT NOT NULL UNIQUE,
    available_quantity INTEGER NOT NULL,
    reserved_quantity INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
