# Horizontal Scaling

Definition:
Increase capacity by adding more application instances.

Usually combined with a load balancer.

Benefits
- Higher throughput
- Fault tolerance
- High availability

Difference
Horizontal Scaling: More machines/instances.
Vertical Scaling: Bigger machine.

## Requirements

- Requests can be handled by any healthy instance.
- Correctness-critical state is shared or partitioned deliberately.
- A load balancer routes traffic across instances.
- Shared dependencies have enough capacity and resilience.
- Metrics distinguish instance-level health from system-level health.

Horizontal scaling improves capacity and can improve availability, but only when failure domains are genuinely independent. Multiple application containers on one host do not protect against that host failing.

## Common mistakes

- Keeping required session or counter state in one process
- Scaling only the application tier while ignoring the database
- Assuming more replicas automatically provide high availability
- Using sticky sessions to conceal unintended local state

## Revision questions

- What application property makes horizontal scaling easier?
- Why can a shared database limit horizontal scaling?
- Does running three containers on one machine provide full high availability?
