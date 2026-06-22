# 11 - Scaling Strategies

# Introduction

Scalability is the ability of a system to handle increasing workloads by efficiently utilizing additional resources.

The current implementation of the URL Shortener supports horizontal scaling at the application layer through stateless Spring Boot instances deployed behind an Nginx load balancer.

---

# Current Architecture

```text
                   Client
                      │
                      ▼
                   Nginx
                      │
        ┌─────────────┼─────────────┐
        ▼             ▼             ▼
     App 1         App 2         App 3
        │             │             │
        └─────────────┼─────────────┘
                      │
             ┌────────┴────────┐
             ▼                 ▼
          Redis          PostgreSQL
```

---

# Stateless Application

Each Spring Boot application instance is stateless.

Application servers do not maintain user sessions or request-specific data.

This allows any request to be processed by any application instance.

---

# Horizontal Scaling

The application tier can be scaled by adding additional Spring Boot instances.

Example:

```text
3 Instances

↓

5 Instances

↓

10 Instances
```

Scaling the application does not require any changes to the application code.

The new instances only need to be added to the Nginx upstream configuration.

---

# Shared Infrastructure

All application instances share the same backend services.

| Component  | Purpose                                   |
| ---------- | ----------------------------------------- |
| Redis      | Distributed ID generation and URL caching |
| PostgreSQL | Persistent storage                        |

Using shared infrastructure ensures consistent behavior across all application instances.

---

# Redis and Scalability

Redis contributes to scalability in two ways.

## Distributed ID Generation

Redis provides globally unique IDs using the atomic `INCR` operation.

Multiple application instances can generate unique short codes without collisions.

---

## URL Caching

Frequently accessed URL mappings are served from Redis instead of PostgreSQL.

This reduces database load and improves redirect latency as traffic increases.

---

# Nginx Load Balancing

Nginx distributes incoming requests across available application instances.

Benefits include:

* Improved throughput
* Better resource utilization
* Even distribution of requests

---

# Current Scaling Characteristics

| Component   | Scaling Strategy     |
| ----------- | -------------------- |
| Spring Boot | Horizontal Scaling   |
| Redis       | Shared Instance      |
| PostgreSQL  | Shared Instance      |
| Nginx       | Single Load Balancer |

---

# Current Bottlenecks

As traffic grows, the following components may become bottlenecks:

* Redis
* PostgreSQL
* Nginx

These services are currently deployed as single instances.

---

# Future Scaling Opportunities

Potential improvements include:

* Redis Cluster
* PostgreSQL read replicas
* Multiple Nginx instances
* Health checks and automatic failover

These enhancements are outside the scope of the current implementation.

---

# Summary

The URL Shortener application is designed to scale horizontally at the application layer through multiple stateless Spring Boot instances behind Nginx.

Redis reduces database load through caching and provides distributed ID generation, while PostgreSQL serves as the persistent data store.

The current architecture provides a scalable foundation for the project's workload while remaining simple and easy to understand.
