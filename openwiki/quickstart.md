# OpenWiki quickstart

ArDoCo REST is a Spring Boot REST API that runs ArDoCo trace-link recovery pipelines behind HTTP endpoints. It accepts multipart uploads, converts them to temporary files, starts the relevant runner, stores results in Redis, and exposes Swagger UI plus actuator endpoints for inspection.

Start here if you need to understand the repository quickly:

- [`architecture.md`](architecture.md) — how requests move through controllers, services, and Redis
- [`domains.md`](domains.md) — the supported runner families and business workflows
- [`operations.md`](operations.md) — how to run the app locally and what infrastructure it expects
- [`testing.md`](testing.md) — what the test suites cover and what to run after changes

## What this repository does

The application provides REST access to several ArDoCo trace-link recovery workflows:

- **ArDoCode / sad-code**: textual documentation + code
- **ArCoTL / sam-code**: architecture model + code
- **SWATTR / sad-sam** with inconsistency detection
- **Sad-sam-code / TransArC**: combined architecture, documentation, and code workflow

The main application class is `edu.kit.kastel.mcse.ardoco.tlr.rest.ArDoCoRestApplication`, which is annotated for Spring Boot, async execution, and OpenAPI metadata.

## Repository shape

The codebase is organized around the standard controller → service → repository flow:

- `src/main/java/.../controller/` contains the HTTP entrypoints
- `src/main/java/.../service/` orchestrates runner execution and result lookup
- `src/main/java/.../repository/` wraps Redis access and in-memory tracking for in-flight requests
- `src/main/java/.../converter/` and `.../api_response/` handle file conversion and response shaping
- `src/test/java/.../` contains controller, Redis, utility, and architecture tests

The repository also includes:

- `README.md` for a short manual run note
- `HELP.md` for Spring Boot and Docker hints
- `architecture_decisions.md` for the original architecture rationale and response schema examples
- `.github/workflows/openwiki.yml` for the automated docs update workflow

## How the API behaves

Most endpoints follow the same pattern:

1. Accept multipart inputs and optional JSON configuration.
2. Convert uploaded files to temporary local files.
3. Generate a request ID from the files, project name, and trace-link type.
4. Set up the appropriate ArDoCo runner.
5. Launch the pipeline asynchronously if the result is not already cached.
6. Return either an in-progress response, a ready result, or an accepted/timed-out response depending on the endpoint.

Result data is stored in Redis with a time-to-live configured in the application environment.

## Important conventions

- Request IDs are based on file content and project metadata, not on the optional config JSON.
- Redis is the canonical persistence/cache layer for completed results.
- Temporary files are created during request handling and cleaned up after pipeline execution.
- `/` redirects to Swagger UI.
- The root application exposes OpenAPI metadata for the API documentation.

## If you are changing the code

Use the docs above as the map, then verify changes in the relevant layer:

- API or controller changes: inspect request parameters, ID generation, and response statuses
- Runner/service changes: confirm async behavior, caching, and cleanup paths
- Redis changes: check TTL behavior and the `DatabaseAccessor` abstraction
- Operations changes: update `operations.md` and `HELP.md` if run instructions change
- Tests: see `testing.md` for the relevant suites

## Source references

- `pom.xml`
- `src/main/java/edu/kit/kastel/mcse/ardoco/tlr/rest/ArDoCoRestApplication.java`
- `src/main/java/edu/kit/kastel/mcse/ardoco/tlr/rest/api/controller/AbstractController.java`
- `src/main/java/edu/kit/kastel/mcse/ardoco/tlr/rest/api/controller/ArDoCodeController.java`
- `src/main/java/edu/kit/kastel/mcse/ardoco/tlr/rest/api/controller/ArCoTLController.java`
- `src/main/java/edu/kit/kastel/mcse/ardoco/tlr/rest/api/controller/InconsistencyController.java`
- `src/main/java/edu/kit/kastel/mcse/ardoco/tlr/rest/api/service/AbstractService.java`
- `src/main/java/edu/kit/kastel/mcse/ardoco/tlr/rest/api/service/AbstractRunnerTLRService.java`
- `src/main/java/edu/kit/kastel/mcse/ardoco/tlr/rest/api/repository/RedisAccessor.java`
- `src/test/java/edu/kit/kastel/mcse/ardoco/tlr/rest/ArchitectureTest.java`
