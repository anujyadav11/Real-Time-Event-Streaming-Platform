package com.example.eventstream.common.event;

import java.util.UUID;

public record InventoryReservationFailedEvent(
        UUID eventId,
        UUID orderId,
        Long productId,
        Integer quantity,
        String reason,
        String correlationId

) {

}