# URL Shortener - Engineering Documentation

## Overview

This directory contains the complete engineering documentation for the **URL Shortener** project.

While the source code demonstrates **how** the system is implemented, these documents explain **why** each architectural decision was made, the trade-offs considered, and how the system evolves toward a production-ready distributed service.

Reusable explanations of backend technologies and patterns live in the [Backend Engineering Handbook](../../../../backend-handbook/README.md). These project documents remain the source for URL Shortener behavior, configuration, measurements, and design decisions.

The documentation is intended to serve as:

- An engineering design reference
- A system design interview preparation guide
- A knowledge base for future enhancements
- A record of architectural decisions made throughout the project

## Choose a study mode

### Quick revision — 15 to 20 minutes

1. Review the [requirements](01-requirements.md#functional-requirements) and [API overview](03-api-design.md#api-overview).
2. Draw the [high-level architecture](04-high-level-design.md#system-architecture) from memory.
3. Explain the [creation and redirect flows](05-low-level-design.md#create-url-flow).
4. Recall the [database indexes](06-database-design.md#database-indexes) and [Cache-Aside flow](07-redis-caching.md#cache-pattern).
5. Answer the [interview questions](14-interview-questions.md) without reading the answers first.

### System-design interview practice — 30 to 45 minutes

1. Use the [system-design interview framework](../../../../backend-handbook/guides/system-design-interview-framework.md).
2. State requirements and calculate the key estimates in [capacity estimation](02-capacity-estimation.md).
3. Present the API, schema, high-level design, and two critical request flows.
4. Explain caching, horizontal scaling, bottlenecks, and failure modes.
5. Close with [trade-offs](12-trade-offs.md) and [future improvements](13-future-improvements.md), clearly separating current behavior from proposals.

### Deep implementation study

Read documents 01 through 13 in order while tracing the corresponding source and infrastructure files. Use document 14 afterward for active recall rather than as the first explanation of a topic.

## Fast reference

| Need | Go to |
| --- | --- |
| Scope and success criteria | [Requirements](01-requirements.md) |
| Traffic, storage, and bandwidth assumptions | [Capacity estimation](02-capacity-estimation.md) |
| Endpoints and status codes | [API design](03-api-design.md) |
| Components and request topology | [High-level design](04-high-level-design.md) |
| Classes and sequence flows | [Low-level design](05-low-level-design.md) |
| Schema, constraints, and indexes | [Database design](06-database-design.md) |
| Redis keys, TTL, and Cache-Aside | [Redis caching](07-redis-caching.md) |
| Nginx and application replicas | [Load balancing](08-load-balancing.md) |
| Workloads and benchmark context | [Performance testing](09-performance-testing.md) |
| Metrics and scrape topology | [Observability](10-observability.md) |
| Bottlenecks and scaling options | [Scaling strategies](11-scaling-strategies.md) |
| Decision reasoning | [Trade-offs](12-trade-offs.md) |
| Explicitly unimplemented ideas | [Future improvements](13-future-improvements.md) |
| Active recall | [Interview questions](14-interview-questions.md) |

---

# Project Overview

The URL Shortener is a distributed backend application designed to explore production-grade backend engineering concepts including:

- REST API Design
- Relational Database Design
- Redis Caching
- Distributed ID Generation
- Horizontal Scaling
- Load Balancing
- Performance Testing
- Observability
- Containerization
- System Design

The project evolves incrementally through multiple versions, with each version introducing new architectural concepts and infrastructure improvements.

---

# Documentation Structure

| Document | Description |
|----------|-------------|
| 01-requirements.md | Functional requirements, non-functional requirements, assumptions, constraints and system scope |
| 02-capacity-estimation.md | Traffic estimation, storage estimation, bandwidth estimation and scalability calculations |
| 03-api-design.md | REST API specification, request/response models, HTTP status codes and API design decisions |
| 04-high-level-design.md | Overall architecture, major components, deployment topology and request flows |
| 05-low-level-design.md | Internal class design, package structure, component interactions and sequence diagrams |
| 06-database-design.md | PostgreSQL schema, indexing strategy, query optimization and persistence layer |
| 07-redis-caching.md | Cache Aside pattern, Redis INCR, TTL strategy, cache metrics and caching trade-offs |
| 08-load-balancing.md | Nginx configuration, horizontal scaling and stateless application design |
| 09-performance-testing.md | k6 load testing, benchmark results, latency analysis and throughput measurements |
| 10-observability.md | Spring Boot Actuator, Micrometer, Prometheus, Grafana and monitoring architecture |
| 11-scaling-strategies.md | Current scalability, bottleneck analysis and future scaling approaches |
| 12-trade-offs.md | Architectural decisions, alternatives considered and engineering trade-offs |
| 13-future-improvements.md | Planned enhancements beyond the current implementation |
| 14-interview-questions.md | Frequently asked backend and system design interview questions with detailed answers |

---

# Architecture Diagrams

The `architecture/` directory contains editable Draw.io diagrams used throughout the documentation.

Planned diagrams include:

- High-Level Architecture
- Deployment Architecture
- URL Creation Sequence Diagram
- URL Redirect Sequence Diagram
- Cache Flow
- Database Interaction Flow
- Monitoring Architecture
- Performance Testing Architecture

All diagrams are maintained in Draw.io format so they can be updated as the project evolves.

---

# Version Timeline

| Version | Major Milestones |
|----------|------------------|
| V1 | Spring Boot project setup, PostgreSQL integration and basic URL shortening |
| V2 | Base62 encoding and collision handling |
| V3 | Redis caching and distributed ID generation using Redis INCR |
| V4 | Dockerized application |
| V5 | Docker Compose, multiple application instances and Nginx load balancing |
| V6 | Performance testing with k6, Prometheus metrics and Grafana service |
| V7 | Cache optimization, database indexing, engineering documentation and architectural improvements |
| V8 | Final documentation, diagrams, interview preparation and production refinements |

---

# Intended Audience

This documentation is written for:

- Backend Engineers
- Software Engineers
- System Design Interview Preparation
- Students learning distributed systems
- Developers interested in Spring Boot production architecture

---

# Recommended Reading Order

To understand the system progressively, the documents should be read in the following order:

1. Requirements
2. Capacity Estimation
3. API Design
4. High-Level Design
5. Low-Level Design
6. Database Design
7. Redis Caching
8. Load Balancing
9. Performance Testing
10. Observability
11. Scaling Strategies
12. Trade-offs
13. Future Improvements
14. Interview Questions

This sequence closely follows the structure of a typical system design interview and the lifecycle of designing a scalable backend system.

---

# Repository Structure

```text
url-shortener/
│
├── docs/
│   ├── README.md
│   ├── architecture/
│   ├── images/
│   └── ...
│
├── monitoring/
├── nginx/
├── performance/
├── src/
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
