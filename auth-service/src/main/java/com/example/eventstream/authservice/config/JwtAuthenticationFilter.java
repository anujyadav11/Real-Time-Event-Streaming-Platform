package com.example.eventstream.authservice.config;

import com.example.eventstream.authservice.security.CustomUserDetailsService;
import com.example.eventstream.authservice.service.JwtService;
import com.example.eventstream.authservice.metrics.SecurityMetrics;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.jsonwebtoken.JwtException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final SecurityMetrics securityMetrics;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService, SecurityMetrics securityMetrics) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.securityMetrics = securityMetrics;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String username;
        try {
            username = jwtService.extractUsername(token);
        } catch (JwtException | IllegalArgumentException ex) {
            securityMetrics.invalidJwt();
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }

        if (username != null
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails;
            try {
                userDetails = userDetailsService.loadUserByUsername(username);
            } catch (AuthenticationException ex) {
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            boolean valid;
            try {
                valid = jwtService.isTokenValid(token, userDetails);
            } catch (JwtException | IllegalArgumentException ex) {
                securityMetrics.invalidJwt();
                valid = false;
            }
            if (valid) {
                String role = jwtService.getClaims(token).get("role", String.class);

                List<String> permissions = jwtService.getClaims(token)
                        .get("permissions", List.class);

                List<GrantedAuthority> authorities = new ArrayList<>();

                authorities.add(
                        new SimpleGrantedAuthority("ROLE_" + role)
                );

                if (permissions != null) {
                    permissions.forEach(permission ->
                            authorities.add(
                                    new SimpleGrantedAuthority(permission)
                            )
                    );
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                authorities
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
