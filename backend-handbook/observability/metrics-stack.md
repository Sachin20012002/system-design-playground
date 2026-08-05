# Application Metrics Stack

## Responsibilities

- Application instrumentation records measurements.
- Micrometer provides a vendor-neutral Java instrumentation API.
- Spring Boot Actuator publishes operational endpoints.
- A Prometheus registry renders metrics in Prometheus format.
- Prometheus scrapes and stores time-series data.
- Grafana queries a datasource such as Prometheus and visualizes results.

Starting Grafana does not automatically configure a datasource or dashboards. Provisioning requires configuration files or manual setup.

## Useful metric types

- Counter: a total that only increases, such as accepted requests
- Gauge: a current value, such as queue depth
- Timer or histogram: duration distributions used for percentiles

## Design guidance

- Name metrics around stable domain events.
- Record both sides of a decision when a ratio matters.
- Avoid high-cardinality labels such as raw URLs, user IDs, or request IDs.
- Distinguish instrumentation from visualization: collecting a metric does not create a dashboard.
- Secure operational endpoints appropriately outside isolated learning environments.

## Revision questions

- What is the responsibility of Micrometer versus Prometheus?
- Why is Grafana not the source of metrics?
- When should a counter, gauge, or timer be used?
- Why are high-cardinality labels dangerous?

## Seen in this repository

- [URL Shortener observability](../../url-shortener/backend/url-shortener/docs/10-observability.md)
- [Rate Limiter low-level metrics design](../../rate-limiter/backend/rate_limiter/docs/low-level-design.md#metrics)
