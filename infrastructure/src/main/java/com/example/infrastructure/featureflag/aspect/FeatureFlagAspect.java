package com.example.infrastructure.featureflag.aspect;

import com.example.infrastructure.featureflag.annotation.FeatureEnabled;
import com.example.infrastructure.featureflag.exception.FeatureDisabledException;
import com.example.infrastructure.featureflag.metrics.FeatureFlagMetrics;
import com.example.infrastructure.featureflag.service.FeatureFlagService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class FeatureFlagAspect {
    private final FeatureFlagService featureFlagService;
    private final FeatureFlagMetrics featureFlagMetrics;
    @Around("@annotation(featureEnabled)")
    public Object validateFeature(
            ProceedingJoinPoint joinPoint,
            FeatureEnabled featureEnabled
    ) throws Throwable {
        String feature = featureEnabled.value();
        if (!featureFlagService.isEnabled(feature)) {
            featureFlagMetrics.recordDisabled(feature);
            throw new FeatureDisabledException(feature);
        }
        featureFlagMetrics.recordEnabled(feature);
        return joinPoint.proceed();
    }
}