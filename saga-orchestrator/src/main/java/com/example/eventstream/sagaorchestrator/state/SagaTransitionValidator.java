package com.example.eventstream.sagaorchestrator.state;

import com.example.eventstream.sagaorchestrator.state.SagaStatus;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

@Component
public class SagaTransitionValidator {
    private static final Map<SagaStatus, EnumSet<SagaStatus>> VALID_TRANSITIONS =
            new EnumMap<>(SagaStatus.class);
    static {
        VALID_TRANSITIONS.put(
                SagaStatus.STARTED,
                EnumSet.of(
                        SagaStatus.INVENTORY_PENDING,
                        SagaStatus.FAILED
                )
        );
        VALID_TRANSITIONS.put(
                SagaStatus.INVENTORY_PENDING,
                EnumSet.of(
                        SagaStatus.INVENTORY_COMPLETED,
                        SagaStatus.INVENTORY_FAILED
                )
        );
        VALID_TRANSITIONS.put(
                SagaStatus.INVENTORY_COMPLETED,
                EnumSet.of(
                        SagaStatus.PAYMENT_PENDING
                )
        );
        VALID_TRANSITIONS.put(
                SagaStatus.PAYMENT_PENDING,
                EnumSet.of(
                        SagaStatus.PAYMENT_COMPLETED,
                        SagaStatus.PAYMENT_FAILED
                )
        );
        VALID_TRANSITIONS.put(
                SagaStatus.PAYMENT_COMPLETED,
                EnumSet.of(
                        SagaStatus.DELIVERY_PENDING
                )
        );
        VALID_TRANSITIONS.put(
                SagaStatus.DELIVERY_PENDING,
                EnumSet.of(
                        SagaStatus.DELIVERY_COMPLETED,
                        SagaStatus.DELIVERY_FAILED
                )
        );
        VALID_TRANSITIONS.put(
                SagaStatus.DELIVERY_COMPLETED,
                EnumSet.of(
                        SagaStatus.NOTIFICATION_PENDING
                )
        );
        VALID_TRANSITIONS.put(
                SagaStatus.NOTIFICATION_PENDING,
                EnumSet.of(
                        SagaStatus.NOTIFICATION_COMPLETED
                )
        );
        VALID_TRANSITIONS.put(
                SagaStatus.NOTIFICATION_COMPLETED,
                EnumSet.of(
                        SagaStatus.COMPLETED
                )
        );
        VALID_TRANSITIONS.put(
                SagaStatus.PAYMENT_FAILED,
                EnumSet.of(
                        SagaStatus.COMPENSATING
                )
        );
        VALID_TRANSITIONS.put(
                SagaStatus.INVENTORY_FAILED,
                EnumSet.of(
                        SagaStatus.FAILED
                )
        );

        VALID_TRANSITIONS.put(
                SagaStatus.COMPENSATING,
                EnumSet.of(
                        SagaStatus.COMPENSATED
                )
        );
    }
    public boolean isValidTransition(
            SagaStatus from,
            SagaStatus to) {
        return VALID_TRANSITIONS
                .getOrDefault(from, EnumSet.noneOf(SagaStatus.class))
                .contains(to);
    }
}