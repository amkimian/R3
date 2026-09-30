# R3

R3 is a Java 21 scripting-language project using ANTLR 4.9.3. The code lives under `com.staberinde.sscript` and includes grammar-driven parsing, visitor-based execution, variables, control flow, collections, lambdas and annotated Java function registration. Existing tests cover script execution; these are source-level observations, not a claim of production readiness.

Canonical location: `D:\Development\Platforms\R3`.

This project retains its own identity and Git history, separately from Catwalk and other development journeys. Lifecycle is undecided; see `STATUS.md`.

## Source layout

- `src/main/antlr4/`: SScript grammar.
- `src/main/java/`: parser integration, execution blocks, values, visitors and Java function support.
- `src/test/java/` and `src/test/resources/`: Java tests and script fixtures.
- `notes.md`: original development notes, retained verbatim.

## Validation

Use Java 21 and Maven; the normal test command is `mvn test`. ANTLR generation is configured in `pom.xml`. The migration attempted `mvn -o test`; it stopped before tests because Maven Surefire 3.2.2 was not cached. Dependencies and build configuration were not upgraded. Runtime behavior remains unverified.
