# Docker Volumes

## Mental model

A container's writable layer follows the container lifecycle. Persistent or host-managed data should be mounted separately.

| Storage | Managed by | Typical use |
| --- | --- | --- |
| Named volume | Docker | Database or tool state |
| Bind mount | Host path | Source code or configuration during local development |
| Container layer | Container | Temporary runtime data |

Removing a container does not normally remove a named volume. `docker compose down -v` does remove the Compose project's named volumes and their data.

## Trade-offs

- Named volumes are portable across containers on one Docker host but less directly visible from the host.
- Bind mounts make files easy to edit but couple the container to a host path and permissions.
- Persistence is not backup: deletion, corruption, and host failure can still destroy data.

## Common mistakes

- Storing a database only in the container writable layer
- Assuming `docker compose down` and `down -v` have the same data effect
- Calling a volume a backup
- Mounting over image content unintentionally
- Using writable mounts when read-only configuration is sufficient

## Revision questions

- How do named volumes and bind mounts differ?
- What survives ordinary container replacement?
- Why is a persistent volume not a backup?
- When should a bind mount be read-only?

## Seen in this repository

- [URL Shortener high-level design](../../url-shortener/backend/url-shortener/docs/04-high-level-design.md)
- [Rate Limiter interview notes](../../rate-limiter/backend/rate_limiter/docs/INTERVIEW_NOTES.md#docker-and-compose)
