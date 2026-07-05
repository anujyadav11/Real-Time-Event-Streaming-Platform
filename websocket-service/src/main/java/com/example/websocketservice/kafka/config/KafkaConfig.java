package com.example.websocketservice.kafka.config;

import com.example.eventstream.common.constants.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaRetryTopic;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafkaRetryTopic
public class KafkaConfig {
    @Bean
    public NewTopic deliveryStatusUpdatedTopic(){
        return TopicBuilder.name(KafkaTopics.DELIVERY_STATUS_UPDATED)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
