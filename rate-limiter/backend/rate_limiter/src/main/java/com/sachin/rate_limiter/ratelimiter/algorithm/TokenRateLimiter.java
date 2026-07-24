package com.sachin.rate_limiter.ratelimiter.algorithm;

import com.sachin.rate_limiter.config.RateLimitProperties;
import com.sachin.rate_limiter.ratelimiter.ClientTokenBucket;
import com.sachin.rate_limiter.ratelimiter.RateLimiter;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service("tokenBucket")
public class TokenRateLimiter implements RateLimiter {

  private final ConcurrentHashMap<String, ClientTokenBucket> clientTokenBuckets =
      new ConcurrentHashMap<>();

  private final RateLimitProperties properties;

  public TokenRateLimiter(RateLimitProperties properties) {
    this.properties = properties;
  }

  @Override
  public boolean allow(String clientId) {

    ClientTokenBucket bucket =
        clientTokenBuckets.computeIfAbsent(
            clientId,
            _ -> new ClientTokenBucket(properties.bucketCapacity(), System.currentTimeMillis()));

    synchronized (bucket) {
      long now = System.currentTimeMillis();

      double elapsedSeconds = (now - bucket.getLastRefillTime()) / 1000.0;

      double earnedTokens = elapsedSeconds * properties.bucketRefillCountPerSecond();

      double updatedTokens =
          Math.min(properties.bucketCapacity(), bucket.getTokens() + earnedTokens);

      bucket.setTokens(updatedTokens);
      bucket.setLastRefillTime(now);

      if (bucket.getTokens() >= 1.0) {
        bucket.setTokens(bucket.getTokens() - 1.0);
        return true;
      }

      return false;
    }
  }
}
