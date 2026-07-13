package com.example.eventstream.sagaorchestrator.kafka.consumer;

import com.example.eventstream.common.command.CreateDeliveryCommand;
import com.example.eventstream.common.constants.KafkaTopics;
import com.example.eventstream.common.event.PaymentCompletedEvent;
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
public class PaymentCompletedConsumer {
    private static final Logger log =
            LoggerFactory.getLogger(PaymentCompletedConsumer.class);
    private static final String CONSUMER_NAME = "saga-payment";
    private final SagaService sagaService;
    private final SagaCommandProducer commandProducer;
    private final IdempotencyService idempotencyService;

    public PaymentCompletedConsumer(
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
            topics = KafkaTopics.PAYMENT_COMPLETED,
            groupId = "saga-group"
    )
    public void consume(PaymentCompletedEvent event) {
        if (idempotencyService.isProcessed(
                CONSUMER_NAME,
                event.eventId())) {
            log.info(
                    "Duplicate PaymentCompletedEvent ignored for order {}",
                    event.orderId());
            return;
        }
        log.info(
                "Payment completed for order {}",
                event.orderId());

        sagaService.updateStatus(
                event.orderId(),
                SagaStatus.PAYMENT_COMPLETED);
        
        CreateDeliveryCommand command =
                new CreateDeliveryCommand(
                        UUID.randomUUID(),
                        event.orderId(),
                        event.correlationId()
                );
        commandProducer
                .publishCreateDeliveryCommand(command)
                .join();
        idempotencyService.markProcessed(
                CONSUMER_NAME,
                event.eventId());
        log.info(
                "CreateDeliveryCommand published for order {}",
                event.orderId());
    }
}