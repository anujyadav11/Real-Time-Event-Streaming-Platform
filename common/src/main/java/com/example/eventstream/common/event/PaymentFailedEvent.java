package com.example.eventstream.common.event;

import java.util.UUID;

public record PaymentFailedEvent(
        UUID eventId,
        UUID orderId,
        Long productId,
        Integer quantity,
        String reason,
        String correlationId

) {}