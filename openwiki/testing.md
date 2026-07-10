# Testing

The test tree suggests a mix of controller integration tests, repository tests, utility tests, and architecture rules.

## What is covered

### Controller flows
The controller tests exercise the primary API paths for the different runner families. The filenames show coverage for:

- ArDoCode / sad-code controller flows
- SWATTR / sad-sam flows
- architecture-related controller behavior
- shared controller helpers

These tests are important because the application behavior is mostly about request shaping, runner setup, asynchronous execution, and response status selection.

### Redis and persistence
There are explicit tests for the Redis connection and Redis accessor layer. That is a good signal that Redis behavior is not incidental; it is part of the application contract.

### Utility behavior
Utility tests cover file conversion, hash generation, and trace-link conversion. These are high-value units because request IDs and serialized output are core to the cache and response flow.

### Architecture constraints
`ArchitectureTest` enforces a simple structural rule: classes in this package should not depend directly on Log4j classes, preserving the intended logging setup.

## What to run after changes

When changing controllers, services, result handling, or repositories, prioritize:

- the relevant controller test class
- `RedisAccessorTest`
- `HashGeneratorTest`
- `TraceLinkConverterTest`
- `ArchitectureTest`

For broader changes, run the Maven verification lifecycle referenced by the repo’s CI setup.

## Change-sensitive areas

- Request ID generation: any change affects caching and result reuse.
- Temporary file handling: failures here can leak files or break async cleanup.
- Redis serialization: a mismatch changes how results are retrieved later.
- Response status handling: start, wait, and timeout behavior is part of the public API.

## Source references

- `src/test/java/edu/kit/kastel/mcse/ardoco/tlr/rest/api/controller/AbstractTLRControllerTest.java`
- `src/test/java/edu/kit/kastel/mcse/ardoco/tlr/rest/api/controller/ArDoCoForSadCodeControllerTest.java`
- `src/test/java/edu/kit/kastel/mcse/ardoco/tlr/rest/api/controller/ArDoCoForSadSamCodeControllerTest.java`
- `src/test/java/edu/kit/kastel/mcse/ardoco/tlr/rest/api/controller/ArDoCoForSadSamTLRControllerTest.java`
- `src/test/java/edu/kit/kastel/mcse/ardoco/tlr/rest/api/RedisConnectionTest.java`
- `src/test/java/edu/kit/kastel/mcse/ardoco/tlr/rest/api/RedisRealConnectionTest.java`
- `src/test/java/edu/kit/kastel/mcse/ardoco/tlr/rest/api/repository/RedisAccessorTest.java`
- `src/test/java/edu/kit/kastel/mcse/ardoco/tlr/rest/api/util/FileConverterTest.java`
- `src/test/java/edu/kit/kastel/mcse/ardoco/tlr/rest/api/util/HashGeneratorTest.java`
- `src/test/java/edu/kit/kastel/mcse/ardoco/tlr/rest/api/util/TraceLinkConverterTest.java`
- `src/test/java/edu/kit/kastel/mcse/ardoco/tlr/rest/ArchitectureTest.java`
