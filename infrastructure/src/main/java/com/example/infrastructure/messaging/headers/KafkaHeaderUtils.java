package com.example.infrastructure.messaging.headers;

import com.example.infrastructure.observability.correlation.CorrelationIdConstants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
/**
 * Utility methods for working with Kafka record headers.
 */
public final class KafkaHeaderUtils {
    private KafkaHeaderUtils() {
        throw new IllegalStateException("Utility class");
    }
    /**
     * Adds or replaces the Correlation ID header.
     */
    public static <K, V> void addCorrelationId(
            ProducerRecord<K, V> record,
            String correlationId) {
        if (correlationId == null || correlationId.isBlank()) {
            return;
        }
        record.headers().remove(
                CorrelationIdConstants.CORRELATION_ID_HEADER
        );
        record.headers().add(
                CorrelationIdConstants.CORRELATION_ID_HEADER,
                correlationId.getBytes(StandardCharsets.UTF_8)
        );
    }
    /**
     * Reads the Correlation ID header from a Kafka message.
     */
    public static Optional<String> getCorrelationId(
            ConsumerRecord<?, ?> record) {
        Header header = record.headers().lastHeader(
                CorrelationIdConstants.CORRELATION_ID_HEADER
        );
        if (header == null) {
            return Optional.empty();
        }
        return Optional.of(
                new String(header.value(), StandardCharsets.UTF_8)
        );
    }
}