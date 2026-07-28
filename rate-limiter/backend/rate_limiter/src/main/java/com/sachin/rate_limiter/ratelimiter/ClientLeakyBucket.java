package com.sachin.rate_limiter.ratelimiter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClientLeakyBucket {
  private double water;
  private long lastLeakTime;
}
