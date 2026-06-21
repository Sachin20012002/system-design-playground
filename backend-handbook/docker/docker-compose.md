# Docker Compose

Purpose:
Describe and run multi-container applications declaratively.

Core Concepts:
- Service
- Network
- Volume
- Environment
- depends_on

Commands:
docker compose up --build -d
docker compose down
docker compose ps
docker compose logs

Things I Learned:
- Compose automatically creates a network.
- Service names become DNS hostnames.
- depends_on controls startup order, not readiness.
