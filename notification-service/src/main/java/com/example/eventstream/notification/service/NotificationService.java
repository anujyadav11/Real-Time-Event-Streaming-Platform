package com.example.eventstream.notification.service;

import com.example.eventstream.common.event.PaymentCompletedEvent;
import com.example.eventstream.notification.entity.Notification;
import com.example.eventstream.common.enums.NotificationChannel;
import com.example.eventstream.common.enums.NotificationStatus;
import com.example.eventstream.notification.repository.NotificationRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository notificationRepository;
    private final Counter notificationSentTotalEmail;
    private final Counter notificationSentTotalSms;


    public NotificationService(NotificationRepository notificationRepository, MeterRegistry meterRegistry) {
        this.notificationRepository = notificationRepository;
        this.notificationSentTotalEmail = meterRegistry.counter("notifications.sent.total.email");
        this.notificationSentTotalSms = meterRegistry.counter("notifications.sent.total.sms");
    }

    @Transactional
    public void sendNotification(PaymentCompletedEvent event) {
        log.info("Sending EMAIL notification for order {}", event.orderId());

        Notification emailNotification = Notification.builder()
                .orderId(event.orderId())
                .channel(NotificationChannel.EMAIL)
                .status(NotificationStatus.SENT)
                .recipient("customer@example.com")
                .message("Your payment was successful")
                .build();

        notificationRepository.save(emailNotification);
        notificationSentTotalEmail.increment();
        log.info("Email notification sent successfully");
        log.info(" Sending SMS notification sent for order {}", event.orderId());

        Notification smsNotification = Notification.builder()
                .orderId(event.orderId())
                .channel(NotificationChannel.SMS)
                .status(NotificationStatus.SENT)
                .recipient("+91XXXXXXXXXX")
                .message("Payment received successful")
                .build();

        notificationRepository.save(smsNotification);
        notificationSentTotalSms.increment();
        log.info("SMS notification sent successfully");
    }
}
