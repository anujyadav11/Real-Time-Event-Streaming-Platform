package com.example.eventstream.sagaorchestrator.kafka.consumer;

import com.example.eventstream.common.command.SendNotificationCommand;
import com.example.eventstream.common.constants.KafkaTopics;
import com.example.eventstream.common.event.DeliveryAssignedEvent;
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
public class DeliveryAssignedConsumer {
    private static final Logger log =
            LoggerFactory.getLogger(DeliveryAssignedConsumer.class);
    private static final String CONSUMER_NAME =
            "saga-delivery";
    private final SagaService sagaService;
    private final SagaCommandProducer commandProducer;
    private final IdempotencyService idempotencyService;
    public DeliveryAssignedConsumer(
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
            topics = KafkaTopics.DELIVERY_ASSIGNED,
            groupId = "saga-group"
    )
    public void consume(DeliveryAssignedEvent event) {
        if (idempotencyService.isProcessed(
                CONSUMER_NAME,
                event.eventId())) {
            log.info("Duplicate DeliveryAssignedEvent ignored for order {}",
                    event.orderId());
            return;
        }
        log.info("Delivery assigned for order {}", event.orderId());
        sagaService.transition(
                event.orderId(),
                SagaStatus.DELIVERY_COMPLETED,
                SagaStatus.NOTIFICATION_PENDING
        );
        SendNotificationCommand command =
                new SendNotificationCommand(
                        UUID.randomUUID(),
                        event.orderId(),
                        event.correlationId()
                );
        commandProducer
                .publishSendNotificationCommand(command)
                .join();
        idempotencyService.markProcessed(
                CONSUMER_NAME,
                event.eventId()
        );
        log.info(
                "SendNotificationCommand published for order {}",
                event.orderId()
        );
    }
}
