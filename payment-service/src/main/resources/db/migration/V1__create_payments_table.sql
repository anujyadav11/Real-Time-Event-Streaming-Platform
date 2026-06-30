CREATE TABLE payments
{
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(30),
    paid_at TIMESTAMP
};