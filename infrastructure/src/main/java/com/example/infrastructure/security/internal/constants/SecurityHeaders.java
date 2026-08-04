package com.example.infrastructure.security.internal.constants;

public final class SecurityHeaders {

    private SecurityHeaders() {}
    public static final String INTERNAL_SERVICE ="X-Internal-Service";
    public static final String INTERNAL_API_KEY = "X-Internal-Api-Key";
    public static final String USER_ID = "X-User-Id";
    public static final String USERNAME = "X-User-Name";
    public static final String USER_ROLE = "X-User-Role";
    public static final String CORRELATION_ID = "X-Correlation-Id";
}