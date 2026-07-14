package com.example.eventstream.notification.kafka.consumer;

import com.example.eventstream.common.command.SendNotificationCommand;
import com.example.eventstream.common.constants.KafkaTopics;
import com.example.eventstream.notification.service.NotificationService;
import com.example.infrastructure.redis.IdempotencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
public class SendNotificationCommandConsumer {
    private static final String CONSUMER_NAME = "notification-command";
    private static final Logger log = LoggerFactory.getLogger(SendNotificationCommandConsumer.class);

    private final NotificationService notificationService;
    private final IdempotencyService idempotencyService;

    public SendNotificationCommandConsumer(NotificationService notificationService, IdempotencyService idempotencyService) {
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
            topics = KafkaTopics.SEND_NOTIFICATION_COMMAND,
            groupId = "notification-group"
    )
    public void consume(SendNotificationCommand command) {
        if(idempotencyService.isProcessed(CONSUMER_NAME, command.commandId())){
            log.info("Duplicate SendNotificationCommand ignored for order {}", command.orderId());
            return;
        }
        log.info("Received SendNotificationCommand for order {}", command.orderId());

        notificationService.sendNotification(command);
        idempotencyService.markProcessed(CONSUMER_NAME, command.commandId());

        log.info("Notification processing completed for order {}", command.orderId());
    }

}
