package com.example.infrastructure.audit.publisher;

import com.example.infrastructure.audit.model.AuditEvent;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

public interface AuditPublisher {
    CompletableFuture<SendResult<String, AuditEvent>>
    publish(AuditEvent event);

}