# Rate Limiter

A production-inspired **Rate Limiter** built with **Java** and **Spring Boot** as part of my **System Design Playground
**.

The objective of this project is to understand how modern backend systems protect services from excessive traffic using
progressively more advanced rate limiting algorithms and distributed system techniques.

Rather than implementing the most complex solution immediately, the project evolves incrementally. Each version
introduces one new concept while keeping the codebase clean, production-oriented, and well documented.

---

# Project Goals

- Learn common rate limiting algorithms
- Understand algorithm trade-offs and use cases
- Build production-inspired backend services
- Explore distributed rate limiting
- Measure and optimize performance
- Add observability and monitoring
- Document architectural decisions and engineering trade-offs

---

# Roadmap

| Version | Status | Description                               |
|---------|--------|-------------------------------------------|
| V1      | ✅      | Fixed Window Rate Limiter                 |
| V2      | ✅      | Sliding Window (Sliding Log) Rate Limiter |
| V3      | ✅      | Token Bucket Rate Limiter                 |
| V4      | ✅      | Leaky Bucket Rate Limiter                 |
| V5      | ⏳      | Redis Distributed Rate Limiter            |
| V6      | ⏳      | Docker & Horizontal Scaling               |
| V7      | ⏳      | Performance Testing (Grafana k6)          |
| V8      | ⏳      | Observability (Prometheus & Grafana)      |
| V9      | ⏳      | Engineering Documentation                 |

---

# Implemented Algorithms

| Algorithm                      | Description                                            |
|--------------------------------|--------------------------------------------------------|
| ✅ Fixed Window                 | Counter-based rate limiting within a fixed time window |
| ✅ Sliding Window (Sliding Log) | Stores request timestamps for more accurate limiting   |
| ✅ Token Bucket                 | Allows bursts while replenishing capacity over time    |
| ✅ Leaky Bucket                 | Models queue occupancy with a constant leak rate       |

---

# Algorithm Comparison

| Feature                  | Fixed Window | Sliding Window | Token Bucket | Leaky Bucket |
|--------------------------|--------------|----------------|--------------|--------------|
| Burst Handling           | ❌            | Limited        | ✅            | Limited      |
| Accuracy                 | Low          | High           | High         | High         |
| Memory Usage             | Low          | Medium         | Low          | Low          |
| Continuous Refill / Leak | ❌            | ❌              | ✅            | ✅            |
| Distributed Friendly     | ✅            | ✅              | ✅            | ✅            |

---

# Technology Stack

## Backend

- Java 26
- Spring Boot 4

## Planned Technologies

- Redis
- Docker
- Docker Compose
- Nginx
- Grafana k6
- Prometheus
- Grafana

---

# Current Status

**Current Version:** ✅ **V4 – In-Memory Rate Limiting Algorithms**

---

# Features

## Core Features

- Fixed Window Rate Limiter
- Sliding Window (Sliding Log)
- Token Bucket
- Leaky Bucket
- Runtime algorithm selection
- Per-client rate limiting
- HTTP 429 responses when limits are exceeded

## Implementation Highlights

- Strategy Pattern
- Configuration-driven algorithm selection
- Thread-safe implementations
- In-memory storage using `ConcurrentHashMap`
- Per-client synchronization
- Spring `OncePerRequestFilter`
- Support for `X-Forwarded-For`

---

# Architecture

```text
                    Client
                       │
                       ▼
             RateLimitFilter
                       │
                       ▼
                RateLimiter Bean
           (Selected by Configuration)
                       │
      ┌────────────────┼────────────────┐
      │                │                │               │
      ▼                ▼                ▼               ▼
 FixedWindow     SlidingWindow     TokenBucket     LeakyBucket
                       │
                       ▼
                  Controller
                       │
                       ▼
                 Business Logic
```

---

# Configuration

```properties
rate-limit.algorithm=TOKEN_BUCKET
rate-limit.max-requests=5
rate-limit.window-seconds=10
rate-limit.bucket-capacity=5
rate-limit.bucket-refill-count-per-second=1
```

Supported algorithms:

- `FIXED_WINDOW`
- `SLIDING_WINDOW`
- `TOKEN_BUCKET`
- `LEAKY_BUCKET`

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
.
├── docs
│   ├── HLD.md
│   ├── LLD.md
│   ├── PROJECT_NOTES.md
│   └── INTERVIEW_NOTES.md
│
├── src
│
└── README.md
```

---

# Current Limitations

- Single application instance
- In-memory storage
- No distributed coordination
- No persistence across restarts
- No automatic cleanup of inactive clients

These limitations will be addressed in future versions using Redis and distributed deployment.

---

# Upcoming Work

The next milestone introduces a distributed architecture.

**V5** will replace the in-memory storage with **Redis**, enabling multiple application instances to share rate-limiting
state and enforce limits consistently across a distributed environment.

---

# Learning Focus

This project emphasizes:

- Clean Architecture
- Incremental Development
- Strategy Pattern
- Thread Safety
- Backend Engineering
- System Design
- Algorithm Comparisons
- Production-inspired Design

---

# License

This project is intended for learning, experimentation, and backend engineering practice.