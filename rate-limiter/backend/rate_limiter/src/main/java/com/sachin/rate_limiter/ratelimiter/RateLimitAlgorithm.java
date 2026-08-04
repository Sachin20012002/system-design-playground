package com.sachin.rate_limiter.ratelimiter;

public enum RateLimitAlgorithm {
  SLIDING_WINDOW,
  TOKEN_BUCKET,
  LEAKY_BUCKET,
  REDIS_TOKEN_BUCKET
}
