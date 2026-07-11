package com.example.eventstream.notification.repository;

import com.example.eventstream.notification.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {

}
