package com.example.infrastructure.security.internal.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
public class InternalSecurityMetrics {

    private final Counter authenticatedRequests;
    private final Counter rejectedRequests;
    private final Counter unknownService;

    public InternalSecurityMetrics(
            MeterRegistry registry) {
        authenticatedRequests =
                registry.counter(
                        "internal_requests_authenticated_total");
        rejectedRequests =
                registry.counter(
                        "internal_requests_rejected_total");
        unknownService =
                registry.counter(
                        "internal_unknown_service_total");
    }
    public void authenticated() {
        authenticatedRequests.increment();
    }
    public void rejected() {
        rejectedRequests.increment();
    }
    public void unknownService() {
        unknownService.increment();
    }

}
