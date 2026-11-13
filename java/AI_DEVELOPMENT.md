# AI-Assisted Development

## Tools and configuration

- GitHub Copilot Agent in IntelliJ IDEA was used for coding assistance.
- ChatGPT was used for planning and code review.
- The exact Copilot model is **not recorded** in the available project configuration.
- `java/AGENTS.md` is the project-specific agent instruction file. It sets a senior Java backend engineer role and requires reading the README; preserving `TimeDepositCalculator.updateBalance` behavior and signature and avoiding breaking changes to `TimeDeposit`; writing characterization tests before refactoring; preserving interest boundaries and rounding; keeping domain logic independent of infrastructure; documenting ambiguous assumptions; and only reporting tests that were actually run.
- No additional checked-in Copilot-specific configuration was found.

## Workflow

AI analyzes a task and proposes an implementation plan. A human reviews and approves the proposal before implementation. The changes are then reviewed by a human and validated with relevant tests; verified work is organized into atomic commits. The human makes final architecture and validation decisions.

## AI-assisted tasks

AI assistance was used for:
- Characterization tests: to capture the existing calculator behavior before refactoring.
- Strategy Pattern refactoring: to separate plan-specific interest rules and improve extensibility.
- JPA/H2 persistence: to implement entity mappings, repositories, and persistence tests.
- REST API: to implement the controller, service, DTOs, mapper, and integration tests.
- OpenAPI documentation: to document the two required endpoints and verify the generated contract.

AI accelerated implementation and test generation, while architectural decisions, behavior preservation, code review, and final validation remained under human control.

## Human decisions and validation

Human review prioritized preserving the existing calculator behavior, public signature, interest boundaries, and rounding. The persistence bridge converts `BigDecimal` balances to the existing `Double` domain representation and maps results back to two-decimal `BigDecimal` values. Withdrawals remain associated through the existing unidirectional JPA relationship and are not deducted from balances.

Integration-test improvements remove class-level test transactions so MockMvc requests exercise the service transaction and committed state. Cleanup deletes withdrawals before deposits to respect the foreign key. Balance assertions are keyed by deposit ID; tests also verify withdrawals remain unchanged and repeated POST calls persist cumulative results.

The OpenAPI integration test verifies that `/swagger-ui/index.html` is served and that `/v3/api-docs` contains exactly the two business operations and their response schemas. Additionally, Swagger UI was manually opened in a browser to verify that both business operations and their response schemas were displayed correctly.

## Reproducing the workflow

1. Use Java 17 or newer, Maven, IntelliJ IDEA, and the GitHub Copilot plugin with Agent mode enabled. Use ChatGPT for planning or review as needed.
2. Keep `AGENTS.md` and `AI_DEVELOPMENT.md` in the `java/` directory. Before each major implementation stage, have the agent read `java/AGENTS.md` and `README.md` from the repository root.
3. Use a prompt such as:

   > Read java/AGENTS.md and README.md from the repository root. Analyze the requested change and propose an implementation plan before modifying any files. Preserve existing behavior and public APIs. Add relevant tests and report their results. Do not commit changes without human approval.

4. Review and approve the proposed plan before edits. Review the changes, run the relevant Maven tests, and commit reviewed changes in atomic units only with human approval.

The recorded test command for the API, persistence, calculator, and OpenAPI changes was `mvn clean test`.
