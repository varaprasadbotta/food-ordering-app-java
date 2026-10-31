# Food Delivery System

A Swiggy-like food delivery backend built using Java.

## Tech Stack

- Java 21
- Maven
- PostgreSQL (plain JDBC + Flyway migrations)
- JUnit 5 + Testcontainers
- Spring Boot (planned)

## Architecture

Modular monolith with a layered design:

- Controller Layer (planned)
- Service Layer
- Repository Layer
- Domain Model

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for components, design patterns and the database schema.

## Running locally

Requires Docker.

```bash
# Start PostgreSQL
docker compose up -d

# Run all tests (repository tests start their own PostgreSQL via Testcontainers)
mvn clean test
```

Database settings are in `src/main/resources/application.properties` and can be overridden
with the `DB_URL`, `DB_USERNAME` and `DB_PASSWORD` environment variables.

## Project Goals

- Apply Object-Oriented Programming principles.
- Understand Low-Level Design.
- Implement suitable design patterns.
- Build a maintainable backend application.
