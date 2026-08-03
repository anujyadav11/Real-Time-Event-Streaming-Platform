package com.example.infrastructure.audit.context;

import io.micrometer.tracing.Tracer;
import org.springframework.beans.factory.ObjectProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class AuditContextExtractor {
    private final ObjectProvider<Tracer> tracerProvider;
    public AuditContextExtractor(ObjectProvider<Tracer> tracerProvider) {
        this.tracerProvider = tracerProvider;
    }
    public String getTraceId() {
        Tracer tracer = tracerProvider.getIfAvailable();
        if (tracer == null || tracer.currentSpan() == null) {
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
