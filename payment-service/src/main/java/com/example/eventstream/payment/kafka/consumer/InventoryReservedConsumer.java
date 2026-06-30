package com.example.eventstream.payment.kafka.consumer;

import com.example.eventstream.common.event.InventoryReservedEvent;
import com.example.eventstream.payment.kafka.producer.PaymentEventProducer;
import com.example.eventstream.payment.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryReservedConsumer {
    private static final Logger log = LoggerFactory.getLogger(InventoryReservedConsumer.class);
    private final PaymentService paymentService;
    public InventoryReservedConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(
            topics = "inventory-reserved",
            groupId = "payment-group"
    )
    public void consume(InventoryReservedEvent event){
        log.info("Received InventoryReservedEvent for Order : {}",event.orderId());
        paymentService.processPayment(event);
    }
}
