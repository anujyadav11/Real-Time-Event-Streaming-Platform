package com.example.eventstream.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "inbox")
public class InboxProperties {

    private Duration processingTimeout =
            Duration.ofMinutes(5);

    private Duration schedulerDelay =
            Duration.ofMinutes(1);

    // getters/setters
    public Duration getProcessingTimeout() {
        return processingTimeout;
    }
    public void setProcessingTimeout(Duration processingTimeout) {
        this.processingTimeout = processingTimeout;
    }
    public Duration getSchedulerDelay() {
        return schedulerDelay;
    }
    public void setSchedulerDelay(Duration schedulerDelay) {
        this.schedulerDelay = schedulerDelay;
    }
}