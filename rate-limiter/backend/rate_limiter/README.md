# Rate Limiter

A Java 26 and Spring Boot 4 project for learning rate-limiting algorithms, distributed state, horizontal deployment, observability, and load testing.

The project is part of a larger System Design Playground repository. It is an educational implementation, not a production-ready rate-limiting service.

## Current status

| Version | Status | Milestone |
| --- | --- | --- |
| V1 | Complete | Fixed Window |
| V2 | Complete | Sliding Window using a sliding log |
| V3 | Complete | Token Bucket |
| V4 | Complete | Simplified Leaky Bucket |
| V5 | Complete | Redis-backed distributed Token Bucket using Lua |
| V6 | Complete | Docker deployment, Nginx load balancing, Prometheus, Grafana, custom metrics, and k6 |

V1 remains a completed historical milestone, but Fixed Window is not present in the current strategy enum or source tree. It is therefore not a currently selectable algorithm.

## Currently selectable algorithms

Set `rate-limit.algorithm` to one of:

- `SLIDING_WINDOW`
- `TOKEN_BUCKET`
- `LEAKY_BUCKET`
- `REDIS_TOKEN_BUCKET`

The application selects an implementation through the `RateLimiter` strategy interface. The default is `REDIS_TOKEN_BUCKET`.

### Sliding Window

Stores accepted request timestamps per client in a `Deque`. Expired timestamps are pruned when the next request arrives.

### Token Bucket

Starts each client with a full bucket, refills tokens lazily, supports fractional token accumulation, and consumes one token for each accepted request.

### Simplified Leaky Bucket

Models current occupancy and leaks that occupancy over time. It does not hold an asynchronous queue or delay synchronous HTTP requests. A request is rejected immediately when the modeled bucket is full because holding HTTP connections would increase connection duration, thread usage, and timeout risk.

### Redis Token Bucket

Stores shared client state in Redis. A Lua script atomically performs refill calculation, admission decision, state update, and TTL refresh so multiple application instances enforce the same limit.

## Architecture

```text
Client ----\
            -> Nginx -> Spring Boot app1 --\
k6 --------/             Spring Boot app2 ----> shared Redis

Prometheus ----scrapes----> app1 /actuator/prometheus
           ----scrapes----> app2 /actuator/prometheus

Grafana --------queries----> Prometheus
```

Request traffic enters through Nginx on host port `8080`. Nginx balances requests between `app1` and `app2` and sets `X-Forwarded-For` to its direct client address for the current local Docker topology. Both application containers use the Compose service name `redis` and share the same Redis state.

Prometheus scrapes both application instances every five seconds. Grafana is started by Compose, but this repository does not provision a Prometheus datasource or dashboards automatically; configure those through the Grafana UI.

## Configuration

Default application configuration:

```properties
rate-limit.max-requests=5
rate-limit.window-seconds=10
rate-limit.bucket-capacity=5
rate-limit.bucketRatePerSecond=1
rate-limit.algorithm=REDIS_TOKEN_BUCKET
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

`max-requests` and `window-seconds` apply to Sliding Window. Bucket capacity and rate apply to the Token Bucket, simplified Leaky Bucket, and Redis Token Bucket implementations.

Compose overrides the Redis host with `SPRING_DATA_REDIS_HOST=redis` and selects `REDIS_TOKEN_BUCKET` for both application services.

## Run locally

Requirements:

- JDK 26
- Maven
- Redis on `localhost:6379` when using the default Redis algorithm

Build and test:

```bash
mvn clean test
mvn clean package
```

Run the application after Redis is available:

```bash
mvn spring-boot:run
```

The protected endpoint is `http://localhost:8080/api/v1/test`.

To run without Redis, select one of the in-memory implementations, for example:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--rate-limit.algorithm=TOKEN_BUCKET
```

## Run the distributed stack

The Dockerfile copies the packaged JAR, so package the application before building the image:

```bash
mvn clean package
docker compose up --build -d
```

Endpoints:

| Component | URL |
| --- | --- |
| Rate-limited API through Nginx | `http://localhost:8080/api/v1/test` |
| Prometheus | `http://localhost:9090` |
| Grafana | `http://localhost:3000` |

In Grafana, add `http://prometheus:9090` as the Prometheus datasource. The hostname works because Grafana and Prometheus share the Compose network.

Useful lifecycle commands:

```bash
docker compose ps
docker compose logs
docker compose down
```

Redis and Grafana data use named volumes and survive a normal `docker compose down`. No explicit `container_name` values are used, allowing Compose project namespacing.

## Load testing with k6

Run from the host after the stack is available:

```bash
k6 run k6/rate-limiter-test.js
```

The script defaults to `http://localhost:8080`. Override the destination with `BASE_URL`. For example, when a k6 container is attached to the Compose network:

```bash
BASE_URL=http://nginx k6 run k6/rate-limiter-test.js
```

The test treats HTTP 200 and HTTP 429 as valid business outcomes. It also paces each virtual user with `sleep(1)` and checks a p95 response-time threshold of 200 ms.

## Metrics

Actuator exposes `/actuator/health`, `/actuator/info`, and `/actuator/prometheus`. Custom counters record admission decisions:

- `rate_limiter_requests_allowed_total`
- `rate_limiter_requests_rejected_total`

Prometheus also receives the JVM and standard Spring Boot metrics exported by Micrometer.

## Repository structure

```text
.
|-- Dockerfile
|-- docker-compose.yml
|-- k6/rate-limiter-test.js
|-- nginx/nginx.conf
|-- prometheus/prometheus.yml
|-- docs/
|   |-- high-level-design.md
|   |-- low-level-design.md
|   |-- requirements.md
|   `-- future-improvements.md
`-- src/
    |-- main/java/com/sachin/rate_limiter/
    `-- main/resources/scripts/token-bucket.lua
```

## Current limitations

- Fixed Window is not selectable in the current source tree.
- In-memory implementations do not share state across JVMs and retain inactive client entries.
- Redis is a single Compose service without replication, clustering, authentication, or high availability.
- Application instance readiness is not checked before Nginx and Prometheus start.
- Grafana datasource and dashboards are not provisioned from repository files.
- Metrics and Actuator endpoints have no authentication.
- The project has only a Spring context test; algorithm, concurrency, Lua, and integration coverage is limited.
- Client identity is IP-based and the proxy-header handling is intentionally simplified for the local Docker topology.

See [future improvements](docs/future-improvements.md) for possible follow-up work. These items are not implemented features.

## License

This project is intended for learning, experimentation, and backend engineering practice.
