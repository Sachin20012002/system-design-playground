package com.sachin.rate_limiter.ratelimiter.algorithm;

import com.sachin.rate_limiter.config.RateLimitProperties;
import com.sachin.rate_limiter.ratelimiter.RateLimiter;
import java.time.Duration;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

@Service("redisTokenBucket")
public class RedisTokenBucketRateLimiter implements RateLimiter {

  private static final String KEY_PREFIX = "rate-limit:token-bucket:";

  private final StringRedisTemplate redisTemplate;
  private final RateLimitProperties properties;
  private final DefaultRedisScript<Long> tokenBucketScript;

  public RedisTokenBucketRateLimiter(
      StringRedisTemplate redisTemplate, RateLimitProperties properties) {

    this.redisTemplate = redisTemplate;
    this.properties = properties;

    this.tokenBucketScript = new DefaultRedisScript<>();
    this.tokenBucketScript.setLocation(new ClassPathResource("scripts/token-bucket.lua"));
    this.tokenBucketScript.setResultType(Long.class);
  }

  @Override
  public boolean allow(String clientId) {

    String key = KEY_PREFIX + clientId;
    long now = System.currentTimeMillis();
    long ttlSeconds = calculateTtlSeconds();

    Long result =
        redisTemplate.execute(
            tokenBucketScript,
            List.of(key),
            String.valueOf(properties.bucketCapacity()),
            String.valueOf(properties.bucketRatePerSecond()),
            String.valueOf(now),
            String.valueOf(ttlSeconds));

    return result != null && result == 1L;
  }

  private long calculateTtlSeconds() {

    double refillRate = properties.bucketRatePerSecond();

    if (refillRate <= 0) {
      return Duration.ofHours(1).toSeconds();
    }

    double secondsToRefill = properties.bucketCapacity() / refillRate;

    return Math.max(1L, (long) Math.ceil(secondsToRefill * 2));
  }
}
