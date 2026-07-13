package com.example.eventstream.inventory.kafka.consumer;

import com.example.eventstream.common.command.ReserveInventoryCommand;
import com.example.eventstream.common.constants.KafkaTopics;
import com.example.eventstream.common.event.InventoryReservationFailedEvent;
import com.example.eventstream.common.event.InventoryReservedEvent;
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

import java.util.UUID;

@Component
public class ReserveInventoryCommandConsumer {
    private static final Logger log =
            LoggerFactory.getLogger(ReserveInventoryCommandConsumer.class);
    private static final String CONSUMER_NAME = "inventory-command";
    private final InventoryService inventoryService;
    private final InventoryEventProducer producer;
    private final IdempotencyService idempotencyService;

    public ReserveInventoryCommandConsumer(
            InventoryService inventoryService,
            InventoryEventProducer producer,
            IdempotencyService idempotencyService) {
        this.inventoryService = inventoryService;
        this.producer = producer;
        this.idempotencyService = idempotencyService;
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
            topics = KafkaTopics.RESERVE_INVENTORY_COMMAND,
            groupId = "inventory-group"
    )
    public void consume(ReserveInventoryCommand command) {
        if (idempotencyService.isProcessed(
                CONSUMER_NAME,
                command.commandId())) {
            log.info(
                    "Duplicate ReserveInventoryCommand ignored for order {}",
                    command.orderId());
            return;
        }
        try {
            inventoryService.reserveInventory(
                    command.productId(),
                    command.quantity());
            InventoryReservedEvent event =
                    new InventoryReservedEvent(
                            UUID.randomUUID(),
                            command.orderId(),
                            command.productId(),
                            command.quantity(),
                            command.amount(),
                            command.correlationId()
                    );
            producer.publishReserved(event).join();
            idempotencyService.markProcessed(
                    CONSUMER_NAME,
                    command.commandId());
            log.info(
                    "InventoryReservedEvent published for order {}",
                    command.orderId());
        } catch (InventoryNotFoundException |
                 InsufficientInventoryException ex) {
            InventoryReservationFailedEvent failed =
                    new InventoryReservationFailedEvent(
                            UUID.randomUUID(),
                            command.orderId(),
                            command.productId(),
                            command.quantity(),
                            ex.getMessage(),
                            command.correlationId()
                    );
            producer.publishReservationFailed(failed).join();
            idempotencyService.markProcessed(
                    CONSUMER_NAME,
                    command.commandId());
            log.warn(
                    "Inventory reservation failed for order {}",
                    command.orderId());
        }
    }
}