package com.sachin.rate_limiter.ratelimiter;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class ClientIdentifierResolver {

  private static final String X_FORWARDED_FOR = "X-Forwarded-For";

  public String resolve(HttpServletRequest request) {

    String forwarded = request.getHeader(X_FORWARDED_FOR);

    if (forwarded != null && !forwarded.isBlank()) {
      return forwarded.split(",")[0].trim();
    }

    return request.getRemoteAddr();
  }
}
