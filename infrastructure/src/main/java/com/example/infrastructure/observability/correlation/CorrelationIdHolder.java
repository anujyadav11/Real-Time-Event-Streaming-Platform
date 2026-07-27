package com.example.infrastructure.observability.correlation;

import org.slf4j.MDC;
/**
 * Stores the Correlation ID for the current request.
 *
 * The Correlation ID is kept in both:
 * 1. ThreadLocal - for programmatic access
 * 2. MDC - so every log statement automatically includes it
 */
public final class CorrelationIdHolder {
    private static final ThreadLocal<String> CORRELATION_ID =
            new ThreadLocal<>();
    private CorrelationIdHolder() {
        throw new IllegalStateException("Utility class");
    }
    /**
     * Stores the Correlation ID for the current thread.
     */
    public static void set(String correlationId) {
        CORRELATION_ID.set(correlationId);
        MDC.put(CorrelationIdConstants.MDC_KEY, correlationId);
    }
    /**
     * Returns the Correlation ID of the current request.
     */
    public static String get() {
        return CORRELATION_ID.get();
    }
    /**
     * Removes the Correlation ID after request completion.
     * IMPORTANT:
     * Application servers reuse threads.
     * If we don't clear the ThreadLocal,
     * the next request may accidentally inherit
     * the previous Correlation ID.
     */
    public static void clear() {
        CORRELATION_ID.remove();
        MDC.remove(CorrelationIdConstants.MDC_KEY);
    }
}