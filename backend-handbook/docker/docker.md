# Docker

## What problem does it solve?
Docker packages applications and their dependencies into portable containers that behave consistently across environments.

## Core Concepts
- Image
- Container
- Dockerfile
- Registry
- Layers

## Best Practices
- Use official images.
- Keep images small.
- Build immutable images.
- Keep secrets outside images.

## Common Mistakes
- Treating containers like virtual machines.
- Storing persistent data inside containers.

## Commands
docker build
docker run
docker ps
docker logs
docker exec

## Things I Learned
- Images are blueprints.
- Containers are running instances.
- Containers are isolated by default.
- The image should contain the application artifact; durable data belongs in mounted storage.
- Container isolation does not remove the need for application security.

## Revision Questions
- Image vs Container?
- Why Docker instead of installing software directly?
- Which data should survive container replacement?
- Why should one container normally run one primary process?

## Seen in this repository

- [URL Shortener deployment](../../url-shortener/backend/url-shortener/docs/04-high-level-design.md)
- [Rate Limiter Docker and Compose notes](../../rate-limiter/backend/rate_limiter/docs/INTERVIEW_NOTES.md#docker-and-compose)
