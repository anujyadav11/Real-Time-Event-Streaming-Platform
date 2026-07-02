package com.example.eventstream.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log =
            LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(org.springframework.web.server.ServerWebExchange exchange,
                             org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        long start = System.currentTimeMillis();

        log.info("Incoming Request: {} {}",
                exchange.getRequest().getMethod(),
                exchange.getRequest().getURI().getPath());

        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {

                    long duration = System.currentTimeMillis() - start;

                    log.info(
                            "Outgoing Response: {} ({} ms)",
                            exchange.getResponse().getStatusCode(),
                            duration
                    );

                }));
    }
    @Override
    public int getOrder() {
        return -1;
    }
}