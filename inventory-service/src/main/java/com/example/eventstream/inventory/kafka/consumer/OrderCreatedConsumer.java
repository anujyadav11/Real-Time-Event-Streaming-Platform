package com.example.eventstream.inventory.kafka.consumer;

import com.example.eventstream.common.event.InventoryReservedEvent;
import com.example.eventstream.common.event.OrderCreatedEvent;
import com.example.eventstream.inventory.kafka.producer.InventoryEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OrderCreatedConsumer {
    private final InventoryEventProducer producer;

    public OrderCreatedConsumer(InventoryEventProducer producer) {
        this.producer = producer;
    }
    private static final Logger log = LoggerFactory.getLogger(OrderCreatedConsumer.class);

    @KafkaListener(
            topics = "order-created",
            groupId = "inventory-group"
    )
    public void consume(OrderCreatedEvent event){
        log.info("Received OrderCreatedEvent: {}", event.orderId());
        log.info("Checking inventory....");

        InventoryReservedEvent inventoryEvent =
                new InventoryReservedEvent(
                        event.orderId(),
                        true,
                        LocalDateTime.now()
                );
        producer.publish(inventoryEvent);
        log.info("Inventory reserved.");
    }
}
