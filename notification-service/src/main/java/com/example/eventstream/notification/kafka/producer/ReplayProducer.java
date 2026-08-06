package com.example.eventstream.notification.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReplayProducer {
    private final KafkaTemplate<String,Object> kafkaTemplate;
    public void replay(
            String topic,
            Object event
    ){
        kafkaTemplate.send(
                topic,
                event
        );
    }
}