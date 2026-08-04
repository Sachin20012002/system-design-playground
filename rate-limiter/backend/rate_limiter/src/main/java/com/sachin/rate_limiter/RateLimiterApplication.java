package com.sachin.rate_limiter;

import com.sachin.rate_limiter.config.RateLimitProperties;
import com.sachin.rate_limiter.ratelimiter.RateLimiter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@ConfigurationPropertiesScan
public class RateLimiterApplication {

  static void main(String[] args) {
    SpringApplication.run(RateLimiterApplication.class, args);
  }

  @Bean
  public RateLimiter rateLimiter(
      RateLimitProperties properties,
      @Qualifier("slidingWindow") RateLimiter slidingWindow,
      @Qualifier("tokenBucket") RateLimiter tokenBucket,
      @Qualifier("leakyBucket") RateLimiter leakyBucket,
      @Qualifier("redisTokenBucket") RateLimiter redisTokenBucket) {

    return switch (properties.algorithm()) {
      case SLIDING_WINDOW -> slidingWindow;
      case TOKEN_BUCKET -> tokenBucket;
      case LEAKY_BUCKET -> leakyBucket;
      case REDIS_TOKEN_BUCKET -> redisTokenBucket;
    };
  }
}
