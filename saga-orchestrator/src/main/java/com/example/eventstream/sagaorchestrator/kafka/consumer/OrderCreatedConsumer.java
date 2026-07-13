package com.example.eventstream.sagaorchestrator.kafka.consumer;

import com.example.eventstream.common.command.ReserveInventoryCommand;
import com.example.eventstream.common.constants.KafkaTopics;
import com.example.eventstream.common.event.OrderCreatedEvent;
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
public class OrderCreatedConsumer {
    private static final Logger log = LoggerFactory.getLogger(OrderCreatedConsumer.class);
    private static final String CONSUMER_NAME = "saga-orchestrator";
    private final SagaService sagaService;
    private final SagaCommandProducer sagaCommandProducer;
    private final IdempotencyService idempotencyService;

    public OrderCreatedConsumer(SagaService sagaService, SagaCommandProducer sagaCommandProducer, IdempotencyService idempotencyService){
        this.sagaService = sagaService;
        this.sagaCommandProducer = sagaCommandProducer;
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
            topics = KafkaTopics.ORDER_CREATED,
            groupId = "saga-group"
    )
    public void consume(OrderCreatedEvent event){
        if(idempotencyService.isProcessed(CONSUMER_NAME, event.eventId())){
            log.info("Duplicate OrderCreatedEvent ignore for order{}", event.orderId());
            return;
        }
        log.info("Starting Saga for order{}",event.orderId());
        sagaService.startSaga(event.orderId(), event.correlationId());
        sagaService.updateStatus(event.orderId(), SagaStatus.INVENTORY_PENDING);

        ReserveInventoryCommand command = new ReserveInventoryCommand(
                UUID.randomUUID(),
                event.orderId(),
                event.productId(),
                event.quantity(),
                event.totalAmount(),
                event.correlationId()
        );
        sagaCommandProducer
                .publishReserveInventoryCommand(command).join();
        idempotencyService.markProcessed(CONSUMER_NAME, event.eventId());
        log.info("ReserveInventoryCommand published for order {}", event.orderId());
    }
}
