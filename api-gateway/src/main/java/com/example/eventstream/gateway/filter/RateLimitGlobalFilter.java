package com.example.eventstream.gateway.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RateLimitGlobalFilter implements GlobalFilter, Ordered {
    private final RedisRateLimiter redisRateLimiter;
    private final KeyResolver keyResolver;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain){
        return keyResolver.resolve(exchange)
                .flatMap(key ->
                        redisRateLimiter.isAllowed(
                                exchange.getRequest().getPath().value(),
                                key
                        ))
                .flatMap(response -> {
                    if(response.isAllowed()){
                        return chain.filter(exchange);
                    }
                    exchange.getResponse()
                            .setStatusCode(HttpStatus.TOO_MANY_REQUESTS);

                    return exchange.getResponse().setComplete();
                });
    }
    @Override
    public int getOrder() {
        return -10;
    }
}
