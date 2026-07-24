package com.sachin.rate_limiter.ratelimiter.algorithm;

import com.sachin.rate_limiter.config.RateLimitProperties;
import com.sachin.rate_limiter.ratelimiter.ClientRequestLog;
import com.sachin.rate_limiter.ratelimiter.RateLimiter;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class SlidingWindowRateLimiter implements RateLimiter {

  private final ConcurrentHashMap<String, ClientRequestLog> store = new ConcurrentHashMap<>();
  private final RateLimitProperties properties;

  public SlidingWindowRateLimiter(RateLimitProperties properties) {
    this.properties = properties;
  }

  @Override
  public boolean allow(String clientId) {
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
