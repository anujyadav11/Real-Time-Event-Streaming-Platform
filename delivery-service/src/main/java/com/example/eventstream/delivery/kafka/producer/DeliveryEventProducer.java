package com.example.eventstream.delivery.kafka.producer;

import com.example.eventstream.common.constants.KafkaTopics;
import com.example.eventstream.common.event.DeliveryAssignedEvent;
import com.example.eventstream.common.event.DeliveryAssignmentFailedEvent;
import com.example.eventstream.common.event.DeliveryStatusUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class DeliveryEventProducer {
    private static final Logger log =
            LoggerFactory.getLogger(DeliveryEventProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    public DeliveryEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public CompletableFuture<Void> publishAssigned(
            DeliveryAssignedEvent event) {
        log.info("Publishing DeliveryAssignedEvent for order {}",
                event.orderId());
        return kafkaTemplate.send(
                        KafkaTopics.DELIVERY_ASSIGNED,
                        event.orderId().toString(),
                        event)
                .thenAccept(result ->
                        log.info("DeliveryAssignedEvent published for order {}",
                                event.orderId()));

    }
    public CompletableFuture<Void> publishAssignmentFailed(
            DeliveryAssignmentFailedEvent event) {
        log.info("Publishing DeliveryAssignmentFailedEvent for order {}",
                event.orderId());
        return kafkaTemplate.send(
                        KafkaTopics.DELIVERY_ASSIGNMENT_FAILED,
                        event.orderId().toString(),
                        event)
                .thenAccept(result ->
                        log.info("DeliveryAssignmentFailedEvent published for order {}",
                                event.orderId()));
    }

    public CompletableFuture<Void> publishStatusUpdated(
            DeliveryStatusUpdatedEvent event) {
        log.info("Publishing DeliveryStatusUpdatedEvent for order {}", event.orderId());
        return kafkaTemplate.send(
                        KafkaTopics.DELIVERY_STATUS_UPDATED,
                        event.orderId().toString(),
                        event)
                .thenAccept(result -> log.info(
                        "DeliveryStatusUpdatedEvent published for order {}", event.orderId()));
    }
}
