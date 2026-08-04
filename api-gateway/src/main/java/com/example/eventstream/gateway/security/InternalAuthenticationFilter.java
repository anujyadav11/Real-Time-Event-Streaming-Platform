package com.example.eventstream.gateway.security;

import com.example.infrastructure.security.internal.constants.SecurityHeaders;
import com.example.infrastructure.security.internal.properties.InternalSecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class InternalAuthenticationFilter
        implements GlobalFilter, Ordered {
    private final InternalSecurityProperties properties;

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        ServerHttpRequest request =
                exchange.getRequest()
                        .mutate()
                        .header(SecurityHeaders.INTERNAL_SERVICE, properties.getServiceName())
                        .header(SecurityHeaders.INTERNAL_API_KEY, properties.getCurrentKey())
                        .header(SecurityHeaders.USER_ID)
                        .header(SecurityHeaders.USERNAME)
                        .header(SecurityHeaders.USER_ROLE)
                        .header(SecurityHeaders.CORRELATION_ID)
                        .build();

        return chain.filter(
                exchange.mutate()
                        .request(request)
                        .build()
        );
    }
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
