# AI-Assisted Development

## 1. Tools and Setup

This project uses a lightweight, human-in-the-loop AI-assisted development workflow.

**Tools:**
- ChatGPT (OpenAI) — architecture discussions, code review, refactoring suggestions, and test case design.
- IntelliJ IDEA — local development and code inspection.
- Apache Maven — build and automated test execution.
- Git and GitHub — version control and submission.

No autonomous agent framework or external orchestration infrastructure is required.

## 2. Configuration

The `AGENTS.md` file contains the engineering rules and constraints used to guide AI assistance.

The same instructions are provided to ChatGPT as development context.

The AI assistant does not have unrestricted access to the local development environment. Suggested changes are reviewed and applied by the developer.

## 3. Development Workflow

1. Review the README and existing implementation.
2. Use AI to identify potential refactoring opportunities and edge cases.
3. Add characterization tests for existing behavior.
4. Review and implement refactoring suggestions.
5. Run tests locally using Maven.
6. Review test failures and adjust the implementation.
7. Commit verified changes to Git.

## 4. AI-Assisted Activities

| Activity | AI Contribution | Human Responsibility |
|---|---|---|
| Code analysis | Identify code smells and design risks | Validate findings |
| Test design | Suggest test scenarios and edge cases | Review and execute tests |
| Refactoring | Suggest design patterns and implementation options | Choose and implement the solution |
| Architecture | Compare REST and persistence approaches | Make final architecture decisions |
| Documentation | Assist with technical documentation | Verify accuracy |

## 5. Verification

AI-generated suggestions are not accepted without review.

The final implementation is validated using automated tests and local build execution.

## 6. Limitations

AI output may contain incorrect assumptions, implementation errors, or unnecessary abstractions.

All suggestions are reviewed against the project requirements, existing behavior, and test results.

The developer remains responsible for the final solution.