# URL Shortener

A URL shortening service built while learning backend engineering, distributed systems, and scalability concepts.

## Tech Stack

* Java 26
* Spring Boot 4
* PostgreSQL 17
* Redis 8
* Docker
* Nginx
* Spring Data JPA / Hibernate

## Features

### V1

* Create short URLs
* Redirect to original URLs
* PostgreSQL persistence
* Global exception handling

### V2

* Random Base62 short code generation
* Collision detection and retry mechanism

### V3

* Redis caching for URL lookups
* Redis-based global ID generation

### V4

* Dockerized application
* Multiple Spring Boot instances
* Nginx load balancing
* Shared PostgreSQL database
* Shared Redis cache

## Architecture

```
                Nginx
                  |
        -------------------
        |        |        |
      App1     App2     App3
        \        |       /
         \       |      /
              Redis
                |
           PostgreSQL
```

## Running PostgreSQL

```bash
docker run --name postgres-url-shortener \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=url_shortener \
  -p 5433:5432 \
  -d postgres:17
```

## Running Redis

```bash
docker run --name redis-url-shortener \
  -p 6379:6379 \
  -d redis:8
```

## Building the Application

```bash
mvn package
```

## Building Docker Image

```bash
docker build -t url-shortener:v4 .
```

## Running the Application

```bash
docker run -d \
  --name url-shortener-1 \
  --network url-shortener-network \
  -e SPRING_PROFILES_ACTIVE=docker \
  -p 8080:8080 \
  url-shortener:v4
```

## Learning Goals

This project is being evolved incrementally to learn:

* API design
* Database design
* Caching strategies
* Load balancing
* Horizontal scaling
* Distributed systems concepts
* Containerization
