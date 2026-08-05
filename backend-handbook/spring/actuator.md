# Spring Boot Actuator

## Mental model

Actuator exposes operational information about a running Spring Boot application. It is an application-management surface, not a monitoring database or visualization tool.

Common endpoints include:

- `/actuator/health` for health contributors
- `/actuator/info` for configured application metadata
- `/actuator/metrics` for inspecting available meters
- `/actuator/prometheus` for Prometheus-formatted metrics when the registry is present

## Health and readiness

Health can include database, Redis, disk-space, liveness, and readiness signals. A useful readiness check answers whether an instance should receive traffic. A liveness check answers whether the process should be restarted. Conflating them can cause unnecessary restart loops.

## Exposure and security

Having an endpoint available internally does not mean it should be exposed publicly. Health details, environment information, and metrics may reveal system internals. Restrict exposure, access, and detail according to the deployment environment.

## Common mistakes

- Exposing every Actuator endpoint publicly
- Returning sensitive health details to anonymous clients
- Using liveness to test every external dependency
- Assuming Actuator stores metric history
- Forgetting the Prometheus registry dependency when exposing its endpoint

## Revision questions

- What is the difference between Actuator and Prometheus?
- How do liveness and readiness differ?
- Why should management endpoints have a separate security posture?
- What additional component renders Prometheus-formatted metrics?

## Seen in this repository

- [URL Shortener observability](../../url-shortener/backend/url-shortener/docs/10-observability.md)
- [Rate Limiter observability notes](../../rate-limiter/backend/rate_limiter/docs/INTERVIEW_NOTES.md#observability)
