package com.example.infrastructure.audit.context;

import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class AuditContextExtractor {
    private final Tracer tracer;
    public AuditContextExtractor(Tracer tracer) {
        this.tracer = tracer;
    }
    public String getTraceId() {
        if (tracer.currentSpan() == null) {
            return null;
        }
        return tracer.currentSpan().context().traceId();
    }
    public String getIpAddress() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes)
                        RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        return request.getRemoteAddr();
    }
}