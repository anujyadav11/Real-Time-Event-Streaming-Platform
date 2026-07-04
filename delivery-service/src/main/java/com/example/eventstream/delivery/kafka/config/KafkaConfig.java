package com.example.eventstream.delivery.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaRetryTopic;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafkaRetryTopic
public class KafkaConfig {
    @Bean
    public NewTopic paymentCompletedTopic(){
        return TopicBuilder
                .name("payment-completed")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
