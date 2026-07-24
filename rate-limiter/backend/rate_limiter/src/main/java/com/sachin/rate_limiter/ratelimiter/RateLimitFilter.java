package com.sachin.rate_limiter.ratelimiter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

  private final RateLimiter rateLimiter;
  private final ClientIdentifierResolver resolver;

  public RateLimitFilter(RateLimiter rateLimiter, ClientIdentifierResolver resolver) {
    this.rateLimiter = rateLimiter;
    this.resolver = resolver;
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String clientId = resolver.resolve(request);

    if (!rateLimiter.allow(clientId)) {

      response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());

      response.setContentType("application/json");

      response
          .getWriter()
          .write(
              """
                    {
                      "message": "Rate limit exceeded"
                    }
                    """);

      return;
    }

    filterChain.doFilter(request, response);
  }
}
