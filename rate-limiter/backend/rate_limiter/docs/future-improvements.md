# Future Improvements

The following items are possible follow-up work. They are not implemented features or commitments.

## Correctness and testing

- Add unit tests for every currently selectable algorithm and boundary condition.
- Add concurrent-request tests for the in-memory strategies.
- Add Redis integration tests covering Lua admission behavior and TTL refresh.
- Add end-to-end tests through Nginx with two application instances.
- Decide whether Fixed Window should be restored as a selectable strategy or remain historical only.

## Operations and resilience

- Add application health checks and readiness-aware service startup.
- Define Redis failure behavior and recovery expectations.
- Evaluate Redis replication or clustering if the project expands beyond local demonstrations.
- Persist Prometheus time-series data when benchmark history needs to survive container replacement.
- Provision the Grafana datasource and dashboards from repository-controlled files.
- Add alerting rules only after actionable service-level signals are defined.

## Security

- Restrict or authenticate Actuator endpoints.
- Add Redis authentication and encrypted transport for non-local deployments.
- Replace the simplified local proxy-header assumption with an explicit trusted-proxy policy before deployment behind arbitrary proxies.
- Add identity-based limits if authentication is introduced.

## Rate-limiter behavior

- Add inactive-client cleanup for the in-memory maps.
- Define configuration validation for non-positive limits, capacities, windows, and refill rates.
- Evaluate server-side time for distributed token refill if application-host clock differences become relevant.
- Consider response headers such as retry guidance after their semantics are defined for every algorithm.

## Delivery

- Pin and maintain container image versions instead of relying on floating tags.
- Add CI checks for Maven tests, Compose validation, and documentation links.
- Consider an orchestrated deployment only if the learning scope expands beyond Docker Compose.
