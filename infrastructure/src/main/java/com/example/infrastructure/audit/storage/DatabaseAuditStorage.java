package com.example.infrastructure.audit.storage;

import com.example.infrastructure.audit.model.AuditEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DatabaseAuditStorage implements AuditStorage {
    @Override
    public void save(AuditEvent event) {
        log.info("Persisting AuditEvent {}", event.action());
    }
}