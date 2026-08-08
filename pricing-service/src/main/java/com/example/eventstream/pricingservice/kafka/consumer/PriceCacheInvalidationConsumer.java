package com.example.eventstream.pricingservice.kafka.consumer;

import com.example.eventstream.common.event.PriceUpdatedEvent;
import com.example.eventstream.pricingservice.service.PricingCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceCacheInvalidationConsumer {
    private final PricingCacheService pricingCacheService;
    @KafkaListener(
            topics = "${kafka.topics.price-updated}",
            groupId = "pricing-cache-invalidation"
    )
    public void handle(
            PriceUpdatedEvent event
    ) {
        log.info(
                "Invalidating pricing cache for product {}",
                event.productId()
        );
        pricingCacheService.evict(
                event.productId()
        );
    }
}