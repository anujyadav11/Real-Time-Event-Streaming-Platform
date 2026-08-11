package com.example.infrastructure.security.internal.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@ConfigurationProperties(prefix = "internal.security")
@RefreshScope
public class InternalSecurityProperties {
    /**
     * Shared secret used for gateway → service communication.
     */
    private String currentKey;

    public String getCurrentKey() {
        return currentKey;
    }
    public void setCurrentKey(String currentKey) {
        this.currentKey = currentKey;
    }

    private String previousKey;

    public String getPreviousKey() {
        return previousKey;
    }
    public void setPreviousKey(String previousKey) {
        this.previousKey = previousKey;
    }

    private String serviceName;

    public String getServiceName() {
        return serviceName;
    }
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}