package com.example.eventstream.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JwtKeyResolver implements KeyResolver {
    @Override
    public Mono<String> resolve(org.springframework.web.server.ServerWebExchange exchange) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getName)
                .switchIfEmpty(
                        Mono.just(getClientIp(exchange))
                );
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