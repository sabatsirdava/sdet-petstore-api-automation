# Petstore API Automation

Small, maintainable API automation framework for the Swagger Petstore `Pet`
resource. The project uses Java, Maven, RestAssured, Cucumber/Gherkin, and
TestNG. It intentionally keeps the framework simple: Gherkin describes
behavior, step definitions coordinate business actions and assertions, and
`PetApiClient` is the only class that talks directly to RestAssured.

## Tech stack

* Java 17+
* Maven 3.9+
* RestAssured 5.5.0
* Cucumber 7.18.1
* TestNG 7.10.2
* SLF4J/Logback for lightweight logging

## Prerequisites

Install a JDK 17 or newer and Maven 3.9 or newer. The API tests require
internet access to `https://petstore.swagger.io`.

## Project structure

```text
src/
├── main/
│   ├── java/com/sdet/api/
│   │   ├── client/PetApiClient.java
│   │   ├── config/ApiConfig.java
│   │   └── model/Pet.java
│   └── resources/config/config.properties
└── test/
    ├── java/com/sdet/api/
    │   ├── hooks/Hooks.java
    │   ├── runners/TestRunner.java
    │   ├── steps/PetSteps.java
    │   └── testdata/TestData.java
    └── resources/features/pet_crud.feature
```

## Setup

1. Open the project folder in IntelliJ IDEA.
2. Select the installed JDK as the Project SDK.
3. Let IntelliJ import `pom.xml` as a Maven project.
4. Run `mvn clean test` from the project root.

## Configuration

The default base URL is stored in
`src/main/resources/config/config.properties`:

```properties
baseUrl=https://petstore.swagger.io/v2
```

It can be overridden without editing the repository:

```bash
mvn clean test -DbaseUrl=https://petstore.swagger.io/v2
```

The `API_BASE_URL` environment variable is also supported. A JVM system
property (`-DbaseUrl`) takes precedence over the environment variable, which
takes precedence over the properties file.

No secrets are required by this public API.

## Running tests

Run the complete suite:

```bash
mvn clean test
```

Run from IntelliJ by opening
`src/test/java/com/sdet/api/runners/TestRunner.java` and selecting the TestNG
run action.

## Reporting

Every Maven test run creates:

* `target/cucumber-reports/cucumber.html`
* `target/cucumber-reports/cucumber.json`
* `target/surefire-reports/` with TestNG/Maven execution details

Generated output is ignored by Git.

## Test coverage

The feature file contains 13 scenarios covering:

* Create, read, update, delete, and read-after-delete for a Pet
* Read and delete using never-used IDs
* Find pets by status
* Malformed JSON on create
* Invalid/non-numeric IDs
* Missing required `name` field
* Updating a never-used ID
* An unsupported status filter value

The final four scenarios are tagged `@known-issue` and act as
characterization tests for deviations observed in the live Petstore
deployment.

The probe used for this assignment showed that the live API:

* accepts a create request without `name`
* upserts a Pet when updating an unknown ID
* returns a JSON `404` for a non-numeric ID
* returns `200 []` for an unsupported status filter

These tests document the observed live behavior rather than assuming that the
API behaves more strictly than it currently does.

## Known API limitations/issues

Swagger Petstore is a shared public demo service. Its data is mutable and its
behavior may change over time. It is not a suitable system for
production-grade reliability testing.

The suite uses unique numeric IDs and cleans up data it creates. It
deliberately does not add retries or polling: a retry could hide a real API
defect.

The observed behavior below is based on the live API probe performed during
framework design:

| Operation                  | Observed behavior                                    |
| -------------------------- | ---------------------------------------------------- |
| Create valid Pet           | `200` with JSON Pet                                  |
| Read existing Pet          | `200` with JSON Pet                                  |
| Update existing Pet        | `200` with JSON Pet                                  |
| Delete existing Pet        | `200` with JSON confirmation                         |
| Read missing Pet           | `404` with JSON error                                |
| Delete missing Pet         | `404` with an empty body                             |
| Malformed JSON             | `400` with JSON error                                |
| Create without `name`      | `200`, field remains absent                          |
| Update missing ID          | `200`, resource is created                           |
| Read non-numeric ID        | `404`, JSON error containing `NumberFormatException` |
| Find by unsupported status | `200` with an empty JSON array                       |

## Troubleshooting

* **`UnknownHostException` or connection failures:** check internet access and
  confirm the Petstore URL is reachable.
* **A missing-pet test finds data:** the chosen ID was not unique on the live
  service; rerun the suite, or use a different numeric range in
  `TestData`.
* **A characterization test fails after a Petstore deployment change:** check
  the response against the current service before changing the framework.
  These tests intentionally capture live behavior.
* **IntelliJ does not show TestNG:** re-import the Maven project and ensure
  test dependencies have been downloaded.
