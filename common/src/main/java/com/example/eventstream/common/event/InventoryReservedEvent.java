package com.example.eventstream.common.event;

import java.util.UUID;

public record InventoryReservedEvent(
        UUID eventId,
        UUID orderId,
        Long productId,
        Integer quantity,
        String correlationId

) {

}
