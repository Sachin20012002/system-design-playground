# 03 - API Design

# Introduction

The URL Shortener exposes a minimal REST API consisting of two endpoints:

* Create a short URL
* Redirect to the original URL

The API follows REST principles and uses JSON for request and response payloads.

---

# Base URL

```text
http://localhost
```

---

# API Overview

| Method | Endpoint     | Description                  |
| ------ | ------------ | ---------------------------- |
| POST   | /api/v1/urls | Create a new short URL       |
| GET    | /{shortCode} | Redirect to the original URL |

---

# Create Short URL

Creates a new short URL for the supplied long URL.

## Endpoint

```http
POST /api/v1/urls
```

---

## Request Headers

```http
Content-Type: application/json
```

---

## Request Body

```json
{
  "longUrl": "https://example.com/products/mobile/samsung/s24-ultra"
}
```

---

## Success Response

**Status Code**

```http
200 OK
```

**Response Body**

```json
{
  "shortCode": "B"
}
```

---

## Request Flow

```text
Client
    │
    ▼
POST /api/v1/urls
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
PostgreSQL
    │
    ▼
Return Short Code
```

---

# Redirect URL

Redirects the client to the original long URL.

## Endpoint

```http
GET /{shortCode}
```

Example

```http
GET /B
```

---

## Success Response

**Status Code**

```http
302 Found
```

**Response Headers**

```http
Location: https://example.com/products/mobile/samsung/s24-ultra
```

The browser automatically follows the redirect.

---

## Request Flow

```text
Client
    │
    ▼
GET /B
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
   │   Return URL
   │
   └── Cache Miss
          │
          ▼
     PostgreSQL
          │
          ▼
      Store in Redis
          │
          ▼
      Return URL
```

---

# Error Responses

## Short URL Not Found

If the supplied short code does not exist, the application returns an error response.

Example

```http
GET /invalid
```

Response

```http
404 Not Found
```

---

# DTOs

## CreateShortUrlRequest

```json
{
  "longUrl": "https://example.com"
}
```

---

## CreateShortUrlResponse

```json
{
  "shortCode": "B"
}
```

---

# HTTP Status Codes

|   Status Code | Description                    |
| ------------: | ------------------------------ |
|        200 OK | Short URL created successfully |
|     302 Found | Redirect to original URL       |
| 404 Not Found | Short code does not exist      |

---

# API Versioning

The URL creation endpoint uses URI versioning.

```text
/api/v1/urls
```

This approach allows future API versions to coexist without breaking existing clients.

The redirect endpoint intentionally omits versioning because the shortened URL itself forms the public-facing API.

---

# Design Decisions

The API design follows these principles:

* RESTful endpoints
* JSON request and response payloads
* Stateless communication
* HTTP status codes for outcome representation
* URI versioning for create operations
* Lightweight redirect endpoint optimized for low latency

---

# Summary

The current implementation exposes two REST endpoints that together provide the complete functionality of the URL Shortener service.

The API is intentionally minimal, making it easy to consume while supporting future enhancements through versioning and an extensible layered architecture.
