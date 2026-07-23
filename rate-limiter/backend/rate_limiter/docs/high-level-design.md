# High Level Design (V2)

## Objective

Replace the Fixed Window algorithm with a Sliding Window (Sliding Log) implementation to eliminate the boundary problem
while keeping the overall application architecture unchanged.

---

# Architecture

```text
                 +----------------------+
                 |        Client        |
                 +----------+-----------+
                            |
                            v
                 +----------------------+
                 |   RateLimitFilter    |
                 +----------+-----------+
                            |
                            v
                 +----------------------+
                 | RateLimiterService   |
                 +----------+-----------+
                            |
                            v
          +------------------------------------+
          | ConcurrentHashMap                  |
          |                                    |
          | Client IP -> ClientRequestLog      |
          +------------------------------------+
                            |
                            v
                 +----------------------+
                 |     Controller       |
                 +----------+-----------+
                            |
                            v
                 +----------------------+
                 |    Business Logic    |
                 +----------------------+
```

---

# Request Flow

```text
Incoming Request
        ↓
Resolve Client IP
        ↓
Fetch ClientRequestLog
        ↓
Remove Expired Timestamps
        ↓
Has Request Limit Been Reached?
        ↓
YES ─────────► Return HTTP 429
        │
        NO
        │
        ▼
Store Current Timestamp
        ↓
Forward Request
```

---

# Components

## RateLimitFilter

Intercepts incoming requests before business logic execution and delegates rate limiting.

---

## ClientIdentifierResolver

Identifies clients using:

1. `X-Forwarded-For`
2. `request.getRemoteAddr()`

---

## RateLimiterService

Implements the Sliding Window (Sliding Log) algorithm.

Responsible for:

- Maintaining request history
- Removing expired timestamps
- Checking request limits
- Allowing or rejecting requests

---

## ClientRequestLog

Maintains the request timestamps for an individual client.

---

## Controller

Represents protected application endpoints.