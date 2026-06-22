# 10 - Observability

# Introduction

Observability enables engineers to understand the health and behavior of an application while it is running.

The URL Shortener application exposes runtime metrics using Spring Boot Actuator and Micrometer. These metrics are collected by Prometheus and visualized using Grafana.

The current implementation focuses on application metrics and performance monitoring.

---

# Observability Architecture

```text
                 Spring Boot
                      │
             Spring Boot Actuator
                      │
                 Micrometer
                      │
                      ▼
               /actuator/prometheus
                      │
                      ▼
                 Prometheus
                      │
                      ▼
                  Grafana
```

---

# Components

## Spring Boot Actuator

Spring Boot Actuator exposes operational endpoints that provide insight into the running application.

Current endpoint:

```text
/actuator/prometheus
```

This endpoint publishes application metrics in a format that Prometheus can scrape.

---

## Micrometer

Micrometer serves as the metrics facade used by Spring Boot.

It automatically collects common application metrics and allows custom application metrics to be registered.

---

## Prometheus

Prometheus periodically scrapes the metrics exposed by the application.

Responsibilities:

* Collect application metrics
* Store time-series data
* Provide metrics for visualization

Current scrape target:

```text
http://app1:8080/actuator/prometheus
http://app2:8080/actuator/prometheus
http://app3:8080/actuator/prometheus
```

---

## Grafana

Grafana connects to Prometheus and visualizes collected metrics through dashboards.

The current setup allows application metrics to be explored interactively.

---

# Metrics Collected

The application exposes both framework-provided and custom metrics.

## JVM Metrics

Examples include:

* Heap memory usage
* Non-heap memory usage
* Garbage collection
* Thread count

---

## HTTP Metrics

Examples include:

* HTTP request count
* HTTP request duration
* Response status codes

---

## Custom Cache Metrics

The application defines custom Micrometer counters for Redis caching.

| Metric     | Description            |
| ---------- | ---------------------- |
| cache.hit  | Number of cache hits   |
| cache.miss | Number of cache misses |
| cache.put  | Number of cache writes |

These metrics help evaluate the effectiveness of the Cache-Aside strategy.

---

# Metrics Flow

```text
Application
      │
      ▼
Micrometer
      │
      ▼
Spring Boot Actuator
      │
      ▼
Prometheus
      │
      ▼
Grafana Dashboard
```

---

# Monitoring Benefits

The current monitoring setup provides visibility into:

* Application health
* JVM behavior
* HTTP request performance
* Redis cache usage
* Overall application performance

These metrics simplify troubleshooting and performance analysis.

---

# Current Limitations

The current implementation does not include:

* Distributed tracing
* Centralized log aggregation
* Alerting
* Custom Grafana dashboards
* Service-level objectives (SLOs)

These capabilities can be added as the project evolves.

---

# Summary

The URL Shortener application uses Spring Boot Actuator, Micrometer, Prometheus, and Grafana to provide operational visibility.

Framework metrics combined with custom cache metrics enable monitoring of application health, request performance, and Redis cache behavior, providing a solid observability foundation for the current implementation.
