ALTER TABLE inventory
    ADD CONSTRAINT inventory_available_quantity_nonnegative
        CHECK (available_quantity >= 0),
    ADD CONSTRAINT inventory_reserved_quantity_nonnegative
        CHECK (reserved_quantity >= 0);
