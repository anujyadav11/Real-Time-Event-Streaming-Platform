package com.example.eventstream.sagaorchestrator.kafka.consumer;

import com.example.eventstream.common.constants.KafkaTopics;
import com.example.eventstream.common.event.NotificationSentEvent;
import com.example.eventstream.sagaorchestrator.service.SagaService;
import com.example.eventstream.sagaorchestrator.state.SagaStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationSentConsumer {
    private final SagaService sagaService;
    public NotificationSentConsumer(SagaService sagaService){
        this.sagaService = sagaService;
    }
    @KafkaListener(
            topics = KafkaTopics.NOTIFICATION_SENT,
            groupId = "saga-group"
    )
    public void consume(NotificationSentEvent event){
        sagaService.updateStatus(
                event.orderId(),
                SagaStatus.NOTIFICATION_COMPLETED
        );
        sagaService.compensateSaga(event.orderId());
    }
}
