package com.example.eventstream.notification.scheduler;

import com.example.eventstream.notification.service.InboxService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InboxRecoveryScheduler {
    private final Logger log = LoggerFactory.getLogger(InboxRecoveryScheduler.class);
    private final InboxService inboxService;

    @Scheduled(fixedDelay = 60000)
    public void recovery() {
        inboxService.recoverStuckEvents();
    }
}
