package com.example.eventstream.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JwtKeyResolver implements KeyResolver {
    @Override
    public Mono<String> resolve(org.springframework.web.server.ServerWebExchange exchange) {
        String username = exchange.getAttribute("username");
        if (username != null && !username.isBlank()) {
            return Mono.just("user:" + username);
        }
        return Mono.just("ip:" + getClientIp(exchange));
    }
    private String getClientIp(org.springframework.web.server.ServerWebExchange exchange) {
        String forwarded =
                exchange.getRequest()
                        .getHeaders()
                        .getFirst("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        if (exchange.getRequest().getRemoteAddress() != null) {
            return exchange.getRequest()
                    .getRemoteAddress()
                    .getAddress()
                    .getHostAddress();
        }
        return "unknown";
    }
}
