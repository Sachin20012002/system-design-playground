# Redis

## Mental model

Redis is an in-memory data-structure server. Applications use it when low-latency shared state or atomic operations matter more than relational querying.

Common uses include caching, sessions, distributed counters, rate limiting, leaderboards, streams, and Pub/Sub.

## Core data structures

- Strings for values and counters
- Hashes for grouped fields
- Lists for ordered queue-like data
- Sets and sorted sets for membership and ranking
- Streams for append-only event processing

Choose a structure from the access pattern and required atomic operation, not merely from the shape of an application object.

## Durability and availability

Redis can persist data through snapshots, an append-only file, or both. Persistence reduces data-loss risk but does not make one Redis instance highly available. Replication, failover, backups, and tested recovery address different failure modes.

## Trade-offs

- Very low latency, but memory is more expensive than disk-oriented storage.
- Rich atomic commands simplify coordination, but multi-step decisions still need transactions or Lua.
- A shared Redis instance coordinates application replicas, but also becomes a network dependency and possible bottleneck.
- Expiry removes inactive data, but TTL design affects correctness, memory, and cache effectiveness.

## Common mistakes

- Treating Redis as durable without configuring and testing persistence
- Using `KEYS` in production request paths instead of incremental scanning
- Storing unbounded data without expiry or eviction planning
- Performing non-atomic read-modify-write logic
- Assuming replication alone prevents all data loss

## Revision questions

- When is Redis more appropriate than a relational database?
- What is the difference between persistence and high availability?
- Why are atomic commands valuable in distributed systems?
- How can TTL affect both memory and correctness?

## Seen in this repository

- [URL Shortener caching and distributed IDs](../../url-shortener/backend/url-shortener/docs/07-redis-caching.md)
- [Rate Limiter Redis design](../../rate-limiter/backend/rate_limiter/docs/low-level-design.md#redis-token-bucket)
