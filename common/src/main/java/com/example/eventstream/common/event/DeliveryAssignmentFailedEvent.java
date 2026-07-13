package com.example.eventstream.common.event;

import java.util.UUID;

public record DeliveryAssignmentFailedEvent(
        UUID eventId,
        UUID orderId,
        String reason,
        String correlationId
) {}