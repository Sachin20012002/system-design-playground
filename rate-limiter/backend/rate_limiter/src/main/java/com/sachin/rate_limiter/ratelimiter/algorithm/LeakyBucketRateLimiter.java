package com.sachin.rate_limiter.ratelimiter.algorithm;

import com.sachin.rate_limiter.config.RateLimitProperties;
import com.sachin.rate_limiter.ratelimiter.ClientLeakyBucket;
import com.sachin.rate_limiter.ratelimiter.RateLimiter;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service("leakyBucket")
public class LeakyBucketRateLimiter implements RateLimiter {

  private final ConcurrentHashMap<String, ClientLeakyBucket> clientBuckets =
      new ConcurrentHashMap<>();

  private final RateLimitProperties properties;

  public LeakyBucketRateLimiter(RateLimitProperties properties) {
    this.properties = properties;
  }

  @Override
  public boolean allow(String clientId) {

    ClientLeakyBucket bucket =
        clientBuckets.computeIfAbsent(
            clientId, _ -> new ClientLeakyBucket(0.0, System.currentTimeMillis()));

    synchronized (bucket) {
      long now = System.currentTimeMillis();

      double elapsedSeconds = (now - bucket.getLastLeakTime()) / 1000.0;

      double leakedWater = elapsedSeconds * properties.bucketRatePerSecond();

      double updatedWater = Math.max(0.0, bucket.getWater() - leakedWater);

      bucket.setWater(updatedWater);
      bucket.setLastLeakTime(now);

      if (bucket.getWater() + 1 > properties.bucketCapacity()) {
        return false;
      }

      bucket.setWater(bucket.getWater() + 1);

      return true;
    }
  }
}
