package com.example.infrastructure.audit.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class AuditMetrics {

    private final Counter successCounter;
    private final Counter failureCounter;

    public AuditMetrics(MeterRegistry registry) {
        successCounter =
                Counter.builder("audit_events_processed_total")
                        .tag("outcome", "success")
                        .register(registry);
        failureCounter = Counter.builder("audit_events_processed_total")
                .tag("outcome", "failure")
                .register(registry);
    }
    public void recordSuccess() {
        successCounter.increment();
    }
    public void recordFailure() {
        failureCounter.increment();
    }
}
