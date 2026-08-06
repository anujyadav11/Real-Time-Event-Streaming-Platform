package com.example.eventstream.notification.service;

import com.example.eventstream.notification.dto.ReplayRequest;
import com.example.eventstream.notification.kafka.producer.ReplayProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReplayService {
    private final ReplayProducer replayProducer;

    public void replay(ReplayRequest request) {
        //TODO : implement retry
        throw new UnsupportedOperationException("Replay storage not implemented yet");
    }
}
