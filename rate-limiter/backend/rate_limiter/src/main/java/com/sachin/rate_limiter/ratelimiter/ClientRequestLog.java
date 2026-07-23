package com.sachin.rate_limiter.ratelimiter;

import java.util.ArrayDeque;
import java.util.Deque;
import lombok.Getter;

@Getter
public class ClientRequestLog {
  private final Deque<Long> requestTimestamps = new ArrayDeque<>();
}
