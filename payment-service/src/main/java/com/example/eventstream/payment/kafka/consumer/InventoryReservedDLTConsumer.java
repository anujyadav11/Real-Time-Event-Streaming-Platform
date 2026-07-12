package com.example.eventstream.payment.kafka.consumer;

import com.example.eventstream.common.event.InventoryReservedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryReservedDLTConsumer {
    private static final Logger log = LoggerFactory.getLogger(InventoryReservedDLTConsumer.class);
    @KafkaListener(
            topics = "inventory-reserved-dlt",
            groupId = "payment-group"
    )
    public void consume(InventoryReservedEvent event) {
        log.error(
                "DLT Message -> OrderId: {}, Amount: {}",
                event.orderId(),
                event.amount()
        );
    }
}
