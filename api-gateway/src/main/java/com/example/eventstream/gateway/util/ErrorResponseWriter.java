package com.example.eventstream.gateway.util;

import com.example.eventstream.gateway.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ErrorResponseWriter {
    private final ObjectMapper objectMapper;
    public Mono<Void> writeRateLimitExceeded(ServerWebExchange exchange) {
        String correlationId =
                exchange.getRequest()
                        .getHeaders()
                        .getFirst("X-Correlation-Id");
        ErrorResponse response = new ErrorResponse(
                Instant.now(),
                HttpStatus.TOO_MANY_REQUESTS.value(),
                HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(),
                "Rate limit exceeded. Please retry later.",
                exchange.getRequest().getPath().value(),
                correlationId
        );
        try {
            byte[] body = objectMapper.writeValueAsBytes(response);
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return exchange.getResponse()
                    .writeWith(Mono.just(exchange.getResponse()
                            .bufferFactory()
                            .wrap(body)));
        } catch (Exception ex) {
            return Mono.error(ex);
        }
    }
}