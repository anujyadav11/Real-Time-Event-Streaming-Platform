package com.example.eventstream.order.kafka.consumer;

import com.example.eventstream.common.event.PaymentCompletedEvent;
import com.example.eventstream.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentCompletedConsumer {
    private static final Logger log =
            LoggerFactory.getLogger(PaymentCompletedConsumer.class);
    private final OrderService orderService;
    public PaymentCompletedConsumer(OrderService orderService) {
        this.orderService = orderService;
    }
    @KafkaListener(
            topics = "payment-completed",
            groupId = "order-group"
    )
    public void consume(PaymentCompletedEvent event) {
        log.info("Received PaymentCompleted event for Order: {}", event.orderId());
        orderService.markPaymentCompleted(event);
    }
}
