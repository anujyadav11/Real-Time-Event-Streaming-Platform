package com.example.eventstream.payment.kafka.producer;

import com.example.eventstream.common.constants.KafkaTopics;
import com.example.eventstream.common.event.PaymentCompletedEvent;
import com.example.eventstream.common.event.PaymentFailedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class PaymentEventProducer {

    private static final Logger log =
            LoggerFactory.getLogger(PaymentEventProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<Void> publishCompleted(PaymentCompletedEvent event) {
        log.info("Publishing PaymentCompletedEvent for order {}", event.orderId());
        return kafkaTemplate.send(
                        KafkaTopics.PAYMENT_COMPLETED,
                        event.orderId().toString(),
                        event
                )
                .thenAccept(result ->
                        log.info("PaymentCompletedEvent published successfully"));
    }
    public CompletableFuture<Void> publishFailed(PaymentFailedEvent event) {
        log.info("Publishing PaymentFailedEvent for order {}", event.orderId());
        return kafkaTemplate.send(
                        KafkaTopics.PAYMENT_FAILED,
                        event.orderId().toString(),
                        event
                )
                .thenAccept(result ->
                        log.info("PaymentFailedEvent published successfully"));
    }
}