ALTER TABLE deliveries
    ADD COLUMN IF NOT EXISTS delivered_at TIMESTAMP;

ALTER TABLE deliveries
    ALTER COLUMN delivered_at DROP NOT NULL;
