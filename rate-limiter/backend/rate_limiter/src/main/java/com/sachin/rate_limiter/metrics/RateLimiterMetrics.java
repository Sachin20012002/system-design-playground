package com.sachin.rate_limiter.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class RateLimiterMetrics {

  private final Counter allowedRequests;

  private final Counter rejectedRequests;

  public RateLimiterMetrics(MeterRegistry meterRegistry) {

    this.allowedRequests =
        Counter.builder("rate_limiter_requests_allowed_total")
            .description("Total allowed requests")
            .register(meterRegistry);

    this.rejectedRequests =
        Counter.builder("rate_limiter_requests_rejected_total")
            .description("Total rejected requests")
            .register(meterRegistry);
  }

  public void incrementAllowed() {
    allowedRequests.increment();
  }

  public void incrementRejected() {
    rejectedRequests.increment();
  }
}
