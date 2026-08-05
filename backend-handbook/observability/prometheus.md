# Prometheus

## Mental model

Prometheus periodically pulls numeric measurements from configured HTTP targets and stores them as time series. A series is identified by a metric name plus its label set.

```text
Application -> metrics endpoint <- Prometheus scrape
Prometheus <- query language -> Grafana or alerts
```

## Core concepts

- Target: an endpoint Prometheus scrapes
- Scrape interval: how frequently it collects samples
- Counter: cumulative total, commonly queried with `rate()`
- Gauge: value that may rise or fall
- Histogram: observations distributed across buckets
- Label: dimension used to filter and aggregate series
- PromQL: query language for rates, aggregations, ratios, and alerts

## Design guidance

- Use counters for events and calculate rates over time.
- Aggregate across application instances when reasoning about system behavior.
- Expect process-local counters to reset after restarts.
- Keep labels bounded; user IDs, request IDs, and raw URLs create excessive cardinality.
- Align scrape interval and query window with how quickly a signal must react.

## Common mistakes

- Reading a cumulative counter as a current rate
- Averaging percentiles produced independently by instances
- Using unbounded label values
- Scraping only one replica of a scaled service
- Assuming collected metrics automatically produce useful alerts or dashboards

## Revision questions

- Why does Prometheus use `rate()` with counters?
- What identifies one time series?
- Why is high cardinality expensive?
- How should metrics from multiple application instances be combined?

## Seen in this repository

- [URL Shortener observability](../../url-shortener/backend/url-shortener/docs/10-observability.md)
- [Rate Limiter monitoring flow](../../rate-limiter/backend/rate_limiter/docs/high-level-design.md#monitoring-flow)
