# Redis Atomicity and Lua

## Problem

Many decisions require a read, calculation, and write. Sending those as separate Redis commands allows another client to modify the state between steps.

## Atomic building blocks

- Single Redis commands such as `INCR` are atomic.
- Transactions group commands, but calculations based on earlier results may still need optimistic locking with `WATCH`.
- Lua scripts execute server-side as one uninterrupted operation.

Lua is useful when a decision must read state, calculate a result, update multiple fields, refresh expiry, and return a value atomically.

## Script inputs

- `KEYS` contains Redis keys accessed by the script.
- `ARGV` contains other arguments such as limits, timestamps, and durations.

Keeping keys in `KEYS` lets Redis understand which keys the script touches, which matters for clustered deployments.

## Trade-offs

- Scripts remove network round trips and race windows.
- Long-running scripts block other Redis work, so scripts should remain small and deterministic.
- Script behavior needs focused tests because failures occur at the data-store boundary.
- Atomic execution on one Redis primary does not make the overall system highly available.

## Revision questions

- Why is a thread-safe Java map insufficient across JVMs?
- When is one atomic Redis command enough?
- Why might `MULTI/EXEC` need `WATCH`?
- Why should Redis keys be passed through `KEYS` rather than ordinary arguments?

## Seen in this repository

- [URL Shortener distributed ID generation](../../url-shortener/backend/url-shortener/docs/07-redis-caching.md)
- [Rate Limiter Redis Token Bucket](../../rate-limiter/backend/rate_limiter/docs/low-level-design.md#redis-token-bucket)
