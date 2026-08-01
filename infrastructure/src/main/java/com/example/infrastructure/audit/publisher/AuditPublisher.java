package com.example.infrastructure.audit.publisher;

import com.example.infrastructure.audit.model.AuditEvent;
import java.util.concurrent.CompletableFuture;

public interface AuditPublisher {
    CompletableFuture<Void> publish(AuditEvent event);
}