package com.example.infrastructure.security.internal.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.internal")
public class InternalSecurityProperties {
    /**
     * Shared secret used for gateway → service communication.
     */
    private String apiKey;
    private String serviceName;
    public String getApiKey() {
        return apiKey;
    }
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    public String getServiceName() {
        return serviceName;
    }
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}