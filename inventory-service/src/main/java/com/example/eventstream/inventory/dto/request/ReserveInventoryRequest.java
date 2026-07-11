package com.example.eventstream.inventory.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReserveInventoryRequest(
        @Positive
        Long productId,

        @NotNull
        @Min(1)
        Integer quantity
) {
}
