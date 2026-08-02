package com.example.infrastructure.audit.config;

import com.example.infrastructure.audit.constants.AuditTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class AuditKafkaConfiguration {
    @Bean
    public NewTopic auditEventsTopic() {
        return TopicBuilder
                .name(AuditTopics.AUDIT_EVENTS)
                .partitions(3)
                .replicas(1)
                .build();
    }
}