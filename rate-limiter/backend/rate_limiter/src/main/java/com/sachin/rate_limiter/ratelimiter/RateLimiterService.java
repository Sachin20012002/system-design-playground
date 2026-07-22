package com.sachin.rate_limiter.ratelimiter;

import com.sachin.rate_limiter.config.RateLimitProperties;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {

  private final ConcurrentHashMap<String, RateLimitInfo> store = new ConcurrentHashMap<>();
  private final RateLimitProperties properties;

  public RateLimiterService(RateLimitProperties properties) {
    this.properties = properties;
  }

  public boolean isAllowed(String clientId) {
    long currentTime = System.currentTimeMillis();
    RateLimitInfo info = store.computeIfAbsent(clientId, _ -> new RateLimitInfo(currentTime));

    synchronized (info) {
      long windowEnd = info.getWindowStartTime() + properties.windowSeconds() * 1000;

      if (currentTime >= windowEnd) {
        info.setWindowStartTime(currentTime);
        info.getRequestCount().set(0);
      }

      int currentCount = info.getRequestCount().incrementAndGet();

      return currentCount <= properties.maxRequests();
    }
  }
}
