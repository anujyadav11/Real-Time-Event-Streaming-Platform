package com.example.infrastructure.featureflag.service;

public interface FeatureFlagService {
    boolean isEnabled(String featureName);
}