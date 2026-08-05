# Low-Level Design

> Reusable background: [Rate-limiting algorithms](../../../../backend-handbook/rate-limiting/rate-limiting.md) and [Redis atomicity and Lua](../../../../backend-handbook/redis/atomicity-and-lua.md). This page remains the source for the current Java and Lua implementation.

## Strategy selection

```java
public interface RateLimiter {
  boolean allow(String clientId);
}
```

`RateLimitFilter` depends on this interface. `RateLimiterApplication` creates the selected strategy bean from the type-safe `rate-limit.algorithm` configuration value and qualified implementation beans.

Currently selectable values are:

- `SLIDING_WINDOW`
- `TOKEN_BUCKET`
- `LEAKY_BUCKET`
- `REDIS_TOKEN_BUCKET`

Fixed Window is a historical V1 milestone but is not present in the current enum or strategy implementations.

## Request filter

`RateLimitFilter` extends Spring's `OncePerRequestFilter`, resolves a client ID, and performs admission control before controller execution. It returns HTTP 429 with a JSON body when the selected strategy rejects a request.

`ClientIdentifierResolver` uses the first `X-Forwarded-For` value when present and otherwise uses `request.getRemoteAddr()`. The current Nginx configuration replaces that header with `$remote_addr` for the local Docker topology.

## In-memory algorithms

### Sliding Window using a sliding log

State:

```text
ConcurrentHashMap<String, ClientRequestLog>
ClientRequestLog -> Deque<Long> requestTimestamps
```

For each request, the implementation removes timestamps older than the configured window, rejects when the remaining count reaches `maxRequests`, or appends the current timestamp and allows the request.

### Token Bucket

State:

```text
ConcurrentHashMap<String, ClientTokenBucket>
ClientTokenBucket -> double tokens, long lastRefillTime
```

New buckets start at full capacity. On each request, elapsed time is converted into fractional earned tokens, the total is capped at capacity, and one full token is consumed when available.

### Simplified Leaky Bucket

State:

```text
ConcurrentHashMap<String, ClientLeakyBucket>
ClientLeakyBucket -> double water, long lastLeakTime
```

The implementation subtracts leaked occupancy according to elapsed time and adds one unit for an accepted request. It rejects when adding that unit would exceed capacity. It models occupancy only and does not maintain or drain an actual request queue.

## In-memory thread safety

The maps are `ConcurrentHashMap` instances, but the state objects stored inside them are mutable. Each implementation synchronizes on the individual client's state object while calculating and updating state. Different clients can proceed independently.

This synchronization is local to one JVM and cannot coordinate application instances.

## Redis Token Bucket

### Key and hash structure

Redis key:

```text
rate-limit:token-bucket:<clientId>
```

Hash fields:

```text
tokens
lastRefillTime
```

`tokens` stores the remaining fractional token count. `lastRefillTime` stores epoch time in milliseconds supplied by the application.

### Java-to-Lua mapping

The Java call uses:

```java
redisTemplate.execute(
    tokenBucketScript,
    List.of(key),
    capacity,
    refillRate,
    nowMillis,
    ttlSeconds);
```

Mapping inside the script:

| Java value | Lua value |
| --- | --- |
| `List.of(key)` | `KEYS[1]` |
| bucket capacity | `ARGV[1]` |
| refill rate | `ARGV[2]` |
| current epoch milliseconds | `ARGV[3]` |
| TTL seconds | `ARGV[4]` |

The key list is separate so Redis knows which keys the script accesses. All non-key inputs are arguments.

### Lua operation

The script:

1. Uses `HMGET` to read `tokens` and `lastRefillTime`.
2. Initializes missing state with a full bucket.
3. Calculates elapsed seconds and fractional earned tokens.
4. Caps the bucket at capacity.
5. Consumes one token if available.
6. Uses `HSET` to persist both fields.
7. Uses `EXPIRE` to refresh the key TTL.
8. Returns `1` for allowed or `0` for rejected.

Redis runs this script atomically, providing cross-JVM read-calculate-write consistency without Java synchronization.

### TTL behavior

For a positive refill rate:

```text
TTL = ceil((capacity / refillRate) * 2)
```

The result is at least one second. If the refill rate is zero or negative, Java supplies a one-hour TTL. Every request refreshes the TTL, so inactive client state is removed after the calculated period.

## Metrics

`RateLimiterMetrics` registers two Micrometer counters:

- `rate_limiter_requests_allowed_total`
- `rate_limiter_requests_rejected_total`

The filter increments one counter for each admission decision. Prometheus scrapes these counters independently from both application instances.

## Configuration properties

| Property | Type | Used by |
| --- | --- | --- |
| `rate-limit.algorithm` | enum | Strategy selection |
| `rate-limit.max-requests` | integer | Sliding Window |
| `rate-limit.window-seconds` | long | Sliding Window |
| `rate-limit.bucket-capacity` | double | Token and Leaky Bucket strategies |
| `rate-limit.bucketRatePerSecond` | integer | Token and Leaky Bucket strategies |
