# Domains

This repository exposes several closely related trace-link recovery workflows. The business domain is ArDoCo-based recovery and inconsistency detection for software artifacts.

## Trace-link recovery families

The controller layer shows four major API families:

- **ArDoCode / sad-code** — documentation plus code
- **ArCoTL / sam-code** — architecture model plus code
- **SWATTR / sad-sam** — documentation plus architecture model, with inconsistency detection
- **TransArC / sad-sam-code** — documentation, architecture model, and code together

Each family has a dedicated controller and service path so the application can assemble the correct runner and output schema for that workflow.

## Inconsistency detection

The `InconsistencyController` adds a distinct workflow for finding inconsistencies while recovering trace links. It uses the same multipart input style as the other controllers, but it creates an `InconsistencyDetection` runner and returns a combined trace-link/inconsistency result.

This domain is important because it is not just a different response type; it also reflects a different runner setup path and a different request ID prefix.

## Result retrieval and polling

The application distinguishes between:

- starting a pipeline and immediately returning a request ID
- starting a pipeline and waiting for a result
- retrieving a result later by ID
- waiting for a result later by ID

`ResultController` and the shared controller/service helpers support the polling model so clients can decouple submission from retrieval.

## Input conventions

Across the domains, the API consistently expects:

- a `projectName`
- one or more multipart files
- optional `additionalConfigs` JSON
- a model format where architecture models are involved

The application only validates that uploaded files are non-empty; the deeper format checks are left to the ArDoCo tooling itself.

## Naming notes

The repository contains multiple naming conventions that are useful to keep straight:

- **product names**: ArDoCo, ArDoCode, ArCoTL, SWATTR, TransArC
- **trace-link labels**: `SAD_CODE`, `SAM_CODE`, `SAD_SAM`, `SAD_SAM_CODE`
- **runner classes**: `Ardocode`, `Arcotl`, `InconsistencyDetection`

The docs and source both show some naming drift over time, so future changes should prefer the current controller/service names in code and update user-facing docs carefully.

## Source references

- `src/main/java/edu/kit/kastel/mcse/ardoco/tlr/rest/api/controller/ArDoCodeController.java`
- `src/main/java/edu/kit/kastel/mcse/ardoco/tlr/rest/api/controller/ArCoTLController.java`
- `src/main/java/edu/kit/kastel/mcse/ardoco/tlr/rest/api/controller/InconsistencyController.java`
- `src/main/java/edu/kit/kastel/mcse/ardoco/tlr/rest/api/controller/TransArCController.java`
- `src/main/java/edu/kit/kastel/mcse/ardoco/tlr/rest/api/controller/ResultController.java`
- `architecture_decisions.md`
- `README.md`
