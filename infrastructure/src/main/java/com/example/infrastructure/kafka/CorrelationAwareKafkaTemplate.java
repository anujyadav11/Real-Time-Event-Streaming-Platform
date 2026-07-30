package com.example.infrastructure.kafka;

import com.example.infrastructure.observability.correlation.CorrelationIdHolder;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * Infrastructure wrapper around KafkaTemplate.
 * Automatically propagates the current Correlation ID
 * into Kafka record headers.
 */
public class CorrelationAwareKafkaTemplate<K, V> {
    private final KafkaTemplate<K, V> kafkaTemplate;
    public CorrelationAwareKafkaTemplate(
            KafkaTemplate<K, V> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public CompletableFuture<SendResult<K, V>> send(
            String topic,
            K key,
            V value) {
        ProducerRecord<K, V> record =
                new ProducerRecord<>(topic, key, value);
        KafkaHeaderUtils.addCorrelationId(
                record,
                CorrelationIdHolder.get()
        );
        return kafkaTemplate.send(record);
    }
}
