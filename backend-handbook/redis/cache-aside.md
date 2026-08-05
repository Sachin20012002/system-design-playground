# Cache-Aside

## Problem

Repeated reads from durable storage can add latency and database load. Cache-Aside keeps frequently read values in a faster store without making the cache the durable source of truth.

## Read flow

1. Read the value from the cache.
2. On a hit, return it.
3. On a miss, read the source of truth.
4. Write the result to the cache with an appropriate TTL.
5. Return the result.

The application owns this flow; the database does not populate the cache automatically.

## Trade-offs

- Hits reduce latency and database work.
- The first request after a miss remains slower.
- Cached data can become stale when the source changes.
- Concurrent misses for one popular key can create a cache stampede.
- A TTL limits stale data and memory growth but is not a complete invalidation strategy.

## Failure decisions

Decide whether a cache failure should fail the request or fall back to the source of truth. The answer depends on whether Redis is only an optimization or also holds correctness-critical state.

## Common mistakes

- Treating cached data as durable
- Using no expiration or invalidation policy
- Caching missing values forever
- Ignoring stampedes for hot keys
- Recording hits without misses, making hit ratio impossible to calculate

## Revision questions

- What happens on a Cache-Aside miss?
- Why can cached data become stale?
- How does TTL selection affect freshness and hit rate?
- When should an application bypass a failed cache?

## Seen in this repository

- [URL Shortener Redis caching](../../url-shortener/backend/url-shortener/docs/07-redis-caching.md)
