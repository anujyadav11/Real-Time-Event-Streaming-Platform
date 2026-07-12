package com.example.eventstream.sagaorchestrator.repository;

import com.example.eventstream.sagaorchestrator.entity.SagaInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SagaRepository extends JpaRepository<SagaInstance, UUID> {
    Optional<SagaInstance> findByOrderId(UUID orderId);
    Optional<SagaInstance> findByCorrelationId(String correlationId);
}
