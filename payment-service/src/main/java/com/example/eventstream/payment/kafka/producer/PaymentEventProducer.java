package com.example.eventstream.payment.kafka.producer;

import com.example.eventstream.common.event.PaymentCompletedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventProducer {
    private final KafkaTemplate<String,PaymentCompletedEvent> kafkaTemplate;

    public PaymentEventProducer(KafkaTemplate<String,PaymentCompletedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void publish(PaymentCompletedEvent event){
        kafkaTemplate.send(
                "payment-completed",
                event.orderId().toString(),
                event
        );
    }
}
