# Nginx

Roles
- Reverse Proxy
- Load Balancer
- Static File Server

Load Balancing Algorithms
- Round Robin
- Least Connections
- IP Hash

Benefits
- Better scalability
- SSL termination
- Central entry point

## Reverse proxy flow

A client connects to Nginx, and Nginx forwards the request to an upstream application. The application normally sees Nginx as its direct network peer unless proxy metadata is forwarded and trusted correctly.

Common forwarded headers include:

- `X-Forwarded-For` for the client/proxy chain
- `X-Forwarded-Proto` for the original scheme
- `Host` or `X-Forwarded-Host` for the requested host

Only trust these headers from known proxies. Accepting client-supplied forwarding headers directly enables identity spoofing, which is especially dangerous for IP-based rate limiting.

## Operational trade-offs

- Nginx creates one entry point but can become a single point of failure.
- Round Robin distributes requests, not equal amounts of work.
- Health-aware routing requires appropriate checks and failure settings.
- Load balancing the application tier does not scale its shared database or cache.

## Revision questions

- How does a reverse proxy differ from a forward proxy?
- Why must forwarded client identity be trusted selectively?
- Why can evenly distributed request counts still create uneven load?
- What shared dependency can bottleneck after application scaling?

## Seen in this repository

- [URL Shortener load balancing](../../url-shortener/backend/url-shortener/docs/08-load-balancing.md)
- [Rate Limiter high-level design](../../rate-limiter/backend/rate_limiter/docs/high-level-design.md)
