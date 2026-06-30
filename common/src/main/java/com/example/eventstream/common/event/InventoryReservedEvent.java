package com.example.eventstream.common.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record InventoryReservedEvent(
        UUID orderId,
        BigDecimal amount,
        boolean reserved,
        LocalDateTime reservedAt
) {
}
