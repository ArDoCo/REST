---
type: "Reference"
title: "Architecture"
openwiki_generated: true
---

# Architecture

ArDoCo REST is a layered Spring Boot application built around a consistent controller → service → repository flow.

## Layering

### Controllers
Controllers are the HTTP boundary. They accept multipart uploads, optional configuration JSON, and request metadata, then hand the work off to the service layer.

The main controller patterns are:

- convert uploaded `MultipartFile` values to local `File` objects
- parse optional `additionalConfigs` JSON into a sorted map
- generate a request ID from file content and project name
- set up the correct ArDoCo runner
- call the shared controller helpers for “start” and “start-and-wait” behavior

`AbstractController` contains the shared response flow, including request ID generation and JSON config parsing.

### Services
`AbstractService` owns the shared lookup logic for results already stored in Redis.

`AbstractRunnerTLRService` adds the orchestration behavior for runner-based pipelines:

- start the runner asynchronously when no cached result exists
- track in-flight requests in `CurrentlyRunningRequestsRepository`
- store completed results through `DatabaseAccessor`
- remove in-flight bookkeeping and delete temporary files in a `finally` block

Specialized services such as `ArDoCodeService`, `ArCoTLService`, `InconsistencyService`, `SwattrService`, and `TransArCService` handle the runner-specific conversion of ArDoCo results into the JSON payload stored in Redis.

### Repositories
The persistence boundary is abstracted through `DatabaseAccessor`.

`RedisAccessor` is the concrete repository used in this codebase. It writes stringified results into Redis with a TTL taken from configuration and provides basic key lookup and deletion.

### Shared API model
Responses are normalized through `ArdocoResultResponse` and `ArDoCoApiResult`. The documented response shape carries:

- `requestId`
- HTTP status
- a human-readable message
- `traceLinkType`
- the result payload or error payload

## Request lifecycle

A typical “start pipeline” request moves through these steps:

1. HTTP controller receives the multipart request.
2. Files are converted to temporary local files.
3. The request ID is generated from file content plus project metadata.
4. The controller creates the correct runner instance and its temp output directory.
5. The service checks Redis and the in-flight request registry.
6. If no result exists yet, the runner is executed asynchronously.
7. When the runner finishes, the service converts the result to JSON and stores it in Redis.
8. The in-flight entry is removed and temporary input files are deleted.

The “start-and-wait” endpoints use the same setup, then wait for a result for a bounded time before returning `202 Accepted` if the result is still unavailable.

## Request IDs and caching

Request IDs are generated from the file list and project name, then prefixed by the trace-link type. The architecture notes and controller code show that the hash is content-based and intended to allow cache reuse for repeated requests with the same inputs.

The docs and code also show an important constraint: the optional `additionalConfigs` JSON is parsed and passed to the runner, but it is not part of the request hash.

## Operational behavior

- `/` redirects to Swagger UI.
- OpenAPI metadata is declared on the main application class.
- Async execution is enabled at the application level.
- DataSource auto-configuration is excluded, which matches the Redis-backed architecture.

## Key source files

- `src/main/java/edu/kit/kastel/mcse/ardoco/tlr/rest/ArDoCoRestApplication.java`
- `src/main/java/edu/kit/kastel/mcse/ardoco/tlr/rest/api/controller/AbstractController.java`
- `src/main/java/edu/kit/kastel/mcse/ardoco/tlr/rest/api/controller/HomeController.java`
- `src/main/java/edu/kit/kastel/mcse/ardoco/tlr/rest/api/service/AbstractService.java`
- `src/main/java/edu/kit/kastel/mcse/ardoco/tlr/rest/api/service/AbstractRunnerTLRService.java`
- `src/main/java/edu/kit/kastel/mcse/ardoco/tlr/rest/api/repository/RedisAccessor.java`
- `architecture_decisions.md`
