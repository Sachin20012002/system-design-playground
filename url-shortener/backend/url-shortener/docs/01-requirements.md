# 01 - Requirements

# Introduction

The URL Shortener is a backend service that generates short URLs for long URLs and redirects users to the original destination when the short URL is accessed.

This document defines the functional requirements, non-functional requirements, assumptions, constraints, and scope of the current implementation. These requirements serve as the foundation for the architectural and implementation decisions described in the subsequent documentation.

---

# Problem Statement

Long URLs are difficult to share, consume unnecessary space, and are not user-friendly.

The objective of this project is to build a scalable URL shortening service that generates compact, unique URLs while providing fast and reliable redirection.

---

# Functional Requirements

## FR-1 Create Short URL

The system shall accept a valid long URL and generate a unique short URL.

**Input**

```text
https://example.com/products/mobile/samsung/s24-ultra
```

**Output**

```text
http://localhost/B
```

---

## FR-2 Redirect to Original URL

The system shall redirect users accessing a valid short URL to the corresponding long URL using an HTTP redirect response.

---

## FR-3 Generate Unique Short Codes

Each generated short URL shall be unique and map to exactly one long URL.

---

# Non-Functional Requirements

## NFR-1 Low Latency

Redirect requests should be served with low latency by utilizing Redis caching.

---

## NFR-2 Durability

URL mappings shall be persisted in PostgreSQL and survive application restarts.

---

## NFR-3 Scalability

The application shall support horizontal scaling by running multiple stateless application instances behind an Nginx load balancer.

---

## NFR-4 Observability

The application shall expose health and performance metrics through Spring Boot Actuator, Prometheus, and Grafana.

---

# Assumptions

The current implementation makes the following assumptions:

* Every input URL is valid.
* Short URLs do not expire.
* The application generates short URLs automatically.
* PostgreSQL is the source of truth for all URL mappings.
* Redis is available for caching and distributed ID generation.

---

# Constraints

The current implementation does not support:

* User authentication
* User authorization
* Custom short URLs
* URL expiration
* Click analytics
* Rate limiting

These features are considered future enhancements and are outside the scope of the current implementation.

---

# Scope

## In Scope

* URL shortening
* URL redirection
* PostgreSQL persistence
* Redis caching
* Distributed ID generation using Redis
* Docker-based deployment
* Nginx load balancing
* Performance testing using k6
* Monitoring using Prometheus and Grafana

---

## Out of Scope

The following capabilities are intentionally excluded from the current implementation:

* User management
* Analytics dashboard
* Multi-region deployment
* Kubernetes deployment
* CI/CD pipeline
* Distributed tracing

---

# Success Criteria

The implementation is considered successful if it satisfies the following objectives:

* Generate unique short URLs.
* Correctly redirect users to the original URL.
* Persist URL mappings in PostgreSQL.
* Improve redirect performance through Redis caching.
* Support multiple application instances behind Nginx.
* Expose operational metrics through Prometheus and Grafana.
* Demonstrate performance through k6 load testing.

---

# Summary

This document defines the requirements and scope of the URL Shortener project.

The following documents describe how these requirements are implemented through the system architecture, database design, caching strategy, deployment, monitoring, and performance optimizations.
