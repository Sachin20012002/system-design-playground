# Dockerfile

## Mental model

A Dockerfile is a reproducible recipe for building an image. Each build instruction contributes to image metadata or a filesystem layer.

Common instructions:

- `FROM` selects a base image.
- `WORKDIR` sets the working directory.
- `COPY` adds build-context files.
- `RUN` executes build-time commands.
- `ENV` defines image environment defaults.
- `EXPOSE` documents a listening port; it does not publish it.
- `ENTRYPOINT` and `CMD` define process startup behavior.

## Build strategy

For a prepackaged Java application, build the JAR first and copy only the artifact into a runtime image. A multi-stage build can instead compile inside a builder stage and copy the artifact into a smaller runtime stage.

Order stable steps before frequently changing steps to improve layer-cache reuse. Keep the build context small with `.dockerignore`.

## Common mistakes

- Copying source, build caches, or secrets into the runtime image
- Using a full development image when only a runtime is needed
- Assuming `EXPOSE` publishes a host port
- Running multiple unrelated long-lived processes in one container
- Using floating base-image tags without an update policy

## Revision questions

- How do build-time and runtime concerns differ?
- What does `EXPOSE` actually do?
- Why can instruction order affect build time?
- What benefit does a multi-stage build provide?

## Seen in this repository

- [URL Shortener Dockerfile](../../url-shortener/backend/url-shortener/Dockerfile)
- [Rate Limiter Dockerfile](../../rate-limiter/backend/rate_limiter/Dockerfile)
