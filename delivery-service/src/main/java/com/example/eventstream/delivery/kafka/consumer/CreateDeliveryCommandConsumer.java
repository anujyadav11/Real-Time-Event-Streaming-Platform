package com.example.eventstream.delivery.kafka.consumer;

import com.example.eventstream.common.command.CreateDeliveryCommand;
import com.example.eventstream.common.constants.KafkaTopics;
import com.example.eventstream.delivery.service.DeliveryService;
import com.example.infrastructure.redis.IdempotencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
public class CreateDeliveryCommandConsumer {
    private static final Logger log = LoggerFactory.getLogger(CreateDeliveryCommandConsumer.class);
    private static final String CONSUMER_NAME = "delivery-command";
    private final DeliveryService deliveryService;
    private final IdempotencyService idempotencyService;

    public CreateDeliveryCommandConsumer(DeliveryService deliveryService, IdempotencyService idempotencyService){
        this.deliveryService = deliveryService;
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
            topics = KafkaTopics.CREATE_DELIVERY_COMMAND,
            groupId = "delivery-group"
    )
    public void consume(CreateDeliveryCommand command) {
        if (idempotencyService.isProcessed(
                CONSUMER_NAME,
                command.commandId())) {
            log.info("Duplicate CreateDeliveryCommand ignored for order {}",
                    command.orderId());
            return;
        }
        log.info(
                "Assigning delivery for order {}",
                command.orderId());
        deliveryService.assignDelivery(command);
        idempotencyService.markProcessed(
                CONSUMER_NAME,
                command.commandId());
        log.info("Delivery processing completed for order {}",
                command.orderId());
    }
}
