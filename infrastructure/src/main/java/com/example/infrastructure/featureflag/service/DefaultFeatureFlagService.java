package com.example.infrastructure.featureflag.service;


import com.example.infrastructure.featureflag.properties.FeatureFlagProperties;

public class DefaultFeatureFlagService
        implements FeatureFlagService {
    private final FeatureFlagProperties properties;
    public DefaultFeatureFlagService(
            FeatureFlagProperties properties) {
        this.properties = properties;
    }
    @Override
    public boolean isEnabled(String featureName) {
        return properties
                .getFlags()
                .getOrDefault(featureName, false);
    }
}