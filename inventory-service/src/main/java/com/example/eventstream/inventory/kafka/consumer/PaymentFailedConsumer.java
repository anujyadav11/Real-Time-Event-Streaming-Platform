package com.example.eventstream.inventory.kafka.consumer;

import com.example.eventstream.common.constants.KafkaTopics;
import com.example.eventstream.common.event.PaymentFailedEvent;
import com.example.eventstream.inventory.service.InventoryService;
import com.example.infrastructure.redis.IdempotencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
public class PaymentFailedConsumer {
    private static final Logger log = LoggerFactory.getLogger(PaymentFailedConsumer.class);
    private static final String CONSUMER_NAME = "inventory-payment-failed";

    private final InventoryService inventoryService;
    private final IdempotencyService idempotencyService;

    public PaymentFailedConsumer(InventoryService inventoryService, IdempotencyService idempotencyService){
        this.inventoryService = inventoryService;
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
            topics = KafkaTopics.PAYMENT_FAILED,
            groupId = "inventory-group"
    )
    public void consume(PaymentFailedEvent event){
        if(idempotencyService.isProcessed(CONSUMER_NAME, event.eventId())){
            log.info("Duplicate PaymentFailedEvent Ignored for order {}", event.orderId());
            return;
        }
        log.info("Releasing Inventory for order {}", event.orderId());
        inventoryService.releaseInventory(
                event.productId(),
                event.quantity()
        );
        idempotencyService.markProcessed(CONSUMER_NAME, event.eventId());
        log.info("Inventory released for order : {}", event.orderId());
    }
}
