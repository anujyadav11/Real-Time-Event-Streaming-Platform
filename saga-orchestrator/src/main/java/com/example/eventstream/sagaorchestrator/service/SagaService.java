package com.example.eventstream.sagaorchestrator.service;


import com.example.eventstream.sagaorchestrator.entity.SagaInstance;
import com.example.eventstream.sagaorchestrator.repository.SagaRepository;
import com.example.eventstream.sagaorchestrator.state.SagaStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SagaService {
    private final SagaRepository sagaRepository;
    public SagaService(SagaRepository sagaRepository) {
        this.sagaRepository = sagaRepository;
    }
    @Transactional
    public SagaInstance startSaga(
            UUID orderId,
            String correlationId) {
        SagaInstance saga = SagaInstance.builder()
                .orderId(orderId)
                .correlationId(correlationId)
                .status(SagaStatus.STARTED)
                .build();
        return sagaRepository.save(saga);
    }
    @Transactional(readOnly = true)
    public SagaInstance getSaga(UUID orderId) {
        return sagaRepository.findByOrderId(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Saga not found for order " + orderId));
    }
    @Transactional
    public SagaInstance updateStatus(
            UUID orderId,
            SagaStatus status) {
        SagaInstance saga = getSaga(orderId);
        saga.setStatus(status);
        return sagaRepository.save(saga);
    }
    @Transactional
    public void completeSaga(UUID orderId) {
        updateStatus(orderId, SagaStatus.COMPLETED);
    }
    @Transactional
    public void failSaga(UUID orderId) {
        updateStatus(orderId, SagaStatus.FAILED);
    }
    @Transactional
    public void compensateSaga(UUID orderId) {
        updateStatus(orderId, SagaStatus.COMPENSATED);
    }
}