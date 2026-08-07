package com.example.eventstream.order.scheduler;

import com.example.eventstream.common.enums.OutBoxStatus;
import com.example.eventstream.order.entity.OutBoxEvent;
import com.example.eventstream.order.repository.OutBoxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRecoveryScheduler {

    private final OutBoxRepository repository;

    @Scheduled(fixedDelayString = "${outbox.recovery.fixed-delay:60000}")
    @Transactional
    public void recoverProcessingEvents() {
        LocalDateTime threshold =
                LocalDateTime.now()
                        .minusMinutes(5);
        List<OutBoxEvent> events =
                repository.findByStatusAndUpdatedAtBefore(
                        OutBoxStatus.PROCESSING,
                        threshold
                );
        for (OutBoxEvent event : events) {
            log.warn(
                    "Recovering stuck outbox event {}",
                    event.getId()
            );
            event.setStatus(
                    OutBoxStatus.NEW
            );
            event.setRetryCount(
                    event.getRetryCount() + 1
            );
        }
    }
}