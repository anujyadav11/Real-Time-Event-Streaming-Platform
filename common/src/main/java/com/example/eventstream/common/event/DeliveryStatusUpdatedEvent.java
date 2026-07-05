package com.example.eventstream.common.event;

import com.example.eventstream.common.enums.DeliveryStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryStatusUpdatedEvent(
        UUID eventId,
        UUID orderId,
        DeliveryStatus status,
        LocalDateTime updatedAt
) {
    public DeliveryStatusUpdatedEvent(
            UUID orderId,
            DeliveryStatus status,
            LocalDateTime updateAt
    ){
        this(UUID.randomUUID(),
                orderId,
                status,
                updateAt
        );
    }
}
