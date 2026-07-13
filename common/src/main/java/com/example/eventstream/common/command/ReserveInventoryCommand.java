package com.example.eventstream.common.command;

import java.math.BigDecimal;
import java.util.UUID;

public record ReserveInventoryCommand(
        UUID commandId,
        UUID orderId,
        Long productId,
        Integer quantity,
        BigDecimal amount,
        String correlationId
) {
}
