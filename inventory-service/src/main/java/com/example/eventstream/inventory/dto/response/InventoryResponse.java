package com.example.eventstream.inventory.dto.response;

public record InventoryResponse(
        Long productId,
        Integer availableQuantity,
        Integer reservedQuantity

) {
}
