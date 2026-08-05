# Rate Limiting

## Problem

Rate limiting controls how frequently a client may perform an operation. It protects capacity, limits abuse, and can enforce product quotas.

Rejected HTTP requests normally use status `429 Too Many Requests`. A production API may also return rate-limit headers or `Retry-After`.

## Algorithms

| Algorithm | Model | Strength | Main trade-off |
| --- | --- | --- | --- |
| Fixed Window | Count requests in fixed time buckets | Simple and cheap | Boundary bursts can exceed the intended smooth rate |
| Sliding Log | Store recent accepted timestamps | Precise rolling window | Memory and pruning cost grow with traffic |
| Token Bucket | Tokens refill over time and requests consume them | Allows controlled bursts | Requires careful time and concurrency handling |
| Leaky Bucket | Work leaves a queue or modeled occupancy at a steady rate | Smooth output rate | A true queue adds waiting, capacity, and timeout concerns |

## Identity and placement

A limiter needs a stable key such as account, API key, tenant, route, or trusted client IP. IP-only identity is coarse because users may share NAT addresses and proxy headers can be spoofed unless the proxy chain is trusted.

Placement changes the goal:

- Before authentication protects login and unauthenticated resources.
- After authentication enables user or tenant quotas.
- A gateway can reject traffic before application work begins.

## Local versus distributed state

In-memory state is fast and simple but each instance enforces its own independent limit. A shared store enables a global decision across instances but adds network dependency and requires atomic updates.

## Common mistakes

- Using an untrusted forwarded header as identity
- Assuming per-instance limits form one global limit
- Performing read-modify-write operations without atomicity
- Rate limiting health or metrics endpoints unintentionally
- Treating a simplified occupancy model as a queued Leaky Bucket

## Revision questions

- Why can Fixed Window permit boundary bursts?
- How does Token Bucket permit bursts while enforcing an average rate?
- Why is Sliding Log more memory-intensive?
- What changes when the service runs on multiple instances?

## Seen in this repository

- [Rate Limiter low-level design](../../rate-limiter/backend/rate_limiter/docs/low-level-design.md)
- [Rate Limiter interview notes](../../rate-limiter/backend/rate_limiter/docs/INTERVIEW_NOTES.md)
