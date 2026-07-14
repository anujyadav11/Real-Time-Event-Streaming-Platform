package com.example.eventstream.common.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationSentEvent(
        UUID eventId,
        UUID orderId,
        String message,
        LocalDateTime sentAt,
        String correlationId
) {
}
