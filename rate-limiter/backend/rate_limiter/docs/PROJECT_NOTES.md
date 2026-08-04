# Project Notes

Concise implementation decisions recorded as the project evolved. Fixed Window is a historical V1 milestone; it is not present in the current strategy enum or source tree.

## V1 - Fixed Window

- Purpose: protect a service from excessive client traffic by enforcing a configurable admission limit before business logic.
- The historical implementation used a counter per client and a fixed time boundary.
- It was simple and memory efficient, but allowed a burst at the end of one window followed by another at the start of the next.

## V2 - Sliding Window using a sliding log

- Current bean: `@Service("slidingWindow")` on `SlidingWindowRateLimiter`.
- State: `ConcurrentHashMap<String, ClientRequestLog>` with a `Deque<Long>` of accepted timestamps.
- Old timestamps leave from the head; new timestamps enter at the tail. Each timestamp is added and removed once, giving amortized O(1) queue work.
- The sliding log is accurate but uses memory proportional to accepted requests retained in the active window.

## V3 - Token Bucket

- Current bean: `@Service("tokenBucket")` on `TokenRateLimiter`.
- A new client starts with `rate-limit.bucket-capacity` tokens.
- Refill is lazy: elapsed time is calculated only when a request arrives, so no scheduler is required.
- Tokens use `double` because elapsed time can earn fractional tokens. Admission still consumes one full token per request.
- Capacity permits a controlled burst; refill rate governs how quickly admission capacity returns, not how fast accepted business work completes.

## V4 - Simplified Leaky Bucket

- Current bean: `@Service("leakyBucket")` on `LeakyBucketRateLimiter`.
- State records modeled occupancy (`water`) and the last leak time. It leaks occupancy lazily and rejects when adding one request would exceed capacity.
- In this synchronous admission-control form, the mathematics resembles Token Bucket with inverted state.
- It is not a classic queued traffic shaper. Holding HTTP requests would extend connections, occupy request-handling resources, and increase timeout risk.
- Real queued Leaky Bucket behavior fits network shaping, message processing, background jobs, and printer queues better than synchronous request filtering.

## Strategy and filter design

- `RateLimitFilter` extends `OncePerRequestFilter` so IP-based admission happens once per request before controller logic.
- The filter depends on the `RateLimiter` interface, not a concrete algorithm.
- The interface has no Spring stereotype; concrete implementations are service beans.
- `RateLimiterApplication.rateLimiter(...)` selects a bean using the `RateLimitAlgorithm` value from `RateLimitProperties`.
- Internal qualifiers are `slidingWindow`, `tokenBucket`, `leakyBucket`, and `redisTokenBucket`. External selection remains enum-based through `rate-limit.algorithm`.
- `@Primary` was not used because it would choose one fixed preference rather than express configuration-driven selection.

## Concurrency

- `ConcurrentHashMap` makes map operations safe but does not make a mutable `ClientRequestLog`, `ClientTokenBucket`, or `ClientLeakyBucket` update atomic.
- Each in-memory implementation synchronizes on the individual client's state object, allowing different clients to proceed independently.
- Java synchronization coordinates threads only inside one JVM. It cannot protect shared decisions across `app1` and `app2`.

## V5 - Redis Token Bucket

- Current bean: `@Service("redisTokenBucket")` on `RedisTokenBucketRateLimiter`.
- Key: `rate-limit:token-bucket:<clientId>`.
- Redis Hash fields: `tokens` and `lastRefillTime`.
- Java passes `List.of(key)` as `KEYS[1]`; capacity, refill rate, current milliseconds, and TTL become `ARGV[1]` through `ARGV[4]`.
- `src/main/resources/scripts/token-bucket.lua` uses `HMGET`, calculates refill and admission, writes both fields with `HSET`, and refreshes expiry with `EXPIRE`.
- Redis executes the script atomically, giving both application instances one read-calculate-write decision.
- TTL is twice the time required to refill an empty bucket, rounded up and bounded to at least one second. A non-positive rate uses one hour. Each request refreshes expiry.

## V6 - Deployment, observability, and load testing

- Compose services are `redis`, `app1`, `app2`, `nginx`, `prometheus`, and `grafana`.
- Containers reach each other through service-name DNS, not `localhost`.
- Redis and Grafana use named volumes. Nginx and Prometheus configuration files are bind-mounted read-only.
- Maven packaging creates `target/rate_limiter-0.0.1-SNAPSHOT.jar`; Docker build copies it into an image; Compose creates and starts the multi-container topology.
- Micrometer counters are incremented in `RateLimitFilter`, Actuator exposes Prometheus-format metrics, Prometheus scrapes both apps, and Grafana can query Prometheus after manual datasource configuration.
- `k6/rate-limiter-test.js` accepts HTTP 200 and 429 in a custom check and sleeps for one second per iteration. Grafana datasource and dashboard provisioning are not repository features.

## Current limitations

- Fixed Window is historical only.
- In-memory state is neither distributed nor cleaned up when clients become inactive.
- Redis is a single unauthenticated local service without replication or clustering.
- Proxy trust is simplified for the local Docker topology.
- Actuator endpoints are unauthenticated.
- Grafana setup is manual, and Prometheus data is not persisted by a named volume.
- Test coverage does not yet exercise algorithm boundaries, concurrency, Redis Lua behavior, or the complete distributed path.
