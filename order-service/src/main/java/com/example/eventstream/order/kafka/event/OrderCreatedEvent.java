package com.example.eventstream.order.kafka.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderCreatedEvent (
        UUID orderId,
        String customerName,
        String restaurantName,
        BigDecimal totalAmount,
        String status,
        LocalDateTime createdAt
){
}
