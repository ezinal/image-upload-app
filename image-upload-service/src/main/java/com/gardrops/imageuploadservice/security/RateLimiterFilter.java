package com.gardrops.imageuploadservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
public class RateLimiterFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, String> redisTemplate;
    private final int maxRequests;
    private final int windowMinutes;

    public RateLimiterFilter(RedisTemplate<String, String> redisTemplate,
                           @Value("${app.rate-limit.max-requests}") int maxRequests,
                           @Value("${app.rate-limit.window-minutes}") int windowMinutes) {
        this.redisTemplate = redisTemplate;
        this.maxRequests = maxRequests;
        this.windowMinutes = windowMinutes;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String clientIp = getClientIpAddress(request);
        String rateCountKey = "rate_count:" + clientIp;

        try {
            String currentCountStr = redisTemplate.opsForValue().get(rateCountKey);
            int currentReqCount = currentCountStr != null ? Integer.parseInt(currentCountStr) : 0;
            
            if (currentReqCount >= maxRequests) {
                response.setStatus(429); // HTTP 429 : too many requests
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Rate limit exceeded. Maximum " + maxRequests + " requests per " + windowMinutes + " minute(s).\"}");
                return;
            }

            if (currentReqCount == 0) {
                redisTemplate.opsForValue().set(rateCountKey, "1", Duration.ofMinutes(windowMinutes));
            } else {
                redisTemplate.opsForValue().increment(rateCountKey);
            }

            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            logger.warn("Rate limiting failed due to Redis error: " + e.getMessage());
            filterChain.doFilter(request, response);
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
} 