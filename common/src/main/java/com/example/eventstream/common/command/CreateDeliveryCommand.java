package com.example.eventstream.common.command;

import java.util.UUID;

public record CreateDeliveryCommand(
        UUID commandId,
        UUID orderId,
        String correlationId
) {}