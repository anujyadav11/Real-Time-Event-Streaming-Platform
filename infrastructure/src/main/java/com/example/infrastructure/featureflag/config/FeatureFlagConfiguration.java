package com.example.infrastructure.featureflag.config;

import com.example.infrastructure.featureflag.properties.FeatureFlagProperties;
import com.example.infrastructure.featureflag.service.DefaultFeatureFlagService;
import com.example.infrastructure.featureflag.service.FeatureFlagService;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FeatureFlagProperties.class)
public class FeatureFlagConfiguration {

    @Bean
    public FeatureFlagService featureFlagService(
            FeatureFlagProperties properties) {
        return new DefaultFeatureFlagService(properties);
    }
}