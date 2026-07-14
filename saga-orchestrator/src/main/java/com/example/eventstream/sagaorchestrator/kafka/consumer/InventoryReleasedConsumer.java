package com.example.eventstream.sagaorchestrator.kafka.consumer;

import com.example.eventstream.common.constants.KafkaTopics;
import com.example.eventstream.common.event.InventoryReleasedEvent;
import com.example.eventstream.sagaorchestrator.service.SagaService;
import com.example.eventstream.sagaorchestrator.state.SagaStatus;
import com.example.infrastructure.redis.IdempotencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
public class InventoryReleasedConsumer {
    private static final Logger log =
            LoggerFactory.getLogger(InventoryReleasedConsumer.class);
    private static final String CONSUMER_NAME =
            "saga-inventory-released";
    private final SagaService sagaService;
    private final IdempotencyService idempotencyService;
    public InventoryReleasedConsumer(
            SagaService sagaService,
            IdempotencyService idempotencyService) {
        this.sagaService = sagaService;
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
            topics = KafkaTopics.INVENTORY_RELEASED,
            groupId = "saga-group"
    )
    public void consume(InventoryReleasedEvent event) {
        if (idempotencyService.isProcessed(
                CONSUMER_NAME,
                event.eventId())) {
            log.info(
                    "Duplicate InventoryReleasedEvent ignored for order {}",
                    event.orderId());

            return;
        }
        log.info(
                "Inventory released for order {}",
                event.orderId());
        sagaService.updateStatus(
                event.orderId(),
                SagaStatus.COMPENSATED);
        idempotencyService.markProcessed(
                CONSUMER_NAME,
                event.eventId());
        log.info(
                "Saga compensated successfully for order {}",
                event.orderId());
    }
}