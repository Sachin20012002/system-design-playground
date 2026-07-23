package com.sachin.rate_limiter.ratelimiter;

import com.sachin.rate_limiter.config.RateLimitProperties;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {

  private final ConcurrentHashMap<String, ClientRequestLog> store = new ConcurrentHashMap<>();
  private final RateLimitProperties properties;

  public RateLimiterService(RateLimitProperties properties) {
    this.properties = properties;
  }

  public boolean isAllowed(String clientId) {
    ClientRequestLog clientRequestLog =
        store.computeIfAbsent(clientId, _ -> new ClientRequestLog());
    synchronized (clientRequestLog) {
      long now = System.currentTimeMillis();
      long windowStart = now - properties.windowSeconds() * 1000;
      Deque<Long> requestTimestamps = clientRequestLog.getRequestTimestamps();
      while (!requestTimestamps.isEmpty() && requestTimestamps.peekFirst() < windowStart) {
        requestTimestamps.removeFirst();
      }
      if (requestTimestamps.size() >= properties.maxRequests()) {
        return false;
      }
      requestTimestamps.addLast(now);
      return true;
    }
  }
}
