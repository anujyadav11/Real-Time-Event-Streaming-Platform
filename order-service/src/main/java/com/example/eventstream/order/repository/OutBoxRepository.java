package com.example.eventstream.order.repository;

import com.example.eventstream.common.enums.OutBoxStatus;
import com.example.eventstream.order.entity.OutBoxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutBoxRepository extends JpaRepository<OutBoxEvent, UUID> {
    List<OutBoxEvent> findByStatusOrderByCreatedAtAsc(OutBoxStatus status);
}