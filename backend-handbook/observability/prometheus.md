# Prometheus

Purpose:
Collect time-series metrics by scraping HTTP endpoints.

Key Concepts
- Pull model
- Time-series database
- Targets
- Metrics

Spring Boot exposes metrics through:
/actuator/prometheus

Prometheus periodically scrapes this endpoint.
