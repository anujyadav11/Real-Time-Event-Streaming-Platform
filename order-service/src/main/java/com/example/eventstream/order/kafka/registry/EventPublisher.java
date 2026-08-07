package com.example.eventstream.order.kafka.registry;

import com.example.eventstream.order.entity.OutBoxEvent;

public interface EventPublisher {
    String supports();
    void publish(OutBoxEvent event) throws Exception;
}
