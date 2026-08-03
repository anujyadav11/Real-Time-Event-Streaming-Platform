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
        return request.getRemoteAddr();
    }
    public String getUserAgent() {
        return request.getHeader("User-Agent");
    }
}