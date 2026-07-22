package com.sachin.rate_limiter.ratelimiter;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.Setter;

@Getter
public class RateLimitInfo {
  private final AtomicInteger requestCount;
  @Setter private volatile long windowStartTime;

  public RateLimitInfo(long windowStartTime) {
    this.windowStartTime = windowStartTime;
    this.requestCount = new AtomicInteger(0);
  }
}
