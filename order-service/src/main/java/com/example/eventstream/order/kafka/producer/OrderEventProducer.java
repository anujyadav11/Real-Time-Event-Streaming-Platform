package com.example.eventstream.order.kafka.producer;


import com.example.eventstream.common.event.OrderCreatedEvent;
import com.example.eventstream.common.constants.KafkaTopics;
import com.example.eventstream.order.metrics.KafkaProducerMetrics;
import com.example.infrastructure.messaging.kafka.CorrelationAwareKafkaTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class OrderEventProducer {
    private static final Logger log =
            LoggerFactory.getLogger(OrderEventProducer.class);

    private final CorrelationAwareKafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    private final KafkaProducerMetrics metrics;

    public OrderEventProducer(CorrelationAwareKafkaTemplate<String, OrderCreatedEvent> kafkaTemplate,
                              KafkaProducerMetrics metrics) {
        this.kafkaTemplate = kafkaTemplate;
        this.metrics = metrics;
    }
    public CompletableFuture<SendResult<String, OrderCreatedEvent>> publish(OrderCreatedEvent event) {
        log.info("Publishing OrderCreatedEvent for order {}", event.orderId());
        return kafkaTemplate.send(
                KafkaTopics.ORDER_CREATED,
                event.orderId().toString(),
                event
        ).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info(
                        "Published OrderCreatedEvent. orderId={}, topic={}, partition={}, offset={}",
                        event.orderId(),
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                );
                metrics.success();
            } else {
                log.error("Failed publishing OrderCreatedEvent. orderId={}", event.orderId(), ex
                );
                metrics.failure();
            }
        });
    }
}
