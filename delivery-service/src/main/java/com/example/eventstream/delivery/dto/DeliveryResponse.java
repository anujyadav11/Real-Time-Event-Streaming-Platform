package com.example.eventstream.delivery.dto;

import com.example.eventstream.common.enums.DeliveryStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryResponse(
        UUID orderId,
        String deliveryPartner,
        DeliveryStatus status,
        LocalDateTime estimatedDeliveryTime
) {
}
