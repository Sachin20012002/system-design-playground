package com.sachin.rate_limiter.ratelimiter;

public interface RateLimiter {
  boolean allow(String clientId);
}
