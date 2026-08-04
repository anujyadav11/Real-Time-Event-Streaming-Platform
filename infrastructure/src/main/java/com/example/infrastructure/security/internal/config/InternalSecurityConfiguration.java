package com.example.infrastructure.security.internal.config;

import com.example.infrastructure.security.internal.filter.InternalApiKeyFilter;
import com.example.infrastructure.security.internal.properties.InternalSecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(InternalSecurityProperties.class)
public class InternalSecurityConfiguration {
    @Bean
    public InternalApiKeyFilter internalApiKeyFilter(InternalSecurityProperties properties) {
        return new InternalApiKeyFilter(properties);
    }
}
