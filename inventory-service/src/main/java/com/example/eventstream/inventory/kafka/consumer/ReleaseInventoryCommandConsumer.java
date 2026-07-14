package com.example.eventstream.inventory.kafka.consumer;

import com.example.eventstream.common.command.ReleaseInventoryCommand;
import com.example.eventstream.common.constants.KafkaTopics;
import com.example.eventstream.common.event.InventoryReleasedEvent;
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
public class ReleaseInventoryCommandConsumer {
    private static final Logger log =
            LoggerFactory.getLogger(ReleaseInventoryCommandConsumer.class);
    private static final String CONSUMER_NAME =
            "inventory-release-command";
    private final InventoryService inventoryService;
    private final InventoryEventProducer producer;
    private final IdempotencyService idempotencyService;
    public ReleaseInventoryCommandConsumer(
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
            topics = KafkaTopics.RELEASE_INVENTORY_COMMAND,
            groupId = "inventory-group"
    )
    public void consume(ReleaseInventoryCommand command) {
        if (idempotencyService.isProcessed(
                CONSUMER_NAME,
                command.commandId())) {
            log.info(
                    "Duplicate ReleaseInventoryCommand ignored for order {}",
                    command.orderId());
            return;
        }
        inventoryService.releaseInventory(
                command.productId(),
                command.quantity()
        );
        InventoryReleasedEvent event =
                new InventoryReleasedEvent(
                        UUID.randomUUID(),
                        command.orderId(),
                        command.productId(),
                        command.quantity(),
                        command.correlationId()
                );
        producer.publishReleased(event).join();
        idempotencyService.markProcessed(
                CONSUMER_NAME,
                command.commandId());
        log.info(
                "InventoryReleasedEvent published for order {}",
                command.orderId());
    }
}