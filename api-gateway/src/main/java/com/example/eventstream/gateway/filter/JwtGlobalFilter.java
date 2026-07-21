package com.example.eventstream.gateway.filter;

import com.example.eventstream.common.constants.SecurityHeaders;
import com.example.eventstream.gateway.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtGlobalFilter.class);
    private final JwtService jwtService;

    public JwtGlobalFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        String path = exchange.getRequest()
                .getURI()
                .getPath();

        // Public endpoints
        if (path.startsWith("/auth/")
                || path.startsWith("/actuator")
                || path.startsWith("/ws/")
                || path.equals("/swagger-ui.html")
                || path.startsWith("/swagger-ui/")
                || path.startsWith("/v3/api-docs")
                || path.matches("^/(auth|order|inventory|pricing|delivery)-service/v3/api-docs(?:/.*)?$")) {

            return chain.filter(exchange);
        }
        String authHeader =
                exchange.getRequest()
                        .getHeaders()
                        .getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {
            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        if (!jwtService.isTokenValid(token)) {
            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        String userId = jwtService.extractUserId(token);
        String username = jwtService.extractUsername(token);
        String role = jwtService.extractRole(token);

        exchange.getAttributes().put("username", username);

        log.info("Authenticated user: {}, role: {}, userId: {}", username, role, userId);

        ServerHttpRequest mutatedRequest =
                exchange.getRequest()
                        .mutate()
                        .header(SecurityHeaders.USER_ID, userId)
                        .header(SecurityHeaders.USER_NAME, username)
                        .header(SecurityHeaders.USER_ROLE, role)
                        .build();

        ServerWebExchange mutatedExchange =
                exchange.mutate()
                        .request(mutatedRequest)
                        .build();

        return chain.filter(mutatedExchange);
    }
    @Override
    public int getOrder() {
        return -100;
    }
}
