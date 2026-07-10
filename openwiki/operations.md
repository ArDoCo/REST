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

`.github/workflows/openwiki.yml` shows the docs automation, and the project history indicates Maven-based CI. The workflow file also confirms the repo expects a working `openwiki` CLI in automation.

## Build and dependency notes

`pom.xml` shows the operational stack:

- Spring Boot 4.0.6 BOM
- Spring Web, Actuator, Data Redis, and Data JPA starters
- Springdoc OpenAPI
- Jedis as the Redis client
- Testcontainers for integration-style verification

The application class excludes data source auto-configuration, which matches the Redis-backed runtime.

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
- `.github/workflows/openwiki.yml`
