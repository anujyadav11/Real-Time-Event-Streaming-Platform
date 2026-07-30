package com.example.infrastructure.observability.config;

import com.example.infrastructure.messaging.kafka.CorrelationAwareKafkaTemplate;
import com.example.infrastructure.observability.correlation.CorrelationIdFilter;
import com.example.infrastructure.observability.correlation.CorrelationIdRestClientInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestClient;

@Configuration
public class ObservabilityConfiguration {
    @Bean
    public CorrelationIdRestClientInterceptor correlationIdRestClientInterceptor() {
        return new CorrelationIdRestClientInterceptor();
    }
    @Bean
    public FilterRegistrationBean<CorrelationIdFilter> correlationIdFilter() {
        FilterRegistrationBean<CorrelationIdFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CorrelationIdFilter());
        registrationBean.setOrder(1);
        return registrationBean;
    }
    @Bean
    @LoadBalanced
    public RestClient.Builder restClientBuilder(
            CorrelationIdRestClientInterceptor correlationIdRestClientInterceptor) {
        return RestClient.builder()
                .requestInterceptor(correlationIdRestClientInterceptor);
    }
    @Bean
    public <K, V> CorrelationAwareKafkaTemplate<K, V> correlationAwareKafkaTemplate(
            KafkaTemplate<K, V> kafkaTemplate) {

        return new CorrelationAwareKafkaTemplate<>(kafkaTemplate);
    }
}
