package com.example.eventstream.common.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record InventoryReservedEvent(
        UUID orderId,
        boolean reserved,
        LocalDateTime reservedAt
) {
}
