# 12 - Trade-offs

# Introduction

Every software system is the result of engineering trade-offs.

Rather than optimizing for every possible requirement, the URL Shortener project focuses on building a simple, scalable, and maintainable architecture while demonstrating key backend engineering concepts.

This document explains the major design decisions made during the implementation and the trade-offs associated with each choice.

---

# PostgreSQL vs NoSQL

## Chosen

PostgreSQL

## Why?

The application stores a simple mapping between a short URL and a long URL.

PostgreSQL provides:

* Strong consistency
* ACID transactions
* Reliable persistence
* Excellent indexing support
* Easy integration with Spring Data JPA

## Trade-off

A relational database may become a bottleneck as data volume grows, but it provides simplicity and reliability for the current implementation.

---

# Redis Cache vs Database Reads

## Chosen

Redis Cache

## Why?

Redirect requests occur far more frequently than URL creation requests.

Caching frequently accessed URL mappings reduces database load and improves response time.

## Trade-off

Cached data consumes additional memory and requires cache management, but significantly improves read performance.

---

# Cache-Aside Pattern

## Chosen

Cache-Aside

## Why?

The application checks Redis first and falls back to PostgreSQL only when the data is not present in the cache.

This keeps PostgreSQL as the source of truth while improving read performance.

## Trade-off

The first request after a cache miss is slower because it requires a database lookup.

---

# Redis INCR vs Local Counter

## Chosen

Redis INCR

## Why?

The application runs multiple Spring Boot instances.

Redis provides an atomic `INCR` operation that guarantees globally unique identifiers across all instances.

## Trade-off

The application depends on Redis being available for URL creation.

---

# Base62 Encoding vs UUID

## Chosen

Base62 Encoding

## Why?

Base62 produces short, readable URLs while supporting a large number of unique combinations.

## Trade-off

Base62 requires an encoding step, whereas UUIDs are generated directly.

However, UUIDs produce much longer URLs, making them less suitable for a URL shortening service.

---

# Stateless Application vs Local State

## Chosen

Stateless Application

## Why?

Keeping application instances stateless allows requests to be served by any instance behind the load balancer.

## Trade-off

All shared state must be stored in external systems such as Redis and PostgreSQL.

---

# Multiple Application Instances vs Single Instance

## Chosen

Three Spring Boot Instances

## Why?

Running multiple instances enables horizontal scaling and demonstrates load balancing with Nginx.

## Trade-off

The deployment becomes more complex compared to a single application instance.

---

# Nginx vs Direct Client Access

## Chosen

Nginx

## Why?

Nginx provides a single entry point and distributes incoming requests across multiple application instances.

## Trade-off

Nginx introduces an additional infrastructure component that must be configured and maintained.

---

# Docker Compose vs Manual Deployment

## Chosen

Docker Compose

## Why?

Docker Compose simplifies local development by starting all infrastructure components together.

## Trade-off

Docker Compose is intended for development environments and does not provide orchestration capabilities.

---

# Spring Data JPA vs Raw SQL

## Chosen

Spring Data JPA

## Why?

Spring Data JPA reduces boilerplate code and provides a clean repository abstraction.

## Trade-off

JPA abstracts SQL generation, making it less transparent than writing SQL manually.

---

# Monitoring Stack

## Chosen

Spring Boot Actuator + Micrometer + Prometheus + Grafana

## Why?

This combination provides application metrics, centralized collection, and visualization with minimal configuration.

## Trade-off

The monitoring stack introduces additional services that consume system resources.

---

# Summary

The current implementation favors simplicity, maintainability, and scalability while introducing production-inspired backend engineering concepts.

Each technology and architectural choice was selected to support the project's learning objectives without adding unnecessary complexity.

As the project evolves, some of these decisions may change to support larger workloads or additional features.
