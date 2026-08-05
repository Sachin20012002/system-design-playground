# Stateless Services and Shared State

## Problem

Horizontal scaling works reliably only when any application instance can handle the next request. State stored only in one process breaks that property.

## Model

A stateless application instance does not depend on its local memory for durable or cross-request correctness. Shared state lives in an external system chosen for the required guarantees:

- A relational database for durable business data
- Redis for shared low-latency state, counters, or cache entries
- Object storage for files
- A message broker for asynchronous work

Stateless does not mean the application uses no memory. It may keep temporary objects and safe local caches; correctness must not depend on a request reaching the same instance again.

## Trade-offs

- Adding instances increases application capacity but does not automatically scale shared dependencies.
- External state adds network latency and failure modes.
- In-memory synchronization protects threads in one process, not multiple processes.
- Sticky sessions can hide local-state coupling but reduce flexibility and do not provide durable state.

## Common mistakes

- Keeping counters or sessions only in application memory
- Assuming a thread-safe collection coordinates multiple instances
- Calling an application stateless while correctness depends on local cache contents
- Scaling application instances without measuring database or Redis bottlenecks

## Revision questions

- What makes an application instance stateless?
- Why does Java synchronization not coordinate separate JVMs?
- When is local caching safe in a horizontally scaled service?
- Which shared component becomes the next bottleneck after the application tier scales?

## Seen in this repository

- [URL Shortener scaling](../../url-shortener/backend/url-shortener/docs/11-scaling-strategies.md)
- [Rate Limiter high-level design](../../rate-limiter/backend/rate_limiter/docs/high-level-design.md)
