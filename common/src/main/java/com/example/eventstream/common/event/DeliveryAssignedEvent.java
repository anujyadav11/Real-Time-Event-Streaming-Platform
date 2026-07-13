package com.example.eventstream.common.event;

import java.util.UUID;

public record DeliveryAssignedEvent(
        UUID eventId,
        UUID orderId,
        UUID deliveryId,
        String riderName,
        String correlationId
) {}
