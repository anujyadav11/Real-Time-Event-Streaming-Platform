package com.example.eventstream.order.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class OutboxMetrics {
    private final Counter recoveredEvents;
    public OutboxMetrics(MeterRegistry registry
    ) {
        recoveredEvents =
                registry.counter(
                        "outbox_recovered_total"
                );
    }
    public void recovered() {
        recoveredEvents.increment();
    }
}