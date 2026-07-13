package com.example.eventstream.common.command;

import java.util.UUID;

public record SendNotificationCommand(
        UUID commandId,
        UUID orderId,
        String correlationId

) {}