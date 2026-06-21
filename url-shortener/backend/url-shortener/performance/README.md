# Performance Tests

## Prerequisites

- Docker Compose is running.
- Nginx is available at http://localhost.
- Node.js 18+ installed.

## Generate Seed Data

```bash
node performance/seed-data.mjs
```

This generates:

```
generated-shortcodes.json
```

## Run Redirect Test

```bash
docker run --rm -i -v "${PWD}\performance:/scripts" grafana/k6 run /scripts/redirect-load-test.js
```

## Run Create Test

```bash
docker run --rm -i -v "${PWD}\performance:/scripts" grafana/k6 run /scripts/create-load-test.js
```

## Run Mixed Workload

```bash
docker run --rm -i -v "${PWD}\performance:/scripts" grafana/k6 run /scripts/mixed-workload.js
```

## Workload Distribution

- 95% Redirect Requests
- 5% Create Requests

This approximates a typical URL Shortener production workload.