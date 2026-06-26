package com.example.eventstream.common.dto;

import java.time.Instant;

public record OrderResponse(
        String orderId,
        String status,
        Instant createdAt
) {
}
