package com.example.infrastructure.observability.correlation;

public final class CorrelationIdConstants {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    public static final String MDC_KEY =  "correlationId";

    private CorrelationIdConstants() {
        throw new IllegalStateException("Utility class");
    }
}