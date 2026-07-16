package com.example.eventstream.notification.kafka.config;

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
    public NewTopic sendNotificationCommandTopic() {
        return topic(KafkaTopics.SEND_NOTIFICATION_COMMAND);
    }

    @Bean
    public NewTopic notificationSentTopic() {
        return topic(KafkaTopics.NOTIFICATION_SENT);
    }

    @Bean
    public NewTopic notificationFailedTopic() {
        return topic(KafkaTopics.NOTIFICATION_FAILED);
    }

    private NewTopic topic(String name) {
        return TopicBuilder.name(name)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
