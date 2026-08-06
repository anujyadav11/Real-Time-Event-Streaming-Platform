package com.example.eventstream.notification.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReplayResponse(
        UUID eventId,
        String status,
        int replayCount,
        LocalDateTime replayedAt
) {
}