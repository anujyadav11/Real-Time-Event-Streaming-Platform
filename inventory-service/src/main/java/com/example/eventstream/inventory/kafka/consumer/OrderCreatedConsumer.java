package com.example.eventstream.inventory.kafka.consumer;

import com.example.eventstream.common.constants.KafkaTopics;
import com.example.eventstream.common.event.InventoryReservedEvent;
import com.example.eventstream.common.event.OrderCreatedEvent;
import com.example.eventstream.inventory.kafka.producer.InventoryEventProducer;
import com.example.infrastructure.redis.IdempotencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class OrderCreatedConsumer {
    private final InventoryEventProducer producer;
    private final IdempotencyService idempotencyService;

    public OrderCreatedConsumer(InventoryEventProducer producer, IdempotencyService idempotencyService) {
        this.producer = producer;
        this.idempotencyService = idempotencyService;
    }
    private static final Logger log = LoggerFactory.getLogger(OrderCreatedConsumer.class);

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(
                    delay = 2000,
                    multiplier = 2.0
            ),
            dltTopicSuffix = "-dlt"
    )
    @KafkaListener(
            topics = KafkaTopics.ORDER_CREATED,
            groupId = "inventory-group"
    )
    public void consume(OrderCreatedEvent event){
        log.info("Received OrderCreatedEvent: {}", event.orderId());
        log.info("Checking inventory....");
        if(idempotencyService.isProcessed(event.eventId())){
            log.info("Duplicate event ignored");
            return;
        }
        InventoryReservedEvent inventoryEvent =
                new InventoryReservedEvent(
                        UUID.randomUUID(),
                        event.orderId(),
                        event.totalAmount(),
                        true,
                        LocalDateTime.now(),
                        event.correlationId()
                );
        producer.publish(inventoryEvent);
        log.info("Inventory reserved.");
        idempotencyService.markProcessed(event.eventId());
    }
}
