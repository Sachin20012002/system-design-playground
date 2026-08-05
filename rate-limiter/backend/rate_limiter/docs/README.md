# Rate Limiter Documentation

These documents describe the current Rate Limiter implementation, its verified behavior, design decisions, limitations, and possible improvements.

Use the [Backend Engineering Handbook](../../../../backend-handbook/README.md) for reusable explanations of rate limiting, Redis atomicity, stateless scaling, containers, observability, and load testing. Keep concrete algorithms, classes, configuration values, Redis data structures, metrics, and deployment topology in this directory.

## Choose a study mode

### Quick revision — 10 to 15 minutes

1. Recall the selectable algorithms and scope from [requirements](requirements.md).
2. Draw the request and monitoring topology from [high-level design](high-level-design.md).
3. Compare algorithms and explain Redis atomicity from [low-level design](low-level-design.md).
4. Answer the [interview notes](INTERVIEW_NOTES.md) without reading the answers first.

### System-design interview practice — 30 minutes

1. Use the [system-design interview framework](../../../../backend-handbook/guides/system-design-interview-framework.md).
2. Clarify the protected resource, client identity, desired limit, burst policy, and deployment scope.
3. Compare Fixed Window, Sliding Log, Token Bucket, and Leaky Bucket.
4. Explain why multiple application instances require shared state and atomic Redis updates.
5. Cover proxy trust, failure modes, observability, testing, and current production gaps.

### Deep implementation study

1. [Requirements](requirements.md)
2. [High-level design](high-level-design.md)
3. [Low-level design](low-level-design.md)
4. [Project notes](PROJECT_NOTES.md)
5. [Interview notes](INTERVIEW_NOTES.md)
6. [Future improvements](future-improvements.md)

## Fast reference

| Need | Go to |
| --- | --- |
| Current guarantees and boundaries | [Requirements](requirements.md) |
| Deployment and request flow | [High-level design](high-level-design.md) |
| Algorithms, Redis hash, Lua, TTL, and metrics | [Low-level design](low-level-design.md) |
| Milestone decisions | [Project notes](PROJECT_NOTES.md) |
| Active recall and decision defense | [Interview notes](INTERVIEW_NOTES.md) |
| Explicitly unimplemented work | [Future improvements](future-improvements.md) |
