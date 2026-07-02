package com.example.eventstream.order.kafka.consumer;

import com.example.eventstream.common.event.PaymentCompletedEvent;
import com.example.eventstream.order.service.OrderService;
import com.example.infrastructure.redis.IdempotencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
public class PaymentCompletedConsumer {
    private static final Logger log =
            LoggerFactory.getLogger(PaymentCompletedConsumer.class);
    private final OrderService orderService;
    private final IdempotencyService idempotencyService;

    public PaymentCompletedConsumer(OrderService orderService,IdempotencyService idempotencyService) {
        this.orderService = orderService;
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
            topics = "payment-completed",
            groupId = "order-group"
    )
    public void consume(PaymentCompletedEvent event) {
        if(idempotencyService.isProcessed(event.eventId())) {
            log.info("Duplicate event ignored.");
            return;
        }
        orderService.markPaymentCompleted(event);
        idempotencyService.markProcessed(event.eventId());
        log.info("[{}]Received PaymentCompleted event for Order: {}",event.correlationID(),event.orderId());
    }
}
