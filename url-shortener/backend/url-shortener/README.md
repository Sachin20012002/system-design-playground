# URL Shortener

A simple URL shortening service built with:

- Java 26
- Spring Boot 4
- PostgreSQL
- Docker
- JPA/Hibernate

## Features

- Create short URLs
- Redirect to original URLs
- Random Base62 short code generation
- Collision handling
- Global exception handling

## Run PostgreSQL

```bash
docker run --name postgres-url-shortener \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=url_shortener \
  -p 5432:5432 \
  -d postgres:17