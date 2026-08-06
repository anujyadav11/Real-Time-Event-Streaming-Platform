package com.example.eventstream.notification.repository;

import com.example.eventstream.common.enums.InboxStatus;
import com.example.eventstream.notification.entity.InboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InboxEventRepository
        extends JpaRepository<InboxEvent, UUID> {
    Optional<InboxEvent> findByEventId(UUID eventId);

    List<InboxEvent> findByStatus(InboxStatus status);

    List<InboxEvent> findByStatusAndReceivedAtBefore(
            InboxStatus status,
            LocalDateTime time
    );
    List<InboxEvent> findAllByStatusOrderByReceivedAtAsc(
            InboxStatus status
    );
    long countByStatus(InboxStatus status);
}
