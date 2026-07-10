package com.example.eventstream.order.service;

import com.example.eventstream.order.entity.OutBoxEvent;
import com.example.eventstream.order.repository.OutBoxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OutboxService {
    private final OutBoxRepository outBoxRepository;
    private final ObjectMapper objectMapper;

    public OutboxService(OutBoxRepository outBoxRepository, ObjectMapper objectMapper){
        this.outBoxRepository = outBoxRepository;
        this.objectMapper = objectMapper;
    }

    public void saveEvent(
            UUID aggregateId,
            String aggregateType,
            String eventType,
            Object payload ){
        try{
            OutBoxEvent event = new OutBoxEvent();
            event.setAggregateId(aggregateId);
            event.setAggregateType(aggregateType);
            event.setEventType(eventType);
            event.setPayload(objectMapper.writeValueAsString(payload));

            outBoxRepository.save(event);
        }catch(JsonProcessingException ex){
            throw new RuntimeException("Failed to serialize Outbox Event", ex);
        }
    }
}
