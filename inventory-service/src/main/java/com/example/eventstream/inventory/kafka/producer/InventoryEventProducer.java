package com.example.eventstream.inventory.kafka.producer;

import com.example.eventstream.common.constants.KafkaTopics;
import com.example.eventstream.common.event.InventoryReservationFailedEvent;
import com.example.eventstream.common.event.InventoryReservedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class InventoryEventProducer {
    private static final Logger log =
            LoggerFactory.getLogger(InventoryEventProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    public InventoryEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public CompletableFuture<Void> publishReserved(
            InventoryReservedEvent event) {
        log.info("Publishing InventoryReservedEvent for order {}",
                event.orderId());
        return kafkaTemplate.send(
                        KafkaTopics.INVENTORY_RESERVED,
                        event.orderId().toString(),
                        event)
                .thenAccept(result ->
                        log.info("InventoryReservedEvent published successfully"));
    }
    public CompletableFuture<Void> publishReservationFailed(
            InventoryReservationFailedEvent event) {
        log.info("Publishing InventoryReservationFailedEvent for order {}",
                event.orderId());
        return kafkaTemplate.send(
                        KafkaTopics.INVENTORY_RESERVATION_FAILED,
                        event.orderId().toString(),
                        event)
                .thenAccept(result ->
                        log.info("InventoryReservationFailedEvent published successfully"));
    }
}