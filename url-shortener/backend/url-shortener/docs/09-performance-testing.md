# 09 - Performance Testing

# Introduction

Performance testing was conducted to evaluate the throughput and response times of the URL Shortener application under concurrent load.

The tests were performed using **Grafana k6** against the Docker Compose deployment consisting of:

* Nginx
* Three Spring Boot application instances
* Redis
* PostgreSQL

The objective was to verify that the application behaves correctly under concurrent requests and to establish baseline performance measurements.

---

# Test Environment

| Component         | Technology     |
| ----------------- | -------------- |
| Load Testing Tool | Grafana k6     |
| Load Balancer     | Nginx          |
| Application       | Spring Boot    |
| Cache             | Redis          |
| Database          | PostgreSQL     |
| Deployment        | Docker Compose |

---

# Test Scenarios

Three different workloads were executed.

## 1. URL Creation

Creates new short URLs using the REST API.

Endpoint:

```http
POST /api/v1/urls
```

Purpose:

* Measure write performance
* Validate distributed ID generation
* Verify persistence

---

## 2. URL Redirect

Redirects previously created short URLs.

Endpoint:

```http
GET /{shortCode}
```

Purpose:

* Measure read performance
* Evaluate Redis cache performance
* Validate redirect latency

---

## 3. Mixed Workload

Executes URL creation and redirect requests concurrently.

Purpose:

* Simulate a more realistic workload
* Measure overall application throughput
* Verify stable behavior under concurrent reads and writes

---

# Benchmark Results

## URL Creation

| Metric          | Value                  |
| --------------- | ---------------------- |
| Throughput      | ~2,600 Requests/Second |
| Average Latency | ~4.8 ms                |
| P95 Latency     | ~7.0 ms                |
| Success Rate    | ~100%                  |

---

## URL Redirect

| Metric          | Value                  |
| --------------- | ---------------------- |
| Throughput      | ~9,600 Requests/Second |
| Average Latency | ~5.1 ms                |
| P95 Latency     | ~8.8 ms                |
| Success Rate    | 100%                   |

---

## Mixed Workload

| Metric          | Value                   |
| --------------- | ----------------------- |
| Throughput      | ~10,000 Requests/Second |
| Average Latency | ~4.8 ms                 |
| P95 Latency     | ~9.1 ms                 |
| Success Rate    | 100%                    |

---

# Understanding the Metrics

## Throughput

Throughput represents the number of HTTP requests processed per second.

Higher throughput indicates that the application can handle more concurrent traffic.

---

## Average Latency

Average latency represents the mean response time across all requests.

Although useful, average latency can hide occasional slow requests.

---

## P95 Latency

P95 latency indicates that **95% of all requests completed within the reported response time**.

This metric provides a better representation of user experience than the average latency.

---

# Observations

The performance tests demonstrate that:

* Redirect requests achieve significantly higher throughput than URL creation requests.
* Redis caching reduces the need for repeated database lookups.
* The application maintains low response times under concurrent load.
* The Docker Compose deployment remained stable throughout testing.

---

# Factors Affecting Performance

Current performance depends on several components.

* Nginx request distribution
* Redis cache hit rate
* PostgreSQL query performance
* JVM performance
* Available CPU and memory

Changes to any of these components may affect the benchmark results.

---

# Limitations

The current benchmark focuses on functional application performance.

The following scenarios were not evaluated:

* Long-running endurance tests
* Stress testing beyond system capacity
* Failure testing
* Redis outage scenarios
* PostgreSQL outage scenarios

These may be explored in future versions.

---

# Summary

Performance testing confirms that the URL Shortener application is capable of handling concurrent create and redirect requests while maintaining low response times.

Redis caching, horizontal scaling through Nginx, and a simple persistence model contribute to stable performance and good throughput in the current implementation.

The benchmark results provide a baseline for future performance improvements and architectural enhancements.
