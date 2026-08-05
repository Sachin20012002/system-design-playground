# Load Testing with k6

## Test types

- Smoke test: verifies that the script and system work under minimal load.
- Load test: evaluates expected traffic.
- Stress test: increases traffic beyond expected capacity to find limits.
- Soak test: runs sustained load to expose leaks and gradual degradation.

## Workload design

A useful test defines:

- The request mix, not only one endpoint
- Arrival rate or virtual-user behavior
- Test data and setup
- Valid business outcomes
- Latency and error thresholds
- Environment and hardware context

Virtual users without pacing send another request immediately after each response. Adding sleep introduces think time, but it does not guarantee an exact global request rate.

## Reading results

- Throughput shows completed work per unit of time.
- Average latency can hide a slow tail.
- P95 means 95 percent of observed requests completed at or below that duration.
- HTTP failures and application-level checks answer different questions. An expected response such as `429` may pass a business check while still being classified as an HTTP failure by default.

## Common mistakes

- Publishing benchmark numbers without the test environment
- Testing through a different path than real clients use
- Treating every non-2xx response as unexpected
- Ignoring warm-up, cache state, or generated test data
- Comparing runs with different workloads

## Revision questions

- How do load and stress tests differ?
- Why is P95 often more useful than average latency?
- What does pacing change in a virtual-user test?
- Why can a valid business rejection still appear in an HTTP failure metric?

## Seen in this repository

- [URL Shortener performance testing](../../url-shortener/backend/url-shortener/docs/09-performance-testing.md)
- [Rate Limiter interview notes](../../rate-limiter/backend/rate_limiter/docs/INTERVIEW_NOTES.md#k6-testing)
