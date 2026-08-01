package com.example.infrastructure.audit.publisher;

import com.example.infrastructure.audit.model.AuditEvent;

public interface AuditPublisher {
    void publish(AuditEvent event);
}