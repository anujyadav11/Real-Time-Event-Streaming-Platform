package com.example.eventstream.gateway.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class GatewayMetrics {  private final MeterRegistry registry;

    public GatewayMetrics(MeterRegistry registry) {
        this.registry = registry;
    }
    public void incrementRejected(
            String route,
            String method,
            String status
    ) {
        Counter.builder("gateway_rate_limit_rejected_total")
                .description("Rejected requests by Gateway Rate Limiter")
                .tag("route", route)
                .tag("method", method)
                .tag("status", status)
                .register(registry)
                .increment();
    }
}
