package com.gardrops.imageprocessingservice.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class AllowedOriginFilter extends OncePerRequestFilter {
    private final List<String> allowedOrigins;

    public AllowedOriginFilter(@Value("${app.allowed-origins}") List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String remoteAddr = request.getRemoteAddr();

        if (allowedOrigins.contains(remoteAddr)) {
            chain.doFilter(request, response);
        } else {
            ((HttpServletResponse) response).sendError(
                    HttpServletResponse.SC_FORBIDDEN, "Access denied: only accessible from allowed origins");
        }
    }
}

