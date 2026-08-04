# Interview Notes

Detailed questions and answers based on the current repository. Fixed Window is discussed as a historical milestone, not as a currently selectable strategy.

## General design

### Why does a service need rate limiting?

Rate limiting protects finite resources from accidental overload, abusive clients, retry storms, and traffic spikes. It can preserve availability, enforce a usage policy, and keep one client from consuming a disproportionate share of capacity.

The functional requirement is to identify a client, decide whether a request is admissible, and return HTTP 429 when it is not. Important non-functional requirements include low decision latency, concurrency correctness, configurability, bounded state, distributed consistency when instances scale horizontally, and useful metrics.

### Should rate limiting happen before or after authentication?

Before-authentication limiting can use network identity such as IP address and protects authentication endpoints and application resources before credentials are processed. Its identity is coarse: users behind one NAT may share an IP, and proxy handling must be trusted.

After-authentication limiting can use a user, tenant, API key, or plan and express fairer business quotas, but authentication work has already occurred. Many systems use both layers. This project implements only early IP-based limiting.

### Why use `OncePerRequestFilter`?

`RateLimitFilter` needs to make the admission decision before controller business logic. Spring's `OncePerRequestFilter` provides a servlet-filter integration intended to run once for a request dispatch and keeps the concern outside individual controllers.

### Why does the filter depend on `RateLimiter`?

The filter owns HTTP concerns: client resolution, HTTP 429, JSON response writing, metrics, and continuing the filter chain. Algorithms own only `boolean allow(String clientId)`. Depending on the interface keeps those responsibilities separate and lets configuration choose the implementation without changing the filter.

### Why is `RateLimiter` not annotated with `@Service`?

An interface is a contract, not an implementation to instantiate. The concrete classes carry named `@Service` annotations. Annotating the interface would not provide a usable algorithm instance and would blur the distinction between abstraction and bean implementation.

### How is an algorithm selected?

`RateLimitProperties` binds `rate-limit.algorithm` to `RateLimitAlgorithm`. `RateLimiterApplication.rateLimiter(...)` receives four qualified candidates and returns the selected bean from a switch.

The verified internal bean names are:

| Enum value | Bean qualifier | Implementation |
| --- | --- | --- |
| `SLIDING_WINDOW` | `slidingWindow` | `SlidingWindowRateLimiter` |
| `TOKEN_BUCKET` | `tokenBucket` | `TokenRateLimiter` |
| `LEAKY_BUCKET` | `leakyBucket` | `LeakyBucketRateLimiter` |
| `REDIS_TOKEN_BUCKET` | `redisTokenBucket` | `RedisTokenBucketRateLimiter` |

Qualifiers disambiguate beans internally. The external configuration remains enum-based and type-safe.

### Why was `@Primary` not used?

`@Primary` supplies one preferred bean when several candidates exist. That would encode a fixed default in bean resolution, not select among algorithms from configuration. The explicit selector bean makes the choice visible and driven by `rate-limit.algorithm`.

## Fixed Window - historical V1

### How does Fixed Window work?

It associates a counter with a fixed interval. A request increments the counter when it is below the limit; the next interval resets the count.

### What are its trade-offs?

- Advantages: simple, constant-sized state per client, and O(1) decisions.
- Boundary problem: a client can use its full allowance near the end of one interval and again near the start of the next, producing nearly twice the intended traffic in a short period.
- Memory: O(number of clients), excluding cleanup overhead.

Fixed Window is no longer present in the current source tree or enum.

## Sliding Window using a sliding log

### How does the current Sliding Window work?

`SlidingWindowRateLimiter` stores accepted timestamps in each client's `ClientRequestLog`. On a request it calculates the window start, removes timestamps older than that point, checks the remaining count, and appends the current timestamp only when allowed.

### Why use a `Deque`?

Timestamps are chronological. Expired entries are removed from the head with `peekFirst()` and `removeFirst()`, while accepted timestamps are appended with `addLast()`. Those end operations are O(1).

### What are its complexity and memory costs?

Each timestamp is inserted and removed once, so pruning is amortized O(1), although a single request may remove several expired entries. The log is accurate but stores up to the configured accepted-request count per active client, making it more memory intensive than a counter.

## Token Bucket

### How does Token Bucket work here?

A new `ClientTokenBucket` starts full. Each request calculates elapsed seconds, earns `elapsed * bucketRatePerSecond` tokens, caps the total at `bucketCapacity`, and consumes one token if at least one is available.

Relevant configuration keys are:

```properties
rate-limit.bucket-capacity=5
rate-limit.bucketRatePerSecond=1
```

### Why lazy refill?

No background task is needed. The state is brought current only when a request arrives, reducing scheduler and idle-client work.

### Why use `double` tokens?

Time does not advance in exact one-second units. At one token per second, 250 milliseconds earns 0.25 token. Retaining fractional credit avoids losing refill precision, while admission still requires and consumes one full token.

### How does it support bursts?

The capacity is accumulated permission. An idle client can spend several stored tokens quickly. The refill rate controls how quickly that permission returns.

### Is refill rate the processing rate?

No. Token Bucket is admission control. Once a request is accepted, this implementation does not pace or serialize its business processing. Downstream concurrency and processing time are separate concerns.

## Simplified Leaky Bucket

### How does the current implementation work?

`LeakyBucketRateLimiter` stores occupancy as `water`. Elapsed time reduces occupancy at `bucketRatePerSecond`; an accepted request adds one unit. A request is rejected when the additional unit would exceed `bucketCapacity`.

### Why does it resemble Token Bucket mathematically?

Both current implementations immediately admit or reject and lazily adjust one continuous value. Token Bucket tracks available capacity upward; this Leaky Bucket tracks occupied capacity downward. They are close mathematical mirrors in this form.

### What makes a classic Leaky Bucket fundamentally different?

A classic traffic shaper holds work in a real queue and releases it at a fixed rate. That changes execution timing rather than only admission. It can smooth output even when input is bursty.

### Why not queue synchronous HTTP requests here?

Waiting would hold connections longer, occupy server request-handling resources, increase timeout risk, and couple queue delay to client patience. Immediate HTTP 429 is more honest for this project's synchronous filter.

Real queues are better suited to network traffic shaping, message processing, background jobs, and printer queues.

## Concurrency

### Why is `ConcurrentHashMap` alone insufficient?

It protects map operations such as `computeIfAbsent`, but the values contain multiple mutable fields or a mutable deque. Refill, decision, and update must act as one critical section. Without synchronization, two same-client requests could observe and spend the same capacity.

### Why synchronize per client?

The code synchronizes on the `ClientRequestLog`, `ClientTokenBucket`, or `ClientLeakyBucket`. Requests for one client serialize their state transition, while different clients use different locks and can proceed concurrently.

### Why does Java synchronization fail across application instances?

A monitor exists only inside one JVM. `app1` cannot acquire or observe a monitor inside `app2`. Distributed admission therefore needs shared state plus a cross-process atomic operation; this project uses Redis and Lua.

## Redis data model

### What is stored in Redis?

Each client uses:

```text
rate-limit:token-bucket:<clientId>
```

The value is a Redis Hash with fields:

```text
tokens
lastRefillTime
```

`HMGET` retrieves those selected fields. `HSET` writes them. `HGETALL` would return every field and value but is not used by the current script.

### How does TTL work?

Java calculates twice the time required to refill an empty bucket:

```text
ceil((bucketCapacity / bucketRatePerSecond) * 2)
```

It uses at least one second. For a non-positive rate it uses one hour. Lua calls `EXPIRE` after every decision, so active clients refresh their TTL and inactive client hashes eventually disappear.

### Why are `KEYS` and `ARGV` separate?

Java passes `List.of(key)` as the script's key list, producing `KEYS[1]`. Capacity, refill rate, current milliseconds, and TTL follow as `ARGV[1]` through `ARGV[4]`.

Redis needs declared keys separately from ordinary arguments so it can understand which keys a script accesses. That distinction also matters for Redis Cluster routing: keys involved in one operation must be routable to the appropriate hash slot. This project uses one key per execution and does not implement Redis Cluster.

## Redis Lua execution

### What is Lua?

Lua is a small general-purpose scripting language that predates Redis. Redis embeds a Lua runtime so clients can send server-side logic close to the data.

### Why use a Redis script?

The admission path must read state, calculate refill, decide, update the hash, and refresh TTL. Redis executes the script atomically, so another command cannot interleave halfway through that sequence.

### Why is `MULTI/EXEC` alone insufficient?

`MULTI/EXEC` queues commands and executes them together, but ordinary transaction commands cannot use the result of `HMGET` to calculate and conditionally queue the correct `HSET` values inside the same server-side decision. The read-calculate-write logic still needs another mechanism.

### What alternatives exist?

- `WATCH` plus retry: watch the key, read and calculate in the client, then attempt a transaction. Conflicts abort the transaction and the client retries. This adds network round trips and retry behavior under contention.
- Distributed lock: acquire a lock per relevant state, perform the commands, and release safely. This adds lock ownership, expiry, failure, and latency complexity.
- Lua: keep the small deterministic state transition in Redis and execute it atomically. That is the selected approach.

### What are `EVAL` and `EVALSHA`?

`EVAL` sends script text for execution. `EVALSHA` sends the SHA-1 digest of a script already present in Redis's script cache, reducing repeated script transfer.

`RedisTokenBucketRateLimiter` uses Spring Data Redis's `DefaultRedisScript`. The client script executor can try the digest and fall back to sending the script when Redis reports `NOSCRIPT`. If the script text changes, its digest changes, so the new version is loaded and referenced by a new SHA. Redis script caching is an execution optimization; the Lua file remains the source of behavior.

## Docker and Compose

### What is the difference between an image and a container?

An image is an immutable packaged filesystem and startup definition. A container is a running or stopped instance created from an image, with its own writable layer and network identity.

### How do Maven package, Docker build, and Compose up differ?

- `mvn clean package` compiles, tests, and creates `target/rate_limiter-0.0.1-SNAPSHOT.jar`.
- `docker compose build` or `docker compose up --build` rebuilds the application image, whose Dockerfile copies that JAR.
- `docker compose up -d` creates or starts services from the currently available image and configuration.

After Java code changes, package a new JAR and rebuild the image. After Dockerfile changes, rebuild the image. For Compose-only changes that do not affect the application image, `docker compose up -d` can recreate affected services without Maven packaging. Bind-mounted Nginx or Prometheus configuration changes require the relevant container to reload or restart, not a new application JAR.

### Why do containers not use `localhost` for other services?

Inside a container, `localhost` refers to that same container. Compose provides DNS names for services on its network. The verified service names are `redis`, `app1`, `app2`, `nginx`, `prometheus`, and `grafana`.

Applications connect to `redis`; Nginx forwards to `app1` and `app2`; Prometheus scrapes both apps; Grafana can use `http://prometheus:9090` after manual datasource configuration.

### What does port mapping do?

A mapping such as `8080:80` publishes container port 80 as host port 8080. It is for host access and is not required for service-to-service traffic on the Compose network.

### What are named volumes and bind mounts?

Named volumes are managed by Docker and store persistent container data. This Compose file uses `redis-data` and `grafana-data`.

Bind mounts map a host path into a container. `nginx/nginx.conf` and `prometheus/prometheus.yml` are mounted read-only as configuration.

### Why avoid explicit `container_name`?

Compose normally prefixes resource names with its project name, allowing separate projects to use the same service names without global container-name collisions. Explicit names bypass that namespacing and can conflict.

Named volumes are also project-scoped by default. Two differently named Compose projects do not normally share a volume unless it is explicitly declared external or assigned a shared fixed name.

## Observability

### What does Micrometer do?

Micrometer is the instrumentation facade used by Spring Boot. `RateLimiterMetrics` registers allowed and rejected counters through `MeterRegistry` without coupling application code directly to Prometheus APIs.

### What does Actuator do?

Actuator exposes management endpoints. The current configuration exposes `health`, `info`, and `prometheus`; the Prometheus registry dependency renders scrapeable metrics.

### What does Prometheus do?

Prometheus pulls metrics from both application instances at `/actuator/prometheus` every five seconds. Counters are cumulative per process, so queries typically convert them into rates over a time range.

Example PromQL shape:

```promql
rate(rate_limiter_requests_allowed_total[1m])
```

`rate(...)` estimates the per-second increase over the selected range and handles counter resets.

### What does Grafana do?

Grafana queries a data source such as Prometheus and visualizes query results. The Compose service and persistent Grafana volume exist, but the repository does not provision a datasource or dashboards.

### What are conceptual Azure equivalents?

Depending on a production architecture, teams might use Azure Monitor, Application Insights, Azure Managed Prometheus, or Azure Managed Grafana, and might deploy workloads to AKS. These are conceptual managed equivalents only. None is implemented in this repository.

Backend engineers should still understand metric types, labels, aggregation, scrape behavior, and actionable signals even when a platform team operates the monitoring stack.

## k6 testing

### What is the difference between smoke, load, and stress testing?

- Smoke test: a small, short run that verifies the test and system work at all.
- Load test: exercises an expected traffic profile for a sustained period.
- Stress test: intentionally increases pressure toward or beyond system limits to study degradation and capacity boundaries.

The labels describe test intent and profile, not merely the tool used.

### Why does `sleep(1)` matter?

Without pacing, each virtual user sends another request as soon as the prior one completes, aggressively hammering the endpoint. A one-second sleep spaces each user's iterations and turns the current profile toward paced load. It does not guarantee an exact global request rate because virtual-user scheduling and response time still matter.

### Why are HTTP 200 and 429 both valid?

HTTP 200 means the request was admitted. HTTP 429 means the limiter correctly rejected it. Under restrictive bucket settings, rejection is expected business behavior rather than evidence that the server crashed or returned an unrelated error.

### Why can k6 still report 429 under `http_req_failed`?

k6's HTTP failure metric classifies unsuccessful HTTP status responses independently of application-specific expectations. A 429 can therefore contribute to `http_req_failed` even while the custom check passes.

The current script explicitly validates:

```javascript
r.status === 200 || r.status === 429
```

Interpret the custom check alongside latency, request counts, status distribution, and `http_req_failed`; do not treat one metric alone as the business verdict.

No benchmark numbers are recorded in the repository notes, so these notes do not claim a measured throughput or production capacity. Local-machine results would describe only that machine, configuration, and test profile.

## Current limitations and production gaps

### What should be stated honestly?

- Fixed Window is not currently selectable.
- The in-memory algorithms retain inactive client entries and cannot coordinate across JVMs.
- Redis is a single local service without authentication, replication, clustering, or high availability.
- Redis refill time comes from application clocks.
- The local Nginx forwarding rule is a simplified trusted-proxy assumption.
- Actuator metrics are unauthenticated.
- Grafana datasource and dashboards require manual configuration.
- Prometheus time-series data has no named volume.
- Floating container tags are used.
- Application readiness checks and defined Redis failure behavior are absent.
- Automated algorithm, concurrency, Redis integration, and distributed end-to-end test coverage is limited.
- Kubernetes, AKS, CI/CD, TLS, authentication, alerting, and managed Azure monitoring are not implemented.

These gaps do not invalidate the learning project, but they prevent describing it as production-ready.
