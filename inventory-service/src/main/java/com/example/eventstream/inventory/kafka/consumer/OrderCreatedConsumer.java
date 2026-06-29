package com.example.eventstream.inventory.kafka.consumer;

import com.example.eventstream.common.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedConsumer {
    private static final Logger log = LoggerFactory.getLogger(OrderCreatedConsumer.class);

    @KafkaListener(
            topics = "order-created",
            groupId = "inventory-group"
    )
    public void consume(OrderCreatedEvent event){
        log.info("Received OrderCreatedEvent: {}", event);
        log.info("Reserving inventory for order {}",event.orderId());
        log.info("Inventroy reserved successfully");
    }
}
