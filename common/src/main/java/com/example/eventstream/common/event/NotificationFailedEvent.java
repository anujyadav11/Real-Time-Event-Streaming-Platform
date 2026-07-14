package com.example.eventstream.common.event;

import java.util.UUID;

public record NotificationFailedEvent(
        UUID eventId,
        UUID orderId,
        String reason,
        String correlationId
) {
}
