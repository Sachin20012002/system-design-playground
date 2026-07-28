# Interview Notes

This document captures design decisions, implementation trade-offs, and common interview questions encountered while
building this project.

---

# General

## Why build multiple rate limiting algorithms?

Different algorithms solve different problems.

- Fixed Window is simple but suffers from boundary issues.
- Sliding Window improves accuracy.
- Token Bucket allows controlled bursts.
- Leaky Bucket smooths traffic.
- Redis enables distributed rate limiting.

The objective of this project is to understand the evolution of rate limiting techniques rather than implementing only
the final production solution.

---

## Why use the Strategy Pattern?

Instead of tightly coupling the filter with a single algorithm, all algorithms implement a common `RateLimiter`
interface.

Benefits:

- Open for extension
- Closed for modification
- Runtime algorithm selection
- Easy testing
- Cleaner architecture

---

## Why select the algorithm through configuration?

```properties
rate-limit.algorithm=TOKEN_BUCKET
```

instead of changing Java code.

Benefits:

- No recompilation
- Easier experimentation
- Production-friendly
- Supports environment-specific configuration

---

## Why not use @Primary?

Using a factory bean makes algorithm selection explicit.

The filter depends only on the `RateLimiter` abstraction and remains unaware of concrete implementations.

---

# Fixed Window

## What is the main drawback?

Boundary problem.

Example:

```
Window 1

59.9 sec

★★★★★

Window 2

60.1 sec

★★★★★
```

A client may effectively send 10 requests within a very short duration.

---

# Sliding Window

## Why Sliding Log instead of Sliding Counter?

Sliding Log is easier to understand and provides accurate rate limiting.

Although it uses more memory, it is ideal for learning and demonstration purposes.

---

## Why use Deque?

Old timestamps are removed from the front while new timestamps are added at the end.

Operations are O(1).

---

# Token Bucket

## Why use double instead of int?

Tokens refill continuously.

Example:

```
Refill Rate

1 token/sec

250 ms elapsed

↓

0.25 token earned
```

Fractional tokens improve accuracy.

---

## Why initialize the bucket as full?

A new client should not be rate limited immediately.

Initial state:

```
Tokens = Capacity
```

---

## Why lazy refill?

Instead of running a background scheduler, tokens are replenished only when a request arrives.

Benefits:

- Simpler implementation
- Lower CPU usage
- Scales better

---

## Does Token Bucket queue requests?

No.

It simply decides whether a request can proceed immediately.

If enough tokens exist:

```
Allow
```

Otherwise:

```
HTTP 429
```

---

## When should Token Bucket be used?

Examples:

- Public REST APIs
- Authentication endpoints
- Payment APIs
- API Gateways

It is ideal when occasional bursts are acceptable.

---

# Leaky Bucket

## Why does this implementation look very similar to Token Bucket?

This project implements Leaky Bucket as an HTTP rate limiter.

Instead of maintaining an actual queue, it models queue occupancy using a simple counter (`water`).

This makes the implementation suitable for synchronous REST APIs.

---

## Isn't this almost identical to Token Bucket?

Yes.

In this project both algorithms immediately either:

- allow the request
- reject the request

The internal state differs:

Token Bucket stores:

```
Available Capacity
```

Leaky Bucket stores:

```
Current Occupancy
```

Mathematically they are nearly mirror images.

---

## What is the original Leaky Bucket algorithm?

Originally, Leaky Bucket contains an actual queue.

```
Incoming Requests

↓

Queue

↓

Constant Processing Rate
```

Requests are processed at a fixed rate rather than immediately.

---

## Why wasn't a real queue implemented?

A real queue would delay HTTP responses.

That introduces problems such as:

- Long-lived client connections
- Increased thread usage
- Request timeouts
- Reduced scalability

For REST APIs it is generally preferable to return:

```
HTTP 429 Too Many Requests
```

instead of making clients wait.

---

## Where is the original Leaky Bucket actually used?

Examples:

- Network traffic shaping
- Routers
- Message queues
- Background job processing
- Printer queues

These systems benefit from smoothing outgoing traffic instead of rejecting requests.

---

# Thread Safety

## Why ConcurrentHashMap alone is not sufficient?

ConcurrentHashMap guarantees thread-safe access to the map.

It does not protect mutation of objects stored inside it.

Therefore:

```
ConcurrentHashMap

+

synchronized(bucket)
```

is required.

---

## Why synchronize on each bucket instead of the entire map?

Each client has an independent bucket.

Synchronizing only on that client's bucket allows different clients to proceed concurrently.

---

# Design Decisions

## Why keep state in memory?

The first few versions intentionally focus on algorithm behavior.

Distributed storage is introduced later using Redis.

This keeps each version focused on one new concept.

---

## Why not implement Redis from the beginning?

Introducing Redis immediately would hide the algorithm behind distributed-system complexity.

The project evolves incrementally:

```
Algorithms

↓

Architecture

↓

Distribution

↓

Observability

↓

Performance
```

Each version introduces one major concept.

---

# Future Improvements

- Distributed rate limiting using Redis
- Horizontal scaling
- Docker deployment
- Performance testing
- Prometheus metrics
- Grafana dashboards

---

# Key Takeaways

- Different algorithms optimize different trade-offs.
- Simple implementations are often easier to understand before introducing distributed systems.
- Thread safety is just as important as algorithm correctness.
- Clean architecture makes introducing new algorithms straightforward.
- Production systems usually combine multiple techniques rather than relying on a single algorithm.