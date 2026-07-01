package com.example.eventstream.order.kafka.consumer;

import com.example.eventstream.common.event.PaymentCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentCompletedDLTConsumer {
    private static final Logger log = LoggerFactory.getLogger(PaymentCompletedDLTConsumer.class);
    @KafkaListener(
            topics = "payment-completed-dlt",
            groupId = "order-group"
    )
    public void consume(PaymentCompletedEvent event){
        log.error(
                "DLT Message -> OrderId: {}, Amount: {}",
                event.orderId(),
                event.amount()
        );
    }
}
