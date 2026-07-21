# Rate Limiter

A production-inspired Rate Limiter built with **Java** and **Spring Boot** as part of my **System Design Playground**.

The objective of this project is to learn how modern backend systems protect services from excessive traffic using progressively more advanced rate limiting algorithms and distributed system techniques.

Rather than implementing the most complex solution immediately, the project is developed incrementally. Each version introduces a new concept while keeping the codebase clean, well-documented, and production-oriented.

## Project Goals

- Learn common rate limiting algorithms
- Understand distributed system challenges
- Build production-inspired backend services
- Measure and optimize performance
- Add observability and monitoring
- Document architectural decisions and trade-offs

## Planned Roadmap

- **V1** – Fixed Window Rate Limiter
- **V2** – Sliding Window Rate Limiter
- **V3** – Token Bucket Rate Limiter
- **V4** – Leaky Bucket Rate Limiter
- **V5** – Redis Distributed Rate Limiter
- **V6** – Docker & Horizontal Scaling
- **V7** – Performance Testing (Grafana k6)
- **V8** – Observability (Prometheus & Grafana)
- **V9** – Engineering Documentation

## Planned Technology Stack

### Backend

- Java 26
- Spring Boot 4

### Data Store

- Redis

### Infrastructure

- Docker
- Docker Compose
- Nginx

### Monitoring

- Spring Boot Actuator
- Micrometer
- Prometheus
- Grafana

### Performance Testing

- Grafana k6

## Current Status

Project initialization.

The first implementation will be a basic **Fixed Window Rate Limiter**, providing a foundation for more advanced algorithms in future versions.

## Repository Structure

```
docs/
src/
README.md
```

## License

This project is intended for learning, experimentation, and backend engineering practice.
