# High Level Design (V1)

## Architecture

```text
Client
   ↓
RateLimitFilter
   ↓
Controller
   ↓
Business Logic
```

---

## Request Flow

```text
Request arrives
        ↓
Resolve client IP
        ↓
Check request count
        ↓
Window expired ?
        ↓
YES → Reset counter
NO  → Continue
        ↓
Request count exceeded ?
        ↓
YES → Return HTTP 429
NO  → Forward request
```

---

## Components

### RateLimitFilter

Responsible for intercepting incoming requests and applying rate limiting.

### ClientIdentifierResolver

Responsible for identifying clients using:

1. X-Forwarded-For
2. request.getRemoteAddr()

### RateLimiterService

Responsible for implementing the Fixed Window algorithm.

### Controller

Represents protected business endpoints.