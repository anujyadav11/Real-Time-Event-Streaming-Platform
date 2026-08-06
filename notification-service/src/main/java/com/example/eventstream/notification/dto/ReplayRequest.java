package com.example.eventstream.notification.dto;

import java.util.UUID;

public record ReplayRequest(
        UUID eventId,
        String topic
) {
}