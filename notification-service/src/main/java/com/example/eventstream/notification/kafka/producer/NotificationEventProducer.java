package com.example.eventstream.notification.kafka.producer;

import com.example.eventstream.common.constants.KafkaTopics;
import com.example.eventstream.common.event.NotificationFailedEvent;
import com.example.eventstream.common.event.NotificationSentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class NotificationEventProducer {
    private static final Logger log = LoggerFactory.getLogger(NotificationEventProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    public NotificationEventProducer(KafkaTemplate<String, Object> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }
    public CompletableFuture<Void> publishSent(NotificationSentEvent event){
        return kafkaTemplate.send(
                KafkaTopics.NOTIFICATION_SENT,
                event.orderId().toString(),
                event)
                .thenAccept(result ->
                        log.info("Notification published for order {} ",event.eventId()));
    }
    public CompletableFuture<Void> publishFailed(NotificationFailedEvent event){
        return kafkaTemplate.send(
                KafkaTopics.NOTIFICATION_FAILED,
                event.orderId().toString(),
                event
        ).thenAccept(result->
                log.info("NotificationFailedEvent published for order {}", event.orderId()));
    }
}
