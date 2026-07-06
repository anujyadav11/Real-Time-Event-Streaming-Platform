package com.example.eventstream.delivery.kafka.consumer;

import com.example.eventstream.common.constants.KafkaTopics;
import com.example.eventstream.common.event.PaymentCompletedEvent;
import com.example.eventstream.delivery.service.DeliveryService;
import com.example.infrastructure.redis.IdempotencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
public class PaymentCompletedConsumer {
    private static final String CONSUMER_NAME = "delivery-service";
    private static final Logger log = LoggerFactory.getLogger(PaymentCompletedConsumer.class);
    private final DeliveryService deliveryService;
    private final IdempotencyService idempotencyService;
    public PaymentCompletedConsumer(DeliveryService deliveryService, IdempotencyService idempotencyService){
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
            topics = KafkaTopics.PAYMENT_COMPLETED,
            groupId = "delivery-group"
    )
    public void consume(PaymentCompletedEvent event){
        if(idempotencyService.isProcessed(CONSUMER_NAME, event.eventId())){
            log.info("Duplicate PaymentCompletedEvent ignored for order {}", event.orderId());
            return;
        }
        log.info("Received PaymentCompletedEvent for order : {}", event.orderId());
        deliveryService.createDelivery(event);
        idempotencyService.markProcessed(CONSUMER_NAME, event.eventId());
        log.info("Delivery processing completed for order {}", event.orderId());
    }
    @DltHandler
    public void handleDlt(PaymentCompletedEvent event){
        log.error("Delivery event moved to DLT. OrderId = {}", event.orderId());
    }
}
