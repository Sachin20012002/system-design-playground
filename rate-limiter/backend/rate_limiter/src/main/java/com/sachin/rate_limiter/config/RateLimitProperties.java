package com.sachin.rate_limiter.config;

import com.sachin.rate_limiter.ratelimiter.RateLimitAlgorithm;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rate-limit")
public record RateLimitProperties(
    RateLimitAlgorithm algorithm,
    int maxRequests,
    long windowSeconds,
    double bucketCapacity,
    int bucketRefillCountPerSecond) {}
