package com.example.infrastructure.featureflag.properties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "feature")
public class FeatureFlagProperties {
    /**
     * Example:
     * feature:
     *   flags:
     *     new-payment-flow: true
     *     inventory-v2: false
     */
    private Map<String, Boolean> flags = new HashMap<>();
    public Map<String, Boolean> getFlags() {
        return flags;
    }
    public void setFlags(Map<String, Boolean> flags) {
        this.flags = flags;
    }
}