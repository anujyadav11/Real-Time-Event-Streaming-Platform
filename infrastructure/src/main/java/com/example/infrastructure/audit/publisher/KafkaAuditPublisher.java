package com.example.infrastructure.audit.publisher;

import com.example.infrastructure.audit.model.AuditEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaAuditPublisher implements AuditPublisher {
    @Override
    public void publish(AuditEvent event) {
        log.info("Publishing audit event: {}", event);
    }
}