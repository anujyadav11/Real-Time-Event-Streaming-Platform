package com.example.eventstream.inventory.kafka.consumer;

import com.example.eventstream.common.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedDLTConsumer {
    private static final Logger log = LoggerFactory.getLogger(OrderCreatedDLTConsumer.class);
    @KafkaListener(
            topics = "order-created-dlt",
            groupId = "inventory-group"
    )
    public void consume(OrderCreatedEvent event) {
        log.error("DLT Message -> OrderId: {}",event.orderId());
    }
}
