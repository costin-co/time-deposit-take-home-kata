# Time Deposit Refactoring Kata - Take-Home Assignment

## Implementation and Running Instructions

### Overview

This implementation provides two business operations for time deposits: retrieve all deposits with their withdrawals, and update and persist the balances of all deposits using the existing monthly interest calculator. Withdrawals are included in responses but do not reduce balances.

### Prerequisites

- Java 17 or newer.
- Maven installed (`mvn` command).
- A browser to open Swagger UI.

H2 runs embedded in the application; no separate database server is required.

### Project structure

This repository-root `README.md` contains the assignment and implementation instructions. The Spring Boot Maven project is in `java/`; its application source, tests, Maven configuration, H2 settings, and optional demo SQL script are under that directory.

### Running the application

From the repository root:

```bash
cd java
mvn spring-boot:run
```

The application starts on the default Spring Boot HTTP port, `8080`.

### Database

The application uses a persistent, file-based H2 database configured by `java/src/main/resources/application.properties`. The JDBC URL is `jdbc:h2:file:./data/time-deposit-db;DB_CLOSE_ON_EXIT=FALSE`; because the application is run from `java/`, the database files are stored under `java/data/`. Hibernate uses spring.jpa.hibernate.ddl-auto=update to create missing database objects and apply supported schema updates while preserving existing application data under normal operation.

`java/demo-data.sql` is optional and is not configured to run at application startup. It contains insert-only demonstration data. Its `NOT EXISTS` checks avoid adding rows matching the sample plan/day or withdrawal/deposit/amount/date values; it does not reset or update existing balances. Use a fresh demo database to see all six examples at their initial balances.

### Loading demonstration data

First, from the repository root, start the application once so Hibernate creates the schema:

```bash
cd java
mvn spring-boot:run
```

Wait for the application to start, then stop it gracefully with Ctrl-C. The application must be stopped before opening its file database with H2 `RunScript`, since an active connection can lock the database file. From the same `java/` directory, load the script using the H2 version resolved by this project's Maven configuration (2.2.224):

```bash
java -cp "$HOME/.m2/repository/com/h2database/h2/2.2.224/h2-2.2.224.jar" \
  org.h2.tools.RunScript \
  -url 'jdbc:h2:file:./data/time-deposit-db;DB_CLOSE_ON_EXIT=FALSE' \
  -user sa -password '' \
  -script demo-data.sql
```
If Maven uses a custom local repository, adjust the H2 JAR path accordingly.
Wait for the command to finish, then restart the application:

```bash
mvn spring-boot:run
```

The script is intended for a fresh demo database. It leaves existing deposit balances and rows untouched; if a sample deposit already exists with the same plan and day count, it will not be reset to `1000.00`.

### Swagger/OpenAPI

With the application running, open [Swagger UI](http://localhost:8080/swagger-ui/index.html).

- To retrieve deposits, expand **GET** `/api/time-deposits`, select **Try it out**, then **Execute**. A successful response is HTTP **200** with a JSON array containing `id`, `planType`, `balance`, `days`, and `withdrawals`.
- To update all balances, expand **POST** `/api/time-deposits/update-balances`, select **Try it out**, then **Execute**. A successful response is HTTP **204 No Content**.

The [OpenAPI JSON specification](http://localhost:8080/v3/api-docs) is available at that URL. Swagger UI exposes these two business operations.

### Expected behavior

The calculator applies monthly interest as the plan's rate divided by 12, subject to a global no-interest period through day 30. Basic uses 1% and accrues after day 30. Student uses 3% after day 30 and only while the deposit is under 366 days old. Premium uses 5% only after day 45. Interest is rounded to two decimal places using `RoundingMode.HALF_UP`; database balances are persisted to two decimal places.

After **one POST** using fresh demo data, expected balances are:

| Plan | Days | Balance |
|---|---:|---:|
| basic | 30 | 1000.00 |
| basic | 31 | 1000.83 |
| premium | 45 | 1000.00 |
| premium | 46 | 1004.17 |
| student | 365 | 1002.50 |
| student | 366 | 1000.00 |

Each POST applies the calculator again to the current balance, so repeated calls apply interest again and are cumulative. GET returns associated withdrawals; withdrawal amounts are not deducted from balances.

### Testing

From the repository root:

```bash
cd java
mvn clean test
```

The suite includes characterization tests for calculator behavior and interest boundaries, JPA/H2 persistence schema tests, API integration tests for GET and POST behavior and persistence, and integration checks for Swagger UI and the generated OpenAPI specification. The latest verified run completed **24 tests with 0 failures and 0 errors**.

### Architecture and design decisions

Interest calculation remains in `TimeDepositCalculator` and its Basic, Student, and Premium Strategy implementations. The existing `TimeDeposit` class and the public signature of `TimeDepositCalculator.updateBalance` are unchanged. Spring Data JPA repositories persist the entities; a service coordinates persistence and calculator calls; a mapper converts between entities, the existing Double-based domain model, and response DTOs. GET retrieves deposits and withdrawals in separate batched queries, without adding a collection relationship to the deposit entity.

The boundary behavior follows the existing calculator and README: no interest through day 30, premium interest only above day 45, and student interest only below day 366. Withdrawals are exposed as related records but are not deducted because the assignment does not require that behavior. The implementation uses these focused layers and does not claim a complete Hexagonal Architecture. Tests use the configured embedded H2 database; Testcontainers is not configured.

### AI-assisted development

The AI-assisted workflow and reproducibility notes are in [`java/AI_DEVELOPMENT.md`](java/AI_DEVELOPMENT.md), with project-specific agent rules in [`java/AGENTS.md`](java/AGENTS.md).

## Original Assignment

### XA Bank Time Deposit

#### Context
A junior developer implemented domain logic for a time deposit system but did not complete the API functionality. Your task is to refactor the existing codebase to implement all required functionalities based on the provided business requirements, ensuring no breaking changes occur.

#### Requirements

1. **API Endpoints**:
    - Create a RESTful API endpoint to update the balances of all time deposits in the database.
    - Create a RESTful API endpoint to retrieve all time deposits.
        - The GET endpoint should return a list of all time deposits with the following schema:
            - `id`
            - `planType`
            - `balance`
            - `days`
            - `withdrawals`

2. **Database Setup**:
    - Store all time deposit plans in a database.
    - Define the following tables:
        - `timeDeposits`:
            - `id`: Integer (primary key)
            - `planType`: String (required)
            - `days`: Integer (required)
            - `balance`: Decimal (required)
        - `withdrawals`:
            - `id`: Integer (primary key)
            - `timeDepositId`: Integer (foreign key, required)
            - `amount`: Decimal (required)
            - `date`: Date (required)

3. **Interest Calculation**:
    - Implement logic to calculate monthly interest based on the plan type:
        - **Basic Plan**: 1% interest
        - **Student Plan**: 3% interest (no interest after 1 year)
        - **Premium Plan**: 5% interest (interest starts after 45 days)
    - No interest is applied for the first 30 days for any existing plans.

4. **Refactoring Constraints**:
    - Do not introduce breaking changes to the shared `TimeDeposit` class or modify the `updateBalance` method signature.
    - Ensure the design is extensible to accommodate future complexities in interest calculations.

5. **Code Quality**:
    - Adhere to SOLID principles, design patterns, and clean code practices where applicable.

6. **AI-Assisted Development**:
    - Set up an AI harness or agent workflow and use it throughout the development for this take-home exercise.
    - Briefly document the tools and setup used (e.g., LLMs, coding assistants, agentic frameworks, configuration).
    - Ensure your AI setup is practical and reproducible.
    - Include any custom rules, system prompts, or agent configurations used.
    - Include a brief summary of which parts of the solution were AI-assisted and why.

#### Important Guidelines
- The existing `TimeDepositCalculator.updateBalance` method is functioning correctly. Ensure its behavior remains unchanged after refactoring.
- The final solution must include **exactly two API endpoints**. Do not develop additional endpoints.
- **Do not** create a pull request or a new branch in the ikigai-digital repository. Instead, fork the repository into your own GitHub repository and develop the solution there.
- Handling invalid input or exceptions is not required.
- Use any tools, frameworks, or libraries you find suitable.
- In case of ambiguity, make logical assumptions and justify them in code comments.

#### Preferred Stack
- Use an OpenAPI Swagger contract.
- Embrace Hexagonal Architecture.
- Follow atomic commit practices.
- Utilize testcontainers.
- Leverage AI-assisted development tools for code generation, testing, and refactoring.

#### Submission Instructions
- Provide clear instructions on how to trigger the endpoints using the Swagger contract.
- Email the link to your public GitHub repository.