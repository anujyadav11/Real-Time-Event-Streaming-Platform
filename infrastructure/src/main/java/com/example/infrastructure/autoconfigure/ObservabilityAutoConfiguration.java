package com.example.infrastructure.autoconfigure;

import com.example.infrastructure.kafka.CorrelationAwareKafkaTemplate;
import com.example.infrastructure.kafka.CorrelationContextRecordInterceptor;
import com.example.infrastructure.observability.correlation.CorrelationIdFilter;
import com.example.infrastructure.observability.correlation.CorrelationIdRestClientInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.web.client.RestClient;

@AutoConfiguration
public class ObservabilityAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public CorrelationIdRestClientInterceptor correlationIdRestClientInterceptor() {
        return new CorrelationIdRestClientInterceptor();
    }
    @Bean
    @ConditionalOnMissingBean
    public FilterRegistrationBean<CorrelationIdFilter> correlationIdFilter() {
        FilterRegistrationBean<CorrelationIdFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new CorrelationIdFilter());
        filterRegistrationBean.setOrder(1);
        return filterRegistrationBean;
    }
    @Bean
    @LoadBalanced
    @ConditionalOnMissingBean
    public RestClient.Builder restClientBuilder(
            CorrelationIdRestClientInterceptor interceptor
    ) {
        return RestClient.builder()
                .requestInterceptor(interceptor);
    }
    @Bean
    @ConditionalOnMissingBean
    public <K, V>CorrelationAwareKafkaTemplate<K, V> correlationAwareKafkaTemplate(
            org.springframework.kafka.core.KafkaTemplate<K, V> kafkaTemplate){
        return new CorrelationAwareKafkaTemplate<>(kafkaTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public RecordInterceptor<Object, Object> correlationContextRecordInterceptor() {
        return new CorrelationContextRecordInterceptor<>();
    }

    @Bean
    public static BeanPostProcessor correlationKafkaListenerFactoryPostProcessor(
            RecordInterceptor<Object, Object> correlationContextRecordInterceptor) {
        return new BeanPostProcessor() {
            @Override
            @SuppressWarnings({"rawtypes", "unchecked"})
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                if (bean instanceof ConcurrentKafkaListenerContainerFactory factory) {
                    factory.setRecordInterceptor(correlationContextRecordInterceptor);
                }
                return bean;
            }
        };
    }
}
