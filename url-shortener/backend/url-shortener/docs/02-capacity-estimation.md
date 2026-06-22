# 02 - Capacity Estimation

# Introduction

Capacity estimation helps determine the approximate resources required to operate the URL Shortener under the expected workload.

The calculations in this document are based on the assumptions used throughout this project and are intended to demonstrate the reasoning process followed during a system design exercise rather than provide production capacity planning.

---

# Assumptions

The following assumptions are used for all calculations.

| Parameter                |       Value |
| ------------------------ | ----------: |
| Daily Active Users (DAU) | 100 Million |
| URL Creation Ratio       |  10% of DAU |
| Redirect Ratio           | 100% of DAU |
| Peak Traffic Factor      |          3× |

These assumptions represent a read-heavy workload, which is common for URL shortening services.

---

# URL Creation Traffic

## Daily Requests

```text
10% of 100 Million Users

= 10 Million URL Creations / Day
```

---

## Requests Per Second

```text
10,000,000
------------
86,400

≈ 116 Requests / Second
```

Rounded:

```text
≈ 120 Requests / Second
```

---

# Redirect Traffic

## Daily Requests

```text
100 Million Redirect Requests / Day
```

---

## Average Requests Per Second

```text
100,000,000
------------
86,400

≈ 1,157 Requests / Second
```

Rounded:

```text
≈ 1,160 Requests / Second
```

---

## Peak Requests Per Second

Applying a peak traffic factor of 3:

```text
1,160 × 3

≈ 3,480 Requests / Second
```

Rounded:

```text
≈ 3,500 Requests / Second
```

---

# Read / Write Ratio

The workload is highly read-heavy.

| Operation  | Approximate Requests |
| ---------- | -------------------: |
| Create URL |              120 RPS |
| Redirect   |       3,500 Peak RPS |

This indicates that redirect requests significantly outnumber URL creation requests.

Consequently, optimizing read performance has a greater impact on the overall system than optimizing writes.

---

# Storage Estimation

Assume the following average sizes.

| Field        | Approximate Size |
| ------------ | ---------------: |
| ID           |          8 Bytes |
| Short Code   |          8 Bytes |
| Long URL     |        200 Bytes |
| Row Overhead |         40 Bytes |

Approximate row size:

```text
≈ 256 Bytes
```

Daily storage:

```text
10 Million × 256 Bytes

≈ 2.56 GB / Day
```

Annual storage:

```text
≈ 934 GB / Year
```

These estimates exclude indexes, backups, replication overhead and metadata.

---

# Bandwidth Estimation

Create requests are relatively infrequent and lightweight.

Redirect requests primarily return an HTTP 302 response with a Location header.

Consequently, network bandwidth is generally not the primary bottleneck for this system compared to database lookups and application throughput.

---

# Scaling Considerations

The workload characteristics suggest the following architecture.

* Stateless Spring Boot application instances.
* Horizontal scaling behind an Nginx load balancer.
* Redis used to reduce database reads.
* PostgreSQL used as the source of truth.

This architecture allows additional application instances to be added without modifying application logic.

---

# Capacity Bottlenecks

Potential bottlenecks include:

* PostgreSQL write throughput.
* Redis memory usage.
* Network bandwidth under sustained high traffic.
* CPU utilization on application instances.

These components should be monitored using Prometheus and Grafana.

---

# Summary

Based on the current assumptions, the system is expected to handle approximately:

| Metric                |   Estimate |
| --------------------- | ---------: |
| URL Creation          |   ~120 RPS |
| Average Redirects     | ~1,160 RPS |
| Peak Redirects        | ~3,500 RPS |
| Daily Storage Growth  |   ~2.56 GB |
| Annual Storage Growth |    ~934 GB |

The read-heavy nature of the workload makes Redis caching and horizontal application scaling the primary optimizations for achieving low latency and high throughput.
