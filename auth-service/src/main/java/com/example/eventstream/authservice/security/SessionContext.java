package com.example.eventstream.authservice.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class SessionContext {

    private final HttpServletRequest request;

    public SessionContext(HttpServletRequest request) {
        this.request = request;
    }
    public String getIpAddress() {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr() == null ? "unknown" : request.getRemoteAddr();
    }
    public String getUserAgent() {
        String userAgent = request.getHeader("User-Agent");
        return userAgent == null || userAgent.isBlank() ? "Unknown Device" : userAgent;
    }
}
