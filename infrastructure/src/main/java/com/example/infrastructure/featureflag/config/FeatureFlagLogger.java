package com.example.infrastructure.featureflag.config;

import com.example.infrastructure.featureflag.properties.FeatureFlagProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FeatureFlagLogger {
    private final FeatureFlagProperties properties;
    @Bean
    ApplicationRunner logFeatureFlags() {
        return args ->
                properties.getFlags()
                        .forEach((k, v) ->
                                log.info("Feature Flag [{}] = {}", k, v));
    }
}