package com.example.infrastructure.audit.enricher;

import com.example.infrastructure.audit.model.AuditEvent;
import org.springframework.stereotype.Component;

@Component
public class AuditEnricher {
    public AuditEvent enrich(AuditEvent event) {
        // Future:
        // Geo Location
        // Tenant
        // Hostname
        // Kubernetes Pod
        // Environment
        return event;
    }
}