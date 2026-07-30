package com.example.eventstream.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
@ConfigurationProperties(prefix = "gateway.rate-limit")
public class RateLimitProperties {
    /**
     * Number of tokens added every second.
     */
    private int replenishRate = 10;
    /**
     * Maximum bucket capacity.
     */
    private int burstCapacity = 20;
    /**
     * Number of tokens consumed per request.
     */
    private int requestedTokens = 1;
    public int getReplenishRate() {
        return replenishRate;
    }
    public void setReplenishRate(int replenishRate) {
        this.replenishRate = replenishRate;
    }
    public int getBurstCapacity() {
        return burstCapacity;
    }
    public void setBurstCapacity(int burstCapacity) {
        this.burstCapacity = burstCapacity;
    }
    public int getRequestedTokens() {
        return requestedTokens;
    }
    public void setRequestedTokens(int requestedTokens) {
        this.requestedTokens = requestedTokens;
    }
}