package com.example.eventstream.common.command;

import java.util.UUID;

public record ReleaseInventoryCommand(
        UUID commandId,
        UUID orderId,
        Long productId,
        Integer quantity,
        String correlationId

) {}