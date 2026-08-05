package com.example.eventstream.notification.kafka.consumer;

import com.example.eventstream.common.command.SendNotificationCommand;
import com.example.eventstream.common.constants.KafkaTopics;
import com.example.eventstream.notification.service.InboxService;
import com.example.eventstream.notification.service.NotificationService;
import com.example.infrastructure.redis.IdempotencyService;
import jakarta.transaction.Transactional;
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
    private final InboxService inboxService;

    public SendNotificationCommandConsumer(NotificationService notificationService, InboxService inboxService) {
        this.notificationService = notificationService;
        this.inboxService = inboxService;
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
    @Transactional
    public void consume(
            SendNotificationCommand command
    ) {
        inboxService.process(
                command.commandId(),
                SendNotificationCommand.class.getSimpleName(),
                () -> notificationService.sendNotification(command)
        );
    }
}
