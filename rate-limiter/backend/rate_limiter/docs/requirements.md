# Requirements

# Functional Requirements

1. The system should limit requests per client.

2. The system should allow a configurable number of requests within a configurable time window.

Example:

```text
5 requests / 60 seconds
```

3. The system should reject requests exceeding the configured limit.

4. The system should return:

```http
429 Too Many Requests
```

5. The system should perform rate limiting before business logic execution.

6. The system should identify clients using IP address.

---

# Non Functional Requirements

## Low Latency

Rate limiting checks should be performed with minimal overhead.

---

## Thread Safety

Concurrent requests from the same client should be handled correctly.

---

## High Throughput

The implementation should support a large number of requests.

---

## Scalability

The current version targets a single application instance.

Future versions will support distributed deployments.

---

## Configurability

Request limits and window duration should be configurable.

---

# Out Of Scope (V1)

- Redis
- Distributed rate limiting
- Authentication based limits
- API Key limits
- Sliding Window
- Token Bucket
- Leaky Bucket
- Monitoring
- Performance testing