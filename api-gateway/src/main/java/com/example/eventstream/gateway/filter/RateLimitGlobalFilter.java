package com.example.eventstream.gateway.filter;

import com.example.eventstream.gateway.metrics.GatewayMetrics;
import com.example.eventstream.gateway.util.ErrorResponseWriter;
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
    private final ErrorResponseWriter errorResponseWriter;
    private final GatewayMetrics gatewayMetrics;
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

                    response.getHeaders().forEach((header, value) ->
                            exchange.getResponse().getHeaders().add(header, value)
                    );
                    if (response.isAllowed()) {
                        return chain.filter(exchange);
                    }

                    exchange.getResponse().getHeaders().set("Retry-After", "1");

                    String route =
                            exchange.getRequest()
                                    .getPath()
                                    .value();
                    gatewayMetrics.incrementRejected(
                            route,
                            exchange.getRequest().getMethod().name(),
                            "429"
                    );

                    return errorResponseWriter.writeRateLimitExceeded(exchange);
                });
    }
    @Override
    public int getOrder() {
        return -10;
    }
}
