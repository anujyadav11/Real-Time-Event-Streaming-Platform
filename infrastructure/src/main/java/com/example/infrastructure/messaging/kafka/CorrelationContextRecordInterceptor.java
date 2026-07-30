package com.example.infrastructure.messaging.kafka;

import com.example.infrastructure.messaging.headers.KafkaHeaderUtils;
import com.example.infrastructure.observability.correlation.CorrelationIdHolder;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.RecordInterceptor;

/**
 * Restores correlation context before every Kafka listener invocation and
 * guarantees that pooled consumer threads do not leak it to the next record.
 */
public class CorrelationContextRecordInterceptor<K, V>
        implements RecordInterceptor<K, V> {

    @Override
    public ConsumerRecord<K, V> intercept(
            ConsumerRecord<K, V> record,
            Consumer<K, V> consumer) {
        CorrelationIdHolder.clear();
        KafkaHeaderUtils.getCorrelationId(record)
                .ifPresent(CorrelationIdHolder::set);
        return record;
    }

    @Override
    public void afterRecord(
            ConsumerRecord<K, V> record,
            Consumer<K, V> consumer) {
        CorrelationIdHolder.clear();
    }
}
