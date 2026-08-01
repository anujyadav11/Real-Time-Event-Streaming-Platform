package com.example.infrastructure.audit.aspect;

import com.example.infrastructure.audit.annotation.AuditLog;
import com.example.infrastructure.audit.context.AuditContextExtractor;
import com.example.infrastructure.audit.model.AuditEvent;
import com.example.infrastructure.audit.publisher.AuditPublisher;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditPublisher auditPublisher;
    private final AuditContextExtractor contextExtractor;
    private final Tracer tracer;

    @Value("${spring.application.name}")
    private String serviceName;

    @Around("@annotation(auditLog)")
    public Object audit(
            ProceedingJoinPoint joinPoint,
            AuditLog auditLog
    ) throws Throwable {
        try {
            Object result = joinPoint.proceed();
            publishEvent(auditLog, "SUCCESS");
            return result;
        } catch (Exception ex) {
            publishEvent(auditLog, "FAILED");
            throw ex;
        }
    }
    private void publishEvent(
            AuditLog auditLog,
            String status
    ) {
        AuditEvent event =
                new AuditEvent(
                        Instant.now(),
                        serviceName,
                        null,
                        null,
                        auditLog.action(),
                        auditLog.resource(),
                        null,
                        status,
                        null,
                        contextExtractor.getTraceId(),
                        contextExtractor.getIpAddress()
                );
        auditPublisher.publish(event);
    }
}