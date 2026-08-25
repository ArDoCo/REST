---
type: "Reference"
title: "Operations"
openwiki_generated: true
---

# Operations

## Local setup

The repository README and help notes indicate the expected local flow:

1. Fill in Redis credentials in `redis/redis_template.env` and rename it to `redis/redis.env`.
2. Start the infrastructure with Docker Compose.
3. Start the Spring Boot application.
4. Use Swagger UI to exercise the API.
5. Use Redis Insight to inspect stored results.

## Key runtime endpoints

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Actuator health: `http://localhost:8080/actuator/health`
- Redis Insight: `http://localhost:5540`

The root path `/` redirects to Swagger UI.

## Docker and infrastructure

The repository includes:

- `docker-compose-template.yaml`
- `Dockerfile`
- `redis/`
- `redisinsight/`

`HELP.md` explicitly documents the Docker Compose workflow for Redis and Redis Insight, which makes this repository a service that depends on external infrastructure rather than a self-contained binary.

## CI and automation

The repository runs Maven-based CI through `.github/workflows/ci.yml`, which builds with Java 21 and `mvn verify` against a Redis service container. `.github/workflows/docker.yml` publishes the container image through the shared reusable workflow in `ardoco/actions`.

`.github/workflows/openwiki.yml` drives the automated documentation update. It no longer contains the update logic inline; instead it delegates to the shared reusable workflow `ardoco/actions/.github/workflows/openwiki.yml@main`, which performs checkout, change detection, the `openwiki` CLI run, and pull-request creation. The schedule runs weekly, Monday 06:00 UTC, and can also be triggered manually via `workflow_dispatch`.

## Build and dependency notes

`pom.xml` shows the operational stack:

- Spring Boot 4.0.6 BOM (imported in `dependencyManagement`)
- Spring Web, Actuator, Data Redis, and Data JPA starters
- Springdoc OpenAPI
- Jedis as the Redis client
- Testcontainers for integration-style verification

The application class excludes data source auto-configuration, which matches the Redis-backed runtime.

The parent POM is `io.github.ardoco:parent` at `2.1.0-SNAPSHOT`. Because Spring Boot 4.0.6 pulls JUnit 6.0.3, which the inherited parent POM does not provide, `pom.xml` overrides `<junit.version>` to `6.0.3` and declares explicit `junit-jupiter-api`, `junit-jupiter-engine`, and `junit-jupiter-params` dependencies pinned to `${junit.version}` so the Spring Boot-managed JUnit versions win.

## Change watch-outs

- If Redis TTL semantics change, update the repository implementation and any runbook notes together.
- If the API paths or ports change, update README, HELP, and OpenWiki together.
- If the application starts persisting more than Redis results, the operational docs should explain the new storage boundary.

## Source references

- `README.md`
- `HELP.md`
- `pom.xml`
- `docker-compose-template.yaml`
- `Dockerfile`
- `.github/workflows/ci.yml`
- `.github/workflows/docker.yml`
- `.github/workflows/openwiki.yml`
