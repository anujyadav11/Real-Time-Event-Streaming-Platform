CREATE TABLE deliveries
(
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL UNIQUE,
    delivery_partner VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    estimated_delivery_time TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);