# High-Level Design

> Reusable background: [Rate limiting](../../../../backend-handbook/rate-limiting/rate-limiting.md), [stateless services](../../../../backend-handbook/architecture/stateless-services-and-shared-state.md), and [Redis atomicity](../../../../backend-handbook/redis/atomicity-and-lua.md). This page documents the deployed Rate Limiter architecture.

## Objective

The current system demonstrates rate limiting across two Spring Boot instances that share Redis state. Nginx provides one entry point, while Prometheus and Grafana provide local observability.

## Architecture

```text
                         Request flow

Client ----\
            -> Nginx -> app1 --\
k6 --------/          -> app2 ----> shared Redis

                         Monitoring flow

Prometheus ----scrapes----> app1 /actuator/prometheus
           ----scrapes----> app2 /actuator/prometheus

Grafana --------queries----> Prometheus
```

Compose provides service-name DNS. Application containers connect to `redis:6379`; Nginx connects to `app1:8080` and `app2:8080`; Grafana can connect to `prometheus:9090`.

## Request flow

1. A client or k6 sends a request through Nginx.
2. Nginx forwards it to one application instance and sets `X-Forwarded-For` to the direct client address seen by Nginx.
3. `RateLimitFilter` resolves the client identifier and calls the configured `RateLimiter` strategy.
4. With the default `REDIS_TOKEN_BUCKET` strategy, the application executes the Lua script against the client's Redis key.
5. The script refills tokens, decides whether one token can be consumed, updates the hash, refreshes its TTL, and returns the decision.
6. An accepted request reaches the controller. A rejected request returns HTTP 429 immediately.
7. The filter increments the corresponding allowed or rejected Micrometer counter.

## Monitoring flow

1. Each application exposes Prometheus-format metrics at `/actuator/prometheus`.
2. Prometheus scrapes `app1:8080` and `app2:8080` every five seconds.
3. Grafana queries Prometheus to visualize custom admission counters and standard JVM metrics.

The repository starts Grafana with persistent storage but does not provision its datasource or dashboards. Those are configured manually through the Grafana UI.

## Why shared Redis is required

Each in-memory algorithm stores state inside one JVM. With two application instances, requests for the same client can reach different processes, so local maps cannot enforce one shared limit.

Redis gives both application instances one shared state location. The Redis Token Bucket uses keys derived from the client identifier, allowing either instance to read and update the same bucket.

## Why Lua is used

Token Bucket admission is a read-calculate-write operation. Separate Redis commands could interleave when multiple application instances process requests concurrently.

Redis executes a Lua script atomically. The script reads the current hash, calculates earned tokens, makes the admission decision, writes the new state, and refreshes the TTL as one uninterrupted operation.

## Persistence and service scope

Redis append-only persistence is enabled and `/data` is backed by the `redis-data` named volume. Grafana uses the `grafana-data` named volume. Prometheus configuration and Nginx configuration are read-only bind mounts; Prometheus time-series data is not assigned a named volume in the current Compose file.

The deployment is intended for local learning. It does not include Redis high availability, application readiness checks, TLS, authentication, orchestration, alerting, or automatic Grafana provisioning.
