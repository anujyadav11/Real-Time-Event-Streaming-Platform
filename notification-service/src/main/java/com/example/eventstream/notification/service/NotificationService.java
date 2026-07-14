package com.example.eventstream.notification.service;

import com.example.eventstream.common.command.SendNotificationCommand;
import com.example.eventstream.common.event.NotificationSentEvent;
import com.example.eventstream.notification.entity.Notification;
import com.example.eventstream.common.enums.NotificationChannel;
import com.example.eventstream.common.enums.NotificationStatus;
import com.example.eventstream.notification.kafka.producer.NotificationEventProducer;
import com.example.eventstream.notification.repository.NotificationRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class NotificationService {
    private static final Logger log =
            LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository notificationRepository;
    private final NotificationEventProducer notificationEventProducer;
    private final Counter notificationSentTotalEmail;
    private final Counter notificationSentTotalSms;
    public NotificationService(
            NotificationRepository notificationRepository,
            NotificationEventProducer notificationEventProducer,
            MeterRegistry meterRegistry) {
        this.notificationRepository = notificationRepository;
        this.notificationEventProducer = notificationEventProducer;
        this.notificationSentTotalEmail =
                meterRegistry.counter("notifications.sent.total.email");
        this.notificationSentTotalSms =
                meterRegistry.counter("notifications.sent.total.sms");
    }
    @Transactional
    public void sendNotification(SendNotificationCommand command) {
        log.info("Sending EMAIL notification for order {}",
                command.orderId());
        Notification emailNotification = Notification.builder()
                .orderId(command.orderId())
                .channel(NotificationChannel.EMAIL)
                .status(NotificationStatus.SENT)
                .recipient("customer@example.com")
                .message("Your order has been confirmed and is being processed.")
                .build();
        notificationRepository.save(emailNotification);
        notificationSentTotalEmail.increment();
        log.info("Email notification sent successfully");
        Notification smsNotification = Notification.builder()
                .orderId(command.orderId())
                .channel(NotificationChannel.SMS)
                .status(NotificationStatus.SENT)
                .recipient("+91XXXXXXXXXX")
                .message("Your order has been confirmed.")
                .build();
        notificationRepository.save(smsNotification);
        notificationSentTotalSms.increment();
        log.info("SMS notification sent successfully");
        NotificationSentEvent event =
                new NotificationSentEvent(
                        UUID.randomUUID(),
                        command.orderId(),
                        "Notification sent successfully",
                        LocalDateTime.now(),
                        command.correlationId()
                );
        notificationEventProducer
                .publishSent(event)
                .join();
        log.info(
                "NotificationSentEvent published for order {}",
                command.orderId());
    }
}
