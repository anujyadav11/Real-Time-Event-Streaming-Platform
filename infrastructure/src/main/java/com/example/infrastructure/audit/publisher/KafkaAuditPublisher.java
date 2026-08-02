package com.example.infrastructure.audit.publisher;

import com.example.infrastructure.audit.constants.AuditTopics;
import com.example.infrastructure.audit.model.AuditEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaAuditPublisher implements AuditPublisher {

    private final KafkaTemplate<String, AuditEvent> kafkaTemplate;

    @Override
    public CompletableFuture<SendResult<String, AuditEvent>> publish(
            AuditEvent event) {
        log.info("Publishing AuditEvent for action {}", event.action());
        return kafkaTemplate.send(
                AuditTopics.AUDIT_EVENTS,
                UUID.randomUUID().toString(),
                event
        ).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("AuditEvent published successfully: {}", event.action());
            } else {
                log.error("Failed to publish AuditEvent", ex);
            }
        });
    }
}