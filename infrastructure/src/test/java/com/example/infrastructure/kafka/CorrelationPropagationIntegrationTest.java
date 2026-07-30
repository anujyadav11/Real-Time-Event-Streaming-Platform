package com.example.infrastructure.kafka;

import com.example.infrastructure.autoconfigure.ObservabilityAutoConfiguration;
import com.example.infrastructure.messaging.kafka.CorrelationAwareKafkaTemplate;
import com.example.infrastructure.messaging.headers.KafkaHeaderUtils;
import com.example.infrastructure.observability.correlation.CorrelationIdHolder;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = CorrelationPropagationIntegrationTest.TestApplication.class,
        properties = {
                "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
                "spring.kafka.consumer.auto-offset-reset=earliest",
                "spring.kafka.consumer.properties.spring.json.trusted.packages=*"
        }
)
@EmbeddedKafka(partitions = 1, topics = CorrelationPropagationIntegrationTest.TOPIC)
class CorrelationPropagationIntegrationTest {

    static final String TOPIC = "correlation-propagation";

    private final CorrelationAwareKafkaTemplate<String, String> kafkaTemplate;
    private final CorrelationProbeListener listener;

    @Autowired
    CorrelationPropagationIntegrationTest(
            CorrelationAwareKafkaTemplate<String, String> kafkaTemplate,
            CorrelationProbeListener listener) {
        this.kafkaTemplate = kafkaTemplate;
        this.listener = listener;
    }

    @AfterEach
    void clearCorrelationContext() {
        CorrelationIdHolder.clear();
    }

    @Test
    void propagatesCorrelationIdFromProducerToKafkaHeaderAndConsumerContext()
            throws Exception {
        String correlationId = "correlation-e2e-123";
        CorrelationIdHolder.set(correlationId);

        kafkaTemplate.send(TOPIC, "order-123", "created").get(10, TimeUnit.SECONDS);

        assertThat(listener.awaitMessage()).isTrue();
        assertThat(listener.correlationIdFromHeader.get()).isEqualTo(correlationId);
        assertThat(listener.correlationIdFromContext.get()).isEqualTo(correlationId);
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import(ObservabilityAutoConfiguration.class)
    static class TestApplication {

        @Bean
        CorrelationProbeListener correlationProbeListener() {
            return new CorrelationProbeListener();
        }
    }

    static class CorrelationProbeListener {

        private final CountDownLatch messageReceived = new CountDownLatch(1);
        private final AtomicReference<String> correlationIdFromHeader = new AtomicReference<>();
        private final AtomicReference<String> correlationIdFromContext = new AtomicReference<>();

        @KafkaListener(topics = TOPIC, groupId = "correlation-propagation-test")
        void consume(ConsumerRecord<String, String> record) {
            correlationIdFromHeader.set(
                    KafkaHeaderUtils.getCorrelationId(record).orElse(null));
            correlationIdFromContext.set(CorrelationIdHolder.get());
            messageReceived.countDown();
        }

        boolean awaitMessage() throws InterruptedException {
            return messageReceived.await(10, TimeUnit.SECONDS);
        }
    }
}
