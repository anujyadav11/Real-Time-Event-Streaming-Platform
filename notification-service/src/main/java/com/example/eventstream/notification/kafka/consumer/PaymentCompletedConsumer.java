package com.example.eventstream.notification.kafka.consumer;

import com.example.eventstream.common.constants.KafkaTopics;
import com.example.eventstream.common.event.PaymentCompletedEvent;
import com.example.eventstream.notification.service.NotificationService;
import com.example.infrastructure.redis.IdempotencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
public class PaymentCompletedConsumer {
    private static final String CONSUMER_NAME = "notification-service";
    private static final Logger log = LoggerFactory.getLogger(PaymentCompletedConsumer.class);

    private final NotificationService notificationService;
    private final IdempotencyService idempotencyService;

    public PaymentCompletedConsumer(NotificationService notificationService, IdempotencyService idempotencyService) {
        this.notificationService = notificationService;
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
            groupId = "notification-group"
    )
    public void consume(PaymentCompletedEvent event) {
        if(idempotencyService.isProcessed(CONSUMER_NAME, event.eventId())){
            log.info("Duplicate PaymentCompletedEvent ignored for order {}", event.orderId());
            return;
        }
        log.info("Received PaymentCompletedEvent for order {}", event.orderId());

        notificationService.sendNotification(event);
        idempotencyService.markProcessed(CONSUMER_NAME, event.eventId());

        log.info("Notification processing completed for order {}", event.orderId());
    }

}
