# AGENTS.md: exquisite_evals

A demo of AI evals built with Spring AI (Kotlin, Spring Boot): it measures how well an agent can work out the API of an unfamiliar Java library with different tool sets ("arms"), graded by code checks, an LLM judge and Jev. Not published. Slides in `.preso/` are published to GitHub Pages by `.github/workflows/preso.yaml`.

## Skills

Follow the `zen-of-projects` Skill; this file records only project facts and exceptions. For design, domain modeling, effects and testing strategy, follow `zen-of-james`. Extract them with `./gradlew extractSkillsJars` into the gitignored `.kiro/skills/` and read the relevant ones before working.

## MCP

`javadocs` (https://www.javadocs.dev/mcp), configured in `.mcp.json` / `.kiro/settings/mcp.json` and approved in `.claude/settings.json`. Its tools are deferred in Claude Code: load them with ToolSearch (search `javadocs`).

## Build, test & dev

- Full validation (what CI runs): `docker build -t exquisite-evals-sandbox:1 sandbox && ./gradlew build`. It excludes the `live` tests.
- **Docker:** the tests need Docker and the sandbox image (`EvalRunnerTest` runs the `shell` arm, which starts a container from `exquisite-evals-sandbox:1`). Start Docker as zen-of-projects "Docker for tests" says, then build the image. When `sandbox/Dockerfile` changes, bump the image tag here, in CI and in the code together.
- **Paid tests:** tests tagged `live` call Amazon Bedrock, javadocs.dev and TypeSafe and cost money; they only run with `-Plive`. Never run them in the maintenance routine.
- **Running the evals** (`./gradlew bootRun ...`) calls paid models and needs AWS credentials and Docker for the shell sandbox (`sandbox/Dockerfile`). Manual only; never in the maintenance routine.
- `results/` holds recorded eval runs; don't edit or regenerate them.

## Maintenance routine

`.factory/MAINTENANCE.md` (weekly), following the `zen-of-projects` Skill.

## Exceptions to zen-of-projects

- **Prereleases on purpose:** the demo targets Spring AI `2.1.0-M1` (with Spring Boot `4.2.0-M2`), because the evaluated tasks and arms use APIs that only exist on that line (for example `spring-ai-hostedtool`). Move to newer milestones or RCs of the same line, and to the 2.1 GA once it's out; don't move back to 2.0.x. Treat a Spring AI or Spring Boot bump as needing a human (`needs-human`) if it changes eval behavior or the recorded results' assumptions.
- **Optional `-Pinspector`** dependency is a local `0.0.1-SNAPSHOT` (`mavenLocal`, only with that flag); leave it.
