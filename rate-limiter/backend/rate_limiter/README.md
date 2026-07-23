# Rate Limiter

A production-inspired Rate Limiter built with **Java** and **Spring Boot** as part of my **System Design Playground**.

The objective of this project is to learn how modern backend systems protect services from excessive traffic using
progressively more advanced rate limiting algorithms and distributed system techniques.

Rather than implementing the most complex solution immediately, the project is developed incrementally. Each version
introduces new concepts while keeping the codebase simple, production-oriented, and well documented.

---

# Project Goals

- Learn common rate limiting algorithms
- Understand distributed system challenges
- Build production-inspired backend services
- Measure and optimize performance
- Add observability and monitoring
- Document architectural decisions and trade-offs

---

# Roadmap

- ✅ V1 – Fixed Window Rate Limiter
- ✅ V2 – Sliding Window Rate Limiter
- ⏳ V3 – Token Bucket Rate Limiter
- ⏳ V4 – Leaky Bucket Rate Limiter
- ⏳ V5 – Redis Distributed Rate Limiter
- ⏳ V6 – Docker & Horizontal Scaling
- ⏳ V7 – Performance Testing (Grafana k6)
- ⏳ V8 – Observability (Prometheus & Grafana)
- ⏳ V9 – Engineering Documentation

---

# Technology Stack

## Backend

- Java 26
- Spring Boot 4

## Future Technologies

- Redis
- Docker
- Docker Compose
- Nginx
- Prometheus
- Grafana
- Grafana k6

---

# Current Status

✅ V2 Completed – Sliding Window Rate Limiter

---

# Current Features

## Core Features

- Sliding Window (Sliding Log) rate limiting
- Client identification using IP address
- Configurable request limits
- HTTP 429 responses when limits are exceeded

## Implementation Details

- In-memory request timestamp storage
- Thread-safe implementation
- Per-client synchronization
- Support for `X-Forwarded-For`
- Implemented using Spring `OncePerRequestFilter`

---

# Current Architecture

```text
Client
   ↓
RateLimitFilter
   ↓
RateLimiterService
   ↓
Controller
   ↓
Business Logic
```

---

# Configuration

```properties
rate-limit.max-requests=5
rate-limit.window-seconds=60
```

---

# Example Response

```json
{
  "message": "Rate limit exceeded"
}
```

---

# Repository Structure

```text
docs/
src/
README.md
```

---

# Current Limitations

- Single application instance only
- In-memory storage
- Higher memory usage due to per-request timestamps
- No cleanup of inactive client entries
- No distributed rate limiting

These limitations will be addressed in future versions.

---

# License

This project is intended for learning, experimentation, and backend engineering practice.