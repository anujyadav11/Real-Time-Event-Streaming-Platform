package com.example.eventstream.inventory.kafka.producer;

import com.example.eventstream.common.event.InventoryReservedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventProducer {
    private static final Logger log =
            LoggerFactory.getLogger(InventoryEventProducer.class);
    private final KafkaTemplate<String, InventoryReservedEvent> kafkaTemplate;
    public InventoryEventProducer(
            KafkaTemplate<String, InventoryReservedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void publish(InventoryReservedEvent event) {
        log.info("Publishing InventoryReservedEvent for order {}",
                event.orderId());
        kafkaTemplate.send(
                "inventory-reserved",
                event.orderId().toString(),
                event
        );
    }
}