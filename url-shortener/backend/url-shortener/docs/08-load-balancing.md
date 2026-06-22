# 08 - Load Balancing

# Introduction

The URL Shortener application uses Nginx as a reverse proxy and load balancer to distribute incoming requests across multiple Spring Boot application instances.

This architecture enables horizontal scaling while keeping the application servers stateless.

---

# Why Load Balancing?

Running a single application instance introduces several limitations:

* Limited request handling capacity
* Single point of failure
* Difficult to scale as traffic increases

Introducing a load balancer allows requests to be distributed across multiple application instances, improving throughput and enabling horizontal scaling.

---

# Current Architecture

```text
                    Client
                       │
                       ▼
                ┌────────────┐
                │   Nginx    │
                │ LoadBalancer│
                └─────┬──────┘
                      │
        ┌─────────────┼─────────────┐
        ▼             ▼             ▼
    Spring Boot   Spring Boot   Spring Boot
       App 1         App 2         App 3
        │              │              │
        └──────────────┼──────────────┘
                       │
          ┌────────────┴────────────┐
          ▼                         ▼
       Redis                  PostgreSQL
```

---

# Components

## Nginx

Nginx is the entry point for all client requests.

Responsibilities:

* Accept incoming HTTP requests
* Forward requests to application instances
* Distribute traffic across backend servers
* Hide backend topology from clients

---

## Application Instances

The application is deployed as three independent Spring Boot containers.

Current instances:

* App 1
* App 2
* App 3

Each instance runs the same application code and shares the same Redis and PostgreSQL instances.

---

# Request Flow

```text
Client
    │
    ▼
Nginx
    │
    ▼
Application Instance
    │
    ▼
Redis / PostgreSQL
    │
    ▼
Response
```

The client communicates only with Nginx.

Application instances are never accessed directly by clients.

---

# Stateless Application Design

Each application instance is stateless.

No application instance stores request-specific or user-specific state locally.

Shared state is maintained in:

* Redis
* PostgreSQL

Because of this design, any request can be processed by any application instance.

---

# Shared Components

All application instances use the same infrastructure components.

| Component  | Shared |
| ---------- | ------ |
| PostgreSQL | Yes    |
| Redis      | Yes    |
| Nginx      | Yes    |

This ensures consistent behavior regardless of which application instance processes the request.

---

# Load Balancing Strategy

The current Nginx configuration distributes requests across all available application instances.

This allows the workload to be shared instead of concentrating all traffic on a single server.

---

# Horizontal Scaling

When traffic increases, additional application instances can be added.

```text
App 1

App 2

App 3

↓

App 4

App 5

App 6
```

Since the application is stateless, scaling only requires:

1. Starting a new application instance.
2. Adding the instance to the Nginx upstream configuration.
3. Reloading Nginx.

No application code changes are required.

---

# Benefits

The current architecture provides:

* Improved request throughput
* Better resource utilization
* Horizontal scalability
* Simplified deployment
* Shared cache and database

---

# Current Limitations

The current implementation does not include:

* Health checks
* Automatic failover
* Dynamic service discovery
* HTTPS termination
* Session affinity
* Multiple Nginx instances

These features can be introduced as the application evolves.

---

# Summary

Nginx acts as the single entry point for the URL Shortener application and distributes incoming requests across multiple stateless Spring Boot instances.

Combined with shared Redis and PostgreSQL services, this architecture supports horizontal scaling while keeping the deployment simple and maintainable.
