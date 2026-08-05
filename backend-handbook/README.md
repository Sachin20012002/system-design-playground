# Backend Engineering Handbook

This handbook contains reusable backend concepts learned and verified while building the projects in this repository. Project documentation explains the concrete implementation; the handbook explains the underlying idea and its trade-offs.

## How to use it

Choose a mode before reading:

- **Learn:** read the mental model, flow, and trade-offs; then follow a project example.
- **Refer:** use the topic index and headings to find one detail without rereading everything.
- **Revise:** answer the revision questions without looking, then check the relevant project interview notes.

For system-design interviews, start with the [interview framework](guides/system-design-interview-framework.md), select a project, and practice explaining requirements, estimates, APIs, data, scaling, failures, and trade-offs in that order.

Topics use the sections that fit the subject rather than forcing every page into an identical template. A useful concept page normally explains the problem, model, trade-offs, common mistakes, and revision questions.

## Topics

### Learning and interview guides

- [System-design interview framework](guides/system-design-interview-framework.md)

### Architecture

- [Horizontal scaling](architecture/horizontal-scaling.md)
- [Stateless services and shared state](architecture/stateless-services-and-shared-state.md)

### Docker

- [Docker](docker/docker.md)
- [Dockerfile](docker/dockerfile.md)
- [Docker Compose](docker/docker-compose.md)
- [Networks](docker/docker-network.md)
- [Volumes](docker/docker-volumes.md)

### Redis

- [Redis](redis/redis.md)
- [Cache-Aside](redis/cache-aside.md)
- [Atomicity and Lua](redis/atomicity-and-lua.md)

### Traffic management

- [Nginx](nginx/nginx.md)
- [Rate limiting](rate-limiting/rate-limiting.md)

### Observability and performance

- [Metrics stack](observability/metrics-stack.md)
- [Prometheus](observability/prometheus.md)
- [Spring Boot Actuator](spring/actuator.md)
- [Load testing with k6](performance/load-testing-with-k6.md)

## Documentation boundary

Keep endpoints, ports, class names, exact configuration, metric names, benchmark results, and current limitations in project documentation. Add a handbook topic only when the lesson applies beyond one implementation.

## Recommended revision loop

1. Explain a topic aloud from memory in two minutes.
2. Draw its request or data flow without notes.
3. State one benefit, two trade-offs, and one failure mode.
4. Connect it to a concrete decision in URL Shortener or Rate Limiter.
5. Revisit only the gaps in your explanation.
