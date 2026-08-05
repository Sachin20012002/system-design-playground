# Docker Networks

## Mental model

A container has its own network namespace. On a user-defined network, Docker provides connectivity and DNS-based discovery without requiring fixed container IP addresses.

Common drivers:

- `bridge` for containers on one Docker host
- `host` to share the host network namespace
- `none` for no external networking
- `overlay` for multi-host orchestrated networking

## Addressing rules

- `localhost` inside a container refers to that container.
- Containers use service or container DNS names to reach peers.
- Container-to-container traffic uses the destination container port.
- A host port mapping such as `8080:80` makes container port `80` reachable through host port `8080`.
- Publishing a port is unnecessary when only peer containers need access.

## Security and operations

Place only services that need to communicate on the same network. Do not treat network isolation as a substitute for authentication, authorization, or encryption.

## Common mistakes

- Using `localhost` for another service
- Connecting to a host-mapped port from a peer container
- Depending on dynamic container IP addresses
- Publishing databases or caches to the host unnecessarily
- Assuming membership in one Docker network establishes trust

## Revision questions

- What does `localhost` mean inside a container?
- Why are service names preferable to container IPs?
- When is a host port mapping required?
- What security property does a separate network provide—and not provide?

## Seen in this repository

- [URL Shortener load balancing](../../url-shortener/backend/url-shortener/docs/08-load-balancing.md)
- [Rate Limiter high-level design](../../rate-limiter/backend/rate_limiter/docs/high-level-design.md)
