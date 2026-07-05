package com.example.eventstream.payment.kafka.consumer;

import com.example.eventstream.common.constants.KafkaTopics;
import com.example.eventstream.common.event.InventoryReservedEvent;
import com.example.eventstream.payment.service.PaymentService;
import com.example.infrastructure.redis.IdempotencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
public class InventoryReservedConsumer {
    private static final Logger log = LoggerFactory.getLogger(InventoryReservedConsumer.class);
    private final PaymentService paymentService;
    private final IdempotencyService idempotencyService;
    public InventoryReservedConsumer(PaymentService paymentService,IdempotencyService idempotencyService) {
        this.paymentService = paymentService;
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
            groupId = "payment-group"
    )
    public void consume(InventoryReservedEvent event){
        if(idempotencyService.isProcessed(event.eventId())){
            log.info("Duplicate event Ignored");
            return;
        }
        log.info("Received InventoryReservedEvent for Order : {}",event.orderId());
        paymentService.processPayment(event);
        idempotencyService.markProcessed(event.eventId());
    }
}
