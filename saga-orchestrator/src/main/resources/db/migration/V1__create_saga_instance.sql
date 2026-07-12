CREATE TABLE saga_instance (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL UNIQUE,
    correlation_id VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);