# System-Design Interview Framework

Use this sequence to keep an open-ended interview structured. The goal is not to mention every technology; it is to make assumptions explicit and connect requirements to design decisions.

## 1. Clarify the problem

- Identify the primary users and use cases.
- Separate functional requirements from non-functional requirements.
- Confirm what is out of scope.
- Ask which property matters most: latency, availability, consistency, durability, cost, or throughput.

## 2. Estimate scale

Estimate only values that influence the design:

- Average and peak requests per second
- Read/write ratio
- Stored objects and retention period
- Storage and bandwidth
- Expected growth

State assumptions, round numbers, and explain which component each estimate pressures.

## 3. Define the API and data model

- Describe the main operations and failure responses.
- Identify durable entities, keys, relationships, and access patterns.
- Select indexes from query patterns rather than adding them blindly.
- Identify idempotency and uniqueness requirements.

## 4. Draw the high-level design

Start with the shortest end-to-end path:

```text
Client -> Edge or load balancer -> Application -> Data store
```

Then add caches, queues, workers, replicas, or observability only when a requirement justifies them.

## 5. Deep-dive into critical paths

Choose one write path and one read path. Explain:

- State ownership and source of truth
- Concurrency and atomicity
- Cache behavior
- Failure handling and retries
- Consistency guarantees

## 6. Scale and remove bottlenecks

- Keep application instances stateless where practical.
- Scale the constrained tier, not every component automatically.
- Discuss partitioning, replication, caching, asynchronous work, or backpressure when relevant.
- Identify single points of failure and overloaded shared dependencies.

## 7. Cover operations and security

- Metrics, logs, traces, health, and alerts
- Authentication, authorization, abuse prevention, and secrets
- Deployment, readiness, rollback, and capacity signals
- Data backup and recovery

## 8. State trade-offs and evolution

Close with what the design optimizes, what it sacrifices, and what you would change at the next scale threshold. Distinguish the current design from future improvements.

## Strong-answer pattern

For each decision, use:

> Because the requirement is **X**, I choose **Y**. This improves **Z**, but costs **A**. If **B** changes, I would reconsider it.

This is stronger than listing technology names because it exposes your reasoning.

## Common mistakes

- Designing before clarifying requirements
- Using precise estimates built on unstated assumptions
- Adding components without explaining the problem they solve
- Claiming high availability while retaining a single critical instance
- Ignoring failure paths, data correctness, or operational visibility
- Presenting future enhancements as already implemented

## Practice prompts

- Explain the URL Shortener in 15, 30, and 45 minutes.
- Explain how one Rate Limiter algorithm behaves under concurrency.
- Identify the first bottleneck after application instances scale horizontally.
- Remove Redis from either design and explain the consequences.
- Describe how your answer changes at ten times the traffic.
