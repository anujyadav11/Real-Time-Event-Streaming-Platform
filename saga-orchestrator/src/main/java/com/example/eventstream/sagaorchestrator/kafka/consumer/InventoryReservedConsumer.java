package com.example.eventstream.sagaorchestrator.kafka.consumer;

import com.example.eventstream.common.command.ProcessPaymentCommand;
import com.example.eventstream.common.constants.KafkaTopics;
import com.example.eventstream.common.event.InventoryReservedEvent;
import com.example.eventstream.sagaorchestrator.kafka.producer.SagaCommandProducer;
import com.example.eventstream.sagaorchestrator.service.SagaService;
import com.example.eventstream.sagaorchestrator.state.SagaStatus;
import com.example.infrastructure.redis.IdempotencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class InventoryReservedConsumer {
    private static final Logger log =
            LoggerFactory.getLogger(InventoryReservedConsumer.class);
    private static final String CONSUMER_NAME =
            "saga-inventory";
    private final SagaService sagaService;
    private final SagaCommandProducer commandProducer;
    private final IdempotencyService idempotencyService;
    public InventoryReservedConsumer(
            SagaService sagaService,
            SagaCommandProducer commandProducer,
            IdempotencyService idempotencyService) {
        this.sagaService = sagaService;
        this.commandProducer = commandProducer;
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
            topics = KafkaTopics.INVENTORY_RESERVED,
            groupId = "saga-group"
    )
    public void consume(InventoryReservedEvent event) {
        if (idempotencyService.isProcessed(
                CONSUMER_NAME,
                event.eventId())) {
            log.info(
                    "Duplicate InventoryReservedEvent ignored for order {}",
                    event.orderId());
            return;
        }
        log.info(
                "Inventory reserved for order {}",
                event.orderId());

        sagaService.transition(
                event.orderId(),
                SagaStatus.INVENTORY_COMPLETED,
                SagaStatus.PAYMENT_PENDING
        );
        
        ProcessPaymentCommand command =
                new ProcessPaymentCommand(
                        UUID.randomUUID(),
                        event.orderId(),
                        event.productId(),
                        event.quantity(),
                        event.amount(),
                        event.correlationId()
                );
        commandProducer
                .publishProcessPaymentCommand(command)
                .join();
        idempotencyService.markProcessed(
                CONSUMER_NAME,
                event.eventId());
        log.info(
                "ProcessPaymentCommand published for order {}",
                event.orderId());
    }
}
