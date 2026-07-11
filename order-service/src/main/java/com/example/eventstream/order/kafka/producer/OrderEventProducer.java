package com.example.eventstream.order.kafka.producer;


import com.example.eventstream.common.event.OrderCreatedEvent;
import com.example.eventstream.common.constants.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class OrderEventProducer {
    private static final Logger log =
            LoggerFactory.getLogger(OrderEventProducer.class);

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public OrderEventProducer(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public CompletableFuture<SendResult<String, OrderCreatedEvent>> publish(OrderCreatedEvent event) {
        log.info("Publishing OrderCreatedEvent for order {}", event.orderId());
        return kafkaTemplate.send(
                KafkaTopics.ORDER_CREATED,
                event.orderId().toString(),
                event
        ).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("OrderCreatedEvent published successfully for order {}", event.orderId());
            }
        });
    }
}
