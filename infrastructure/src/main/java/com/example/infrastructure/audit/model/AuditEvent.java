package com.example.infrastructure.audit.model;

import java.time.Instant;

public record AuditEvent(
        Instant timestamp,
        String service,
        String userId,
        String username,
        String action,
        String resource,
        String resourceId,
        String status,
        String correlationId,
        String traceId,
        String ipAddress
) {
}