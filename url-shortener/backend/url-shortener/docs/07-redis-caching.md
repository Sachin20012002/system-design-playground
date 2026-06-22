# 07 - Redis Caching

# Introduction

Redis is used to improve the performance and scalability of the URL Shortener application.

The current implementation uses Redis for two purposes:

* Distributed ID generation
* URL caching

Using Redis reduces database load, improves redirect latency, and enables multiple application instances to generate unique short URLs safely.

---

# Why Redis?

Redis was selected because it provides:

* In-memory data storage
* Extremely low latency
* Atomic operations
* Simple key-value data model
* Seamless integration with Spring Boot

These characteristics make Redis suitable for both caching and distributed counters.

---

# Redis Responsibilities

The application uses Redis in two different ways.

| Purpose                   | Description                             |
| ------------------------- | --------------------------------------- |
| Distributed ID Generation | Generates globally unique numeric IDs   |
| URL Cache                 | Stores frequently accessed URL mappings |

---

# Distributed ID Generation

Every new URL requires a unique identifier.

Instead of maintaining a counter inside each application instance, Redis provides a shared atomic counter using the `INCR` command.

Current key:

```text
global:url:id
```

Application flow:

```text
Application
      │
      ▼
Redis
      │
 INCR global:url:id
      │
      ▼
Unique Numeric ID
      │
      ▼
Base62 Encoding
      │
      ▼
Short Code
```

Because Redis executes `INCR` atomically, multiple application instances can safely generate unique IDs without collisions.

---

# URL Caching

Redirect requests are significantly more frequent than URL creation requests.

To avoid querying PostgreSQL on every redirect, recently accessed URL mappings are stored in Redis.

Current cache structure:

```text
Key   : shortCode
Value : longUrl
```

Example:

```text
A  → https://example.com

B  → https://google.com

C  → https://github.com
```

---

# Cache Pattern

The application follows the **Cache-Aside** pattern.

## Cache Hit

```text
Client
    │
    ▼
Application
    │
    ▼
Redis
    │
    ▼
Return Long URL
```

The database is not accessed.

---

## Cache Miss

```text
Client
    │
    ▼
Application
    │
    ▼
Redis
    │
 Cache Miss
    │
    ▼
PostgreSQL
    │
    ▼
Store in Redis
    │
    ▼
Return Long URL
```

After the first database lookup, subsequent requests are served directly from Redis until the cache entry expires.

---

# Cache TTL

Cached URL mappings are stored with a Time-To-Live (TTL).

Current configuration:

```java
Duration.ofHours(24)
```

The TTL ensures cached entries are automatically removed after 24 hours, preventing stale data from remaining in memory indefinitely.

---

# Cache Metrics

The application records custom cache metrics using Micrometer.

Current metrics:

| Metric     | Description            |
| ---------- | ---------------------- |
| cache.hit  | Number of cache hits   |
| cache.miss | Number of cache misses |
| cache.put  | Number of cache writes |

These metrics are exposed through Spring Boot Actuator and collected by Prometheus.

Grafana can be used to visualize cache behavior over time.

---

# Redis Access Flow

## URL Creation

```text
Application
      │
      ▼
Redis INCR
      │
      ▼
Generate ID
      │
      ▼
Base62 Encode
      │
      ▼
Save to PostgreSQL
```

---

## Redirect

```text
Application
      │
      ▼
Redis
   │
   ├── Cache Hit
   │      │
   │      ▼
   │   Return URL
   │
   └── Cache Miss
          │
          ▼
     PostgreSQL
          │
          ▼
     Store in Redis
          │
          ▼
      Return URL
```

---

# Benefits

The current Redis implementation provides:

* Fast redirect responses
* Reduced database load
* Atomic distributed ID generation
* Support for multiple application instances
* Improved scalability

---

# Current Limitations

The current implementation does not include:

* Redis persistence configuration
* Redis replication
* Redis clustering
* Cache invalidation strategy
* Distributed locking

These capabilities are outside the scope of the current project.

---

# Summary

Redis plays a key role in the URL Shortener architecture by providing distributed ID generation and low-latency URL caching.

The combination of atomic counters, Cache-Aside caching, and configurable TTL improves application performance while supporting horizontal scaling across multiple application instances.
