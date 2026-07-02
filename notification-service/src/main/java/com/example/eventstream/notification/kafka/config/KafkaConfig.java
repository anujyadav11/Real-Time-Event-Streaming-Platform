package com.example.eventstream.notification.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaRetryTopic;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafkaRetryTopic
public class KafkaConfig {
    @Bean
    public NewTopic notificationTopic() {
        return TopicBuilder.name("notification-topic").partitions(3).replicas(1).build();
    }
}
