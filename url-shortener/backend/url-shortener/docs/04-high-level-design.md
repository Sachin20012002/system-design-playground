# 04 - High Level Design

# Introduction

The High Level Design (HLD) describes the major components of the URL Shortener system and how they interact to process incoming requests.

The current architecture focuses on scalability, low latency, and maintainability while remaining simple enough to understand and extend.

---

# System Architecture

![System Architecture](images/architecture-v7.png)

---

# System Components

## Client

The client interacts with the application through HTTP requests.

Supported operations:

* Create Short URL
* Redirect to Original URL

---

## Nginx

Nginx acts as the entry point to the system.

Responsibilities:

* Accept incoming requests
* Distribute traffic across application instances
* Improve scalability
* Hide backend application topology

---

## Spring Boot Application

The application contains the business logic of the URL Shortener.

Responsibilities:

* Process API requests
* Generate short URLs
* Perform redirects
* Access Redis
* Access PostgreSQL
* Expose application metrics

Multiple application instances can run simultaneously because the application is stateless.

---

## Redis

Redis serves two purposes.

### Distributed ID Generation

Redis generates globally unique numeric identifiers using the `INCR` command.

These identifiers are encoded into Base62 short codes.

---

### URL Cache

Redis stores recently accessed URL mappings.

This reduces database lookups and improves redirect latency.

The application uses the Cache-Aside pattern.

---

## PostgreSQL

PostgreSQL is the primary data store.

Responsibilities:

* Persist URL mappings
* Guarantee data durability
* Store the relationship between short URLs and long URLs

Every URL mapping is permanently stored in PostgreSQL.

---

## Prometheus

Prometheus periodically collects application metrics exposed through Spring Boot Actuator.

Examples include:

* JVM metrics
* HTTP metrics
* Cache metrics

---

## Grafana

Grafana visualizes the metrics collected by Prometheus.

Dashboards provide insight into application health and performance.

---

# Request Flow

## Create Short URL

```text
Client
    │
    ▼
Nginx
    │
    ▼
Application
    │
    ▼
Redis (INCR)
    │
    ▼
Base62 Encoding
    │
    ▼
PostgreSQL
    │
    ▼
Return Short Code
```

---

## Redirect Request

```text
Client
    │
    ▼
Nginx
    │
    ▼
Application
    │
    ▼
Redis
   │
   ├── Cache Hit
   │      │
   │      ▼
   │   Return Long URL
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
     Return Long URL
```

---

# Design Principles

The architecture follows the following principles.

## Stateless Application

Application instances do not maintain session state.

Any request can be processed by any application instance.

---

## Separation of Responsibilities

Each component has a clearly defined responsibility.

| Component   | Responsibility                      |
| ----------- | ----------------------------------- |
| Nginx       | Load Balancing                      |
| Spring Boot | Business Logic                      |
| Redis       | Cache and Distributed ID Generation |
| PostgreSQL  | Persistent Storage                  |
| Prometheus  | Metrics Collection                  |
| Grafana     | Metrics Visualization               |

---

## Horizontal Scalability

Additional application instances can be added behind Nginx without modifying application code.

This allows the system to handle increasing traffic by scaling the application tier.

---

# Advantages of the Architecture

The current architecture provides:

* Stateless application servers
* Low-latency redirects through Redis caching
* Persistent storage using PostgreSQL
* Horizontal scalability
* Centralized monitoring and observability
* Production-inspired deployment using Docker Compose

---

# Current Limitations

The current implementation intentionally excludes:

* Rate limiting
* URL expiration
* Click analytics
* User management
* Multi-region deployment
* High availability for Redis and PostgreSQL

These features are considered future enhancements.

---

# Summary

The current high-level architecture separates request routing, business logic, caching, persistence, and monitoring into dedicated components.

This separation improves maintainability, enables horizontal scaling, and provides a solid foundation for future enhancements while keeping the implementation simple and easy to understand.
