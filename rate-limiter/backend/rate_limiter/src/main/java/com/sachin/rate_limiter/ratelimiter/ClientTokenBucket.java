package com.sachin.rate_limiter.ratelimiter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ClientTokenBucket {
  double tokens;
  long lastRefillTime;
}
