package com.sachin.rate_limiter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rate-limit")
public record RateLimitProperties(int maxRequests, long windowSeconds) {}
