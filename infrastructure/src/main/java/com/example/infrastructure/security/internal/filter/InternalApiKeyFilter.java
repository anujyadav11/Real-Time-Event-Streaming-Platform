package com.example.infrastructure.security.internal.filter;

import com.example.infrastructure.security.internal.constants.SecurityHeaders;
import com.example.infrastructure.security.internal.context.InternalRequestContext;
import com.example.infrastructure.security.internal.properties.InternalSecurityProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class InternalApiKeyFilter
        extends OncePerRequestFilter {

    private final InternalSecurityProperties properties;
    private final Logger log = LoggerFactory.getLogger(getClass());
    public InternalApiKeyFilter(InternalSecurityProperties properties) {
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();
        // Only protect internal endpoints
        if (!path.startsWith("/internal")) {
            filterChain.doFilter(request, response);
            log.debug(
                    "Validated internal request for {}",
                    request.getRequestURI()
            );
            return;
        }
        String apiKey =
                request.getHeader(
                        SecurityHeaders.INTERNAL_API_KEY);
        if (apiKey == null ||
                !apiKey.equals(properties.getApiKey())) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid Internal API Key"
            );
            log.warn(
                    "Rejected internal request from {}",
                    request.getRemoteAddr()
            );
            return;
        }

        String service = request.getHeader(
                SecurityHeaders.INTERNAL_SERVICE
        );

        InternalRequestContext.setService(service);

        try{
            filterChain.doFilter(request, response);
        }finally {
            InternalRequestContext.clear();
        }
    }
}