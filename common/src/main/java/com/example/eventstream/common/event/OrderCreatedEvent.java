package com.example.eventstream.common.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderCreatedEvent (
        UUID eventId,
        UUID orderId,
        String customerName,
        String restaurantName,
        BigDecimal totalAmount,
        String status,
        LocalDateTime createdAt,
        String correlationId
){
}
