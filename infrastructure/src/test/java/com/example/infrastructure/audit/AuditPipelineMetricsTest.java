package com.example.infrastructure.audit;

import com.example.infrastructure.audit.enricher.AuditEnricher;
import com.example.infrastructure.audit.metrics.AuditMetrics;
import com.example.infrastructure.audit.model.AuditEvent;
import com.example.infrastructure.audit.pipeline.AuditPipeline;
import com.example.infrastructure.audit.storage.AuditStorage;
import com.example.infrastructure.audit.validator.AuditValidator;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuditPipelineMetricsTest {

    @Test
    void recordsSuccessfulAndFailedAuditProcessing() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        AuditPipeline pipeline = new AuditPipeline(
                new AuditValidator(), new AuditEnricher(), event -> { }, new AuditMetrics(registry));

        pipeline.process(event());
        assertThatThrownBy(() -> pipeline.process(null))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(registry.get("audit_events_processed_total")
                .tag("outcome", "success").counter().count()).isEqualTo(1);
        assertThat(registry.get("audit_events_processed_total")
                .tag("outcome", "failure").counter().count()).isEqualTo(1);
    }

    private AuditEvent event() {
        return new AuditEvent(Instant.now(), "payment-service", "user-1", "admin",
                "PROCESS_PAYMENT", "PAYMENT", "payment-1", "SUCCESS", "corr-1", "trace-1", "127.0.0.1");
    }
}
