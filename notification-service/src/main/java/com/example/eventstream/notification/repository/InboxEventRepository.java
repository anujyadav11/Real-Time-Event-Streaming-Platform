package com.example.eventstream.notification.repository;

import com.example.eventstream.notification.entity.InboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InboxEventRepository
        extends JpaRepository<InboxEvent, UUID> {

    boolean existsByEventId(UUID eventId);

}
