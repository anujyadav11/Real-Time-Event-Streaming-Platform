package com.example.eventstream.inventory.kafka.consumer;

import com.example.eventstream.common.constants.KafkaTopics;
import com.example.eventstream.common.event.InventoryReservationFailedEvent;
import com.example.eventstream.common.event.InventoryReservedEvent;
import com.example.eventstream.common.event.OrderCreatedEvent;
import com.example.eventstream.inventory.exception.InsufficientInventoryException;
import com.example.eventstream.inventory.exception.InventoryNotFoundException;
import com.example.eventstream.inventory.kafka.producer.InventoryEventProducer;
import com.example.eventstream.inventory.service.InventoryService;
import com.example.infrastructure.redis.IdempotencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

//@Component
public class OrderCreatedConsumer {
    private static final Logger log = LoggerFactory.getLogger(OrderCreatedConsumer.class);
    private static final String CONSUMER_NAME = "inventory-service";
    private final InventoryEventProducer producer;
    private final IdempotencyService idempotencyService;
    private final InventoryService inventoryService;

    public OrderCreatedConsumer(InventoryEventProducer producer,
                                IdempotencyService idempotencyService,
                                InventoryService inventoryService) {
        this.producer = producer;
        this.idempotencyService = idempotencyService;
        this.inventoryService = inventoryService;
    }

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
        if(idempotencyService.isProcessed(CONSUMER_NAME, event.eventId())){
            log.info("Duplicate OrderCreatedEvent ignored for order {}", event.orderId());
            return;
        }
        try{
            inventoryService.reserveInventory(event.productId(),event.quantity());
            log.info(
                    "Reserved inventory for product {} (quantity {}) for order {}",
                    event.productId(),
                    event.quantity(),
                    event.orderId()
            );

            InventoryReservedEvent inventoryEvent =
                    new InventoryReservedEvent(
                            UUID.randomUUID(),
                            event.orderId(),
                            event.productId(),
                            event.quantity(),
                            event.totalAmount(),
                            event.correlationId()
                    );
            producer.publishReserved(inventoryEvent).join();
            idempotencyService.markProcessed(CONSUMER_NAME, event.eventId()
            );
            log.info("InventoryReservedEvent published for Order {}", event.orderId());
        }catch (InventoryNotFoundException | InsufficientInventoryException ex){
            log.warn("Inventory reservation failed for order {} : {}" , event.orderId(), ex.getMessage());
            InventoryReservationFailedEvent failedEvent = new InventoryReservationFailedEvent(
                    UUID.randomUUID(),
                    event.orderId(),
                    event.productId(),
                    event.quantity(),
                    ex.getMessage(),
                    event.correlationId()
            );
            producer.publishReservationFailed(failedEvent).join();
            idempotencyService.markProcessed(CONSUMER_NAME, event.eventId());
        }
    }
}
