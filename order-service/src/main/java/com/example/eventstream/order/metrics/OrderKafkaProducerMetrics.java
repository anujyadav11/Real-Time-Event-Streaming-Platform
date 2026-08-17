package com.example.eventstream.order.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class OrderKafkaProducerMetrics {
    private final Counter success;
    private final Counter failure;
    public OrderKafkaProducerMetrics(MeterRegistry registry) {
        success = registry.counter(
                "kafka_producer_success_total"
        );
        failure = registry.counter(
                "kafka_producer_failure_total"
        );
    }
    public void success() {
        success.increment();
    }
    public void failure() {
        failure.increment();
    }
}