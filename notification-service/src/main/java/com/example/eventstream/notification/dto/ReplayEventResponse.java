package com.example.eventstream.notification.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReplayEventResponse(
        UUID eventId,
        String eventType,
        String topic,
        String status,
        int replayCount,
        LocalDateTime receivedAt
) {
}