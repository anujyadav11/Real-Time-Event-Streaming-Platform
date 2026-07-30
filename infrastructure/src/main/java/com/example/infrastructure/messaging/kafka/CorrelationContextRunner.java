package com.example.infrastructure.messaging.kafka;

import com.example.infrastructure.messaging.headers.KafkaHeaderUtils;
import com.example.infrastructure.observability.correlation.CorrelationIdHolder;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Restores the Correlation ID from Kafka headers,
 * executes business logic and guarantees cleanup.
 */
public final class CorrelationContextRunner {
    private CorrelationContextRunner() {
        throw new IllegalStateException("Utility class");
    }
    public static void run(
            ConsumerRecord<?, ?> record,
            Runnable task) {
        try {
            KafkaHeaderUtils.getCorrelationId(record)
                    .ifPresent(CorrelationIdHolder::set);
            task.run();
        } finally {
            CorrelationIdHolder.clear();
        }
    }
}