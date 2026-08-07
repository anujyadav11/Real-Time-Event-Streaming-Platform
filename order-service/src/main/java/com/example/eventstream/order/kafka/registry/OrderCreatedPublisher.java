package com.example.eventstream.order.kafka.registry;

import com.example.eventstream.common.event.OrderCreatedEvent;
import com.example.eventstream.order.entity.OutBoxEvent;
import com.example.eventstream.order.kafka.producer.OrderEventProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCreatedPublisher implements EventPublisher {

    private final ObjectMapper objectMapper;
    private final OrderEventProducer producer;

    @Override
    public String supports() {
        return "ORDER_CREATED";
    }

    @Override
    public void publish(OutBoxEvent event) throws Exception {

        OrderCreatedEvent payload =
                objectMapper.readValue(
                        event.getPayload(),
                        OrderCreatedEvent.class
                );

        producer.publish(payload).join();
    }
}