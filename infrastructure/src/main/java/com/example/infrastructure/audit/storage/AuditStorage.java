package com.example.infrastructure.audit.storage;

import com.example.infrastructure.audit.model.AuditEvent;

public interface AuditStorage {
    void save(AuditEvent event);
}