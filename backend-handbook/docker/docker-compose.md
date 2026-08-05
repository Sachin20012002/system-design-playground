# Docker Compose

Purpose:
Describe and run multi-container applications declaratively.

Core Concepts:
- Service
- Network
- Volume
- Environment
- depends_on

Commands:
docker compose up --build -d
docker compose down
docker compose ps
docker compose logs

Things I Learned:
- Compose automatically creates a network.
- Service names become DNS hostnames.
- depends_on controls startup order, not readiness.

## Startup order versus readiness

Starting a database container does not mean the database is ready to accept connections. A health check provides a readiness signal, and long-form `depends_on` can wait for `service_healthy`. Applications should still tolerate dependency restarts and temporary failures after startup.

## Service discovery

Containers on the Compose network connect through service names and container ports. Host port mappings are for access from the host; they are not required for container-to-container traffic.

## Project names and scaling

Compose prefixes resources with a project name. Avoiding unnecessary fixed `container_name` values preserves namespacing and makes it easier to run multiple stacks or scale services.

## Common mistakes

- Using `localhost` to reach another container
- Confusing host ports with container ports
- Assuming short-form `depends_on` waits for readiness
- Baking environment-specific secrets into the Compose file
- Treating a named volume as a backup

## Revision questions

- What does Compose create automatically?
- Why can startup order still produce connection failures?
- Which port should one service use to contact another service?
- Why can explicit container names make reuse harder?

## Seen in this repository

- [URL Shortener deployment documentation](../../url-shortener/backend/url-shortener/docs/04-high-level-design.md)
- [Rate Limiter high-level design](../../rate-limiter/backend/rate_limiter/docs/high-level-design.md)
