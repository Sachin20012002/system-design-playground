# 05 - Low Level Design

# Introduction

The Low Level Design (LLD) describes the internal structure of the URL Shortener application.

Unlike the High Level Design, which focuses on the interaction between major system components, the Low Level Design explains how the application is organized internally, how requests are processed, and how responsibilities are distributed across different layers.

The application follows a layered architecture that separates request handling, business logic, persistence, and configuration.

---

# Project Structure

```text
src/main/java/com/sachin/url_shortener/

├── config/
├── controller/
├── dto/
├── entity/
├── exception/
├── repository/
├── service/
└── UrlShortenerApplication.java
```

Each package has a well-defined responsibility.

---

# Package Responsibilities

| Package    | Responsibility                               |
| ---------- | -------------------------------------------- |
| config     | Application configuration and custom metrics |
| controller | REST API endpoints                           |
| dto        | Request and response models                  |
| entity     | Database entities                            |
| exception  | Custom exceptions and exception handling     |
| repository | Database access layer                        |
| service    | Business logic                               |

---

# Layered Architecture

```text
                HTTP Request
                     │
                     ▼
              Controller Layer
                     │
                     ▼
               Service Layer
              ┌─────────────┐
              │             │
              ▼             ▼
           Redis      Repository
                            │
                            ▼
                      PostgreSQL
```

Each layer communicates only with the layer directly below it.

---

# Controller Layer

The controller exposes the application's REST endpoints.

Responsibilities:

* Accept HTTP requests
* Validate request payloads
* Delegate processing to the service layer
* Return HTTP responses

Current endpoints:

```text
POST /api/v1/urls

GET /{shortCode}
```

The controller contains no business logic.

---

# Service Layer

The service layer contains the core business logic.

Responsibilities:

* Generate unique short URLs
* Generate distributed IDs using Redis
* Encode Base62 short codes
* Retrieve URL mappings
* Implement Cache-Aside pattern
* Interact with PostgreSQL
* Record cache metrics

The service acts as the central coordination layer of the application.

---

# Repository Layer

The repository layer provides access to PostgreSQL using Spring Data JPA.

Current repository:

```text
UrlMappingRepository
```

Responsibilities:

* Save URL mappings
* Retrieve URL mappings by short code

The application relies on Spring Data JPA to generate SQL queries automatically.

---

# Entity Layer

The current implementation contains a single entity.

```text
UrlMapping
```

Fields:

| Field     | Description                 |
| --------- | --------------------------- |
| id        | Primary key                 |
| shortCode | Generated Base62 identifier |
| longUrl   | Original URL                |

The entity is mapped to the `url_mapping` table.

---

# DTO Layer

The application exposes two DTOs.

## CreateShortUrlRequest

Used when creating a new short URL.

```json
{
  "longUrl": "https://example.com"
}
```

---

## CreateShortUrlResponse

Returned after successful URL creation.

```json
{
  "shortCode": "B"
}
```

---

# Exception Handling

The application defines custom exceptions to provide meaningful error responses.

Current exception:

```text
ShortCodeNotFoundException
```

This exception is thrown when a requested short code does not exist.

A global exception handler converts exceptions into HTTP responses.

---

# Create URL Flow

```text
Client
    │
    ▼
Controller
    │
    ▼
Service
    │
    ▼
Redis INCR
    │
    ▼
Base62 Encoding
    │
    ▼
Repository
    │
    ▼
PostgreSQL
    │
    ▼
Return Short Code
```

---

# Redirect Flow

```text
Client
    │
    ▼
Controller
    │
    ▼
Service
    │
    ▼
Redis
   │
   ├── Cache Hit
   │      │
   │      ▼
   │   Return Long URL
   │
   └── Cache Miss
          │
          ▼
     Repository
          │
          ▼
     PostgreSQL
          │
          ▼
      Update Redis
          │
          ▼
     Return Long URL
```

---

# Design Principles

The implementation follows several design principles.

## Separation of Concerns

Each layer has a single responsibility.

Business logic remains isolated from HTTP handling and database access.

---

## Dependency Injection

Spring Boot manages application dependencies through constructor injection.

This improves testability and reduces coupling between components.

---

## Stateless Services

The service layer maintains no request-specific state.

Any application instance can process any request.

---

## Reusability

Business logic is centralized within the service layer.

Controllers remain lightweight and reusable.

---

# Current Limitations

The current implementation does not include:

* Service interfaces
* Asynchronous processing
* Transaction management
* Validation annotations
* Authentication
* Authorization

These may be introduced as the application evolves.

---

# Summary

The application follows a simple layered architecture consisting of controllers, services, repositories, entities, DTOs, and configuration classes.

This separation keeps the codebase modular, maintainable, and easy to extend while supporting the scalability and deployment model described in the High Level Design.
