# Requirements

## Functional requirements

- Identify clients by IP address before controller execution.
- Allow or reject each request through the configured rate-limiting strategy.
- Return HTTP 429 with a JSON response when a request exceeds the limit.
- Support configuration-driven selection among the current Sliding Window, Token Bucket, simplified Leaky Bucket, and Redis Token Bucket implementations.
- Support configurable window, request limit, bucket capacity, and bucket rate values.
- Share Redis Token Bucket state between multiple application instances.
- Load balance client traffic through Nginx in the Docker deployment.
- Export allowed and rejected request counters through the Prometheus Actuator endpoint.
- Provide a k6 load test that accepts HTTP 200 and HTTP 429 as valid application outcomes.

## Non-functional requirements

### Correctness and concurrency

- In-memory mutable client state must be updated under per-client synchronization.
- Distributed Token Bucket read-calculate-write behavior must execute atomically in Redis.

### Configurability

- Algorithm selection and limits must be configurable without changing Java code.
- Container-to-container addresses must use Compose service-name DNS rather than `localhost`.

### Observability

- Prometheus must scrape each application instance independently.
- Metrics must expose both admission decisions and the standard metrics supplied by Spring Boot and Micrometer.

### Local reproducibility

- Docker Compose must define two application services, Nginx, Redis, Prometheus, and Grafana.
- Persistent Redis and Grafana data must use named volumes.
- Compose resources must rely on project namespacing rather than explicit container names.

## Current scope boundaries

The repository does not currently implement:

- Authentication-, user-, tenant-, or API-key-based limits
- A currently selectable Fixed Window strategy
- An asynchronous Leaky Bucket request queue
- Redis replication, Redis Cluster, or Redis high availability
- Redis authentication or encrypted Redis transport
- TLS termination or application authentication
- Kubernetes or AKS deployment
- CI/CD deployment automation
- Alert rules
- Provisioned Grafana datasources or dashboards
- Comprehensive algorithm, concurrency, Redis, or end-to-end tests

These boundaries should not be interpreted as implemented roadmap commitments.
