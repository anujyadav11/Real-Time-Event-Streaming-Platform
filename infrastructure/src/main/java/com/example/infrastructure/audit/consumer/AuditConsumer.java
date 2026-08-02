package com.example.infrastructure.audit.consumer;

import com.example.infrastructure.audit.constants.AuditTopics;
import com.example.infrastructure.audit.model.AuditEvent;
import com.example.infrastructure.audit.pipeline.AuditPipeline;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditConsumer {
    private final AuditPipeline pipeline;
    @KafkaListener(
            topics = AuditTopics.AUDIT_EVENTS,
            groupId = "audit-group"
    )
    public void consume(AuditEvent event) {
        pipeline.process(event);
    }
}