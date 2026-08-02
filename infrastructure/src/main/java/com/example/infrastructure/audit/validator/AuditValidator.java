package com.example.infrastructure.audit.validator;

import com.example.infrastructure.audit.model.AuditEvent;
import org.springframework.stereotype.Component;

@Component
public class AuditValidator {
    public void validate(AuditEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("AuditEvent cannot be null.");
        }
        if (event.action() == null || event.action().isBlank()) {
            throw new IllegalArgumentException("Action is required.");
        }
        if (event.resource() == null || event.resource().isBlank()) {
            throw new IllegalArgumentException("Resource is required.");
        }
    }
}