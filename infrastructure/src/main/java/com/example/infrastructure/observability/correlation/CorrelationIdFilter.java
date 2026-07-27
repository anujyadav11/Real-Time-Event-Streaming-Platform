package com.example.infrastructure.observability.correlation;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
/**
 * Ensures every incoming HTTP request has a Correlation ID.
 * Responsibilities:
 * - Read incoming Correlation ID
 * - Generate one if absent
 * - Store in ThreadLocal + MDC
 * - Return it in response header
 * - Clean up after request completes
 */
public class CorrelationIdFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
                            @NonNull HttpServletRequest request,
                            @NonNull HttpServletResponse response,
                            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
            String correaltionId = request.getHeader(
                    CorrelationIdConstants.CORRELATION_ID_HEADER
            );
            if(!StringUtils.hasText(correaltionId)) {
                correaltionId = UUID.randomUUID().toString();
            }
            CorrelationIdHolder.set(correaltionId);
            response.setHeader(
                    CorrelationIdConstants.CORRELATION_ID_HEADER,
                    correaltionId
            );
            try{
                filterChain.doFilter(request,response);
            }finally {
                CorrelationIdHolder.clear();
            }
    }
}