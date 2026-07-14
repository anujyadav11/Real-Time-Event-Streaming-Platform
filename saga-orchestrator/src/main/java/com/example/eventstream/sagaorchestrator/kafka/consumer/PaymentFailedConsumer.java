package com.example.eventstream.sagaorchestrator.kafka.consumer;


import com.example.eventstream.common.command.ReleaseInventoryCommand;
import com.example.eventstream.common.constants.KafkaTopics;
import com.example.eventstream.common.event.PaymentFailedEvent;
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
public class PaymentFailedConsumer {
    private static final Logger log =
            LoggerFactory.getLogger(PaymentFailedConsumer.class);
    private static final String CONSUMER_NAME =
            "saga-payment-failed";
    private final SagaService sagaService;
    private final SagaCommandProducer commandProducer;
    private final IdempotencyService idempotencyService;
    public PaymentFailedConsumer(
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
            topics = KafkaTopics.PAYMENT_FAILED,
            groupId = "saga-group"
    )
    public void consume(PaymentFailedEvent event) {
        if (idempotencyService.isProcessed(
                CONSUMER_NAME,
                event.eventId())) {
            log.info(
                    "Duplicate PaymentFailedEvent ignored for order {}",
                    event.orderId());
            return;
        }
        log.warn(
                "Payment failed for order {}. Starting compensation.",
                event.orderId());

        sagaService.updateStatus(
                event.orderId(),
                SagaStatus.PAYMENT_FAILED);
        sagaService.updateStatus(
                event.orderId(),
                SagaStatus.COMPENSATING);
        ReleaseInventoryCommand command =
                new ReleaseInventoryCommand(
                        UUID.randomUUID(),
                        event.orderId(),
                        event.productId(),
                        event.quantity(),
                        event.correlationId()
                );
        commandProducer
                .publishReleaseInventoryCommand(command)
                .join();
        idempotencyService.markProcessed(
                CONSUMER_NAME,
                event.eventId());
        log.info(
                "ReleaseInventoryCommand published for order {}",
                event.orderId());
    }
}