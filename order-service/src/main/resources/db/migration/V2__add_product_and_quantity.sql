ALTER TABLE orders
ADD COLUMN product_id BIGINT;

ALTER TABLE orders
ADD COLUMN quantity INTEGER;

UPDATE orders
SET product_id = 1,
    quantity = 1
WHERE product_id IS NULL;

ALTER TABLE orders
ALTER COLUMN product_id SET NOT NULL;

ALTER TABLE orders
ALTER COLUMN quantity SET NOT NULL;