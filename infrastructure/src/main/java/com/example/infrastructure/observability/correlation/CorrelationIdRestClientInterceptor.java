package com.example.infrastructure.observability.correlation;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Propagates the current Correlation ID
 * to every outgoing REST request.
 */
public class CorrelationIdRestClientInterceptor
        implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution)
            throws IOException {
        String correlationId = CorrelationIdHolder.get();
        if (correlationId != null) {
            request.getHeaders().set(
                    CorrelationIdConstants.CORRELATION_ID_HEADER,
                    correlationId
            );
        }
        return execution.execute(request, body);
    }
}