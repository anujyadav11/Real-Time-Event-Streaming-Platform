package com.example.infrastructure.audit.pipeline;

import com.example.infrastructure.audit.enricher.AuditEnricher;
import com.example.infrastructure.audit.metrics.AuditMetrics;
import com.example.infrastructure.audit.model.AuditEvent;
import com.example.infrastructure.audit.storage.AuditStorage;
import com.example.infrastructure.audit.validator.AuditValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditPipeline {

    private final AuditValidator validator;
    private final AuditEnricher enricher;
    private final AuditStorage storage;
    private final AuditMetrics metrics;
    public void process(AuditEvent event) {
        validator.validate(event);
        AuditEvent enriched =
                enricher.enrich(event);
        storage.save(enriched);
        metrics.recordSuccess();
    }
}