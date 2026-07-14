package com.example.eventstream.common.command;

import java.util.UUID;

public record CancelOrderCommand(
        UUID commandId,
        UUID orderId,
        String correlationId
) {}
