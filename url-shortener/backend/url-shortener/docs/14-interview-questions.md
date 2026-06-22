# 14 - Interview Questions

# Introduction

This document contains interview questions based on the design and implementation of the URL Shortener project.

The questions cover backend development, distributed systems, database design, caching, scalability, performance, and observability.

They are intended as a revision guide and help explain the engineering decisions made throughout the project.

---

# Requirements

### 1. What are the functional requirements of a URL Shortener?

* Create a short URL.
* Redirect users to the original URL.
* Generate unique short codes.

---

### 2. What are the non-functional requirements?

* Low latency
* Durability
* Scalability
* Observability

---

### 3. Why is a URL Shortener considered a read-heavy system?

Redirect requests significantly outnumber URL creation requests.

---

# API Design

### 4. Why is the create endpoint versioned?

The creation API uses `/api/v1/urls` to support future API evolution without breaking existing clients.

---

### 5. Why is the redirect endpoint not versioned?

The short URL itself is the public identifier. Including a version in the redirect URL would unnecessarily change the generated links.

---

### 6. Why is HTTP 302 returned?

The application redirects the client to the original URL using the `Location` header with a `302 Found` response.

---

# Database

### 7. Why was PostgreSQL selected?

PostgreSQL provides ACID transactions, reliable persistence, indexing, and integrates well with Spring Data JPA.

---

### 8. Why is `short_code` unique?

Each short URL must map to exactly one long URL.

A unique constraint enforces this requirement.

---

### 9. What index exists on the table?

A unique B-Tree index on the `short_code` column.

---

### 10. How did you verify that PostgreSQL uses the index?

Using:

```sql
EXPLAIN ANALYZE
SELECT *
FROM url_mapping
WHERE short_code = 'B';
```

The execution plan showed an **Index Scan**.

---

# Redis

### 11. Why is Redis used?

Redis is used for:

* Distributed ID generation
* URL caching

---

### 12. Why use Redis `INCR`?

`INCR` is an atomic operation that guarantees globally unique IDs across multiple application instances.

---

### 13. Which caching strategy is implemented?

Cache-Aside.

---

### 14. What happens on a cache miss?

The application retrieves the URL from PostgreSQL, stores it in Redis, and returns the response.

---

### 15. What is the cache key?

The generated short code.

---

### 16. What is the cache value?

The original long URL.

---

### 17. Why is a TTL configured?

To automatically remove cached entries after a fixed duration and prevent indefinite memory growth.

---

# Load Balancing

### 18. Why use Nginx?

Nginx distributes requests across multiple Spring Boot application instances.

---

### 19. Why are the application instances stateless?

Any request should be handled by any application instance.

Shared state is stored in Redis and PostgreSQL.

---

### 20. How do multiple application instances share data?

Through shared Redis and PostgreSQL services.

---

# Performance

### 21. Which tool was used for load testing?

Grafana k6.

---

### 22. Which workloads were tested?

* URL creation
* URL redirect
* Mixed workload

---

### 23. Why are redirect requests faster than create requests?

Redirects are served from Redis whenever possible, while URL creation requires ID generation and database persistence.

---

### 24. What is P95 latency?

The response time within which 95% of requests completed.

---

### 25. Why is P95 more useful than average latency?

Average latency can hide slow requests, whereas P95 better represents typical user experience.

---

# Observability

### 26. Why is Spring Boot Actuator used?

To expose operational metrics.

---

### 27. What is Micrometer?

Micrometer is the metrics facade used by Spring Boot.

---

### 28. What does Prometheus do?

Prometheus periodically scrapes and stores application metrics.

---

### 29. What is Grafana used for?

Grafana visualizes metrics collected by Prometheus.

---

### 30. Which custom metrics were implemented?

* cache.hit
* cache.miss
* cache.put

---

# Scalability

### 31. How does the application scale?

By adding more stateless Spring Boot instances behind Nginx.

---

### 32. Which components are currently shared?

* Redis
* PostgreSQL

---

### 33. What is the current bottleneck?

Redis, PostgreSQL, and Nginx are each deployed as single instances.

---

# General Design

### 34. Why use a layered architecture?

To separate concerns between controllers, services, repositories, and persistence.

---

### 35. Why use Spring Data JPA?

It reduces boilerplate code and simplifies database access.

---

### 36. What is the source of truth?

PostgreSQL.

Redis acts only as a cache.

---

### 37. Why is Redis not the source of truth?

Redis is an in-memory cache and should not replace durable storage in the current architecture.

---

### 38. What is the biggest performance optimization in the project?

Serving redirect requests from Redis instead of querying PostgreSQL for every request.

---

### 39. What was the purpose of adding Prometheus and Grafana?

To monitor application health and performance.

---

### 40. What was the primary learning outcome from this project?

Building a scalable backend application by combining REST APIs, PostgreSQL, Redis, Docker, Nginx, performance testing, and observability into a single production-inspired system.

---

# Summary

These questions cover the major architectural and implementation decisions of the URL Shortener project.

A strong understanding of these topics demonstrates practical knowledge of backend development, system design, caching, database optimization, load balancing, observability, and performance engineering.
