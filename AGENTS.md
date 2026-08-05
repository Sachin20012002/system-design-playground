# Repository Working Context

## Repository map

- `url-shortener/` and `rate-limiter/` are implemented system-design projects.
- Both implemented projects are complete for the current learning scope. This does not imply production readiness or rule out future improvements.
- `backend-handbook/` is reference and learning material, not a project.
- Use `backend-handbook/` for durable backend concepts, patterns, trade-offs, common mistakes, and revision questions that apply across projects.
- Keep implementation-specific behavior, configuration, diagrams, limitations, and measurements in the corresponding project's documentation.
- Update the handbook after a reusable lesson is verified in a project or when existing technical guidance becomes inaccurate.
- Avoid copying project-specific endpoints, ports, class names, metric names, benchmark results, or version details into the handbook.
- Chat App and News Feed are planned projects. Keep them represented as planned even when their directories do not yet exist.

## Sources of truth

- Treat source code, tests, build files, and runtime configuration as the authority for current behavior.
- Keep README files and design documentation aligned with the implementation.
- Do not describe planned or future capabilities as implemented.
- Do not infer that a planned item is obsolete merely because no directory or implementation exists yet.

## Documentation workflow

- Give every implemented project a project README and a `docs/README.md` study index.
- Link implemented project names in the root README to their project READMEs. Leave planned projects unlinked until their READMEs exist.
- Organize each project study index for three modes: quick revision, system-design interview practice, and deep implementation study.
- Include a fast-reference table that routes readers to requirements, APIs, architecture, data, operations, trade-offs, and future work as applicable.
- Keep project documentation centered on verified implementation behavior and decisions. Mark future improvements and production gaps explicitly.
- When a verified lesson applies across projects, add or update one concise Backend Handbook topic and link it to concrete project examples.
- Handbook concept pages should normally include a mental model, mechanics, trade-offs, common mistakes, revision questions, and project links. Use only the sections that improve the topic.
- Keep cross-cutting study material under `backend-handbook/guides/`, not under a technology or architecture category.
- Keep interview notes project-specific and optimized for active recall: question first, direct answer, decision rationale, trade-off, limitation, and links to deeper material when useful.
- Avoid maintaining the same full generic explanation in the handbook and project docs. Summarize and link while retaining enough project context to understand the implementation.
- After documentation changes, verify that local Markdown links resolve and run `git diff --check`.

## Change discipline

- Make narrowly scoped changes and preserve the repository's learning-oriented structure.
- Do not refactor, redesign, or add documentation when the task only asks to correct stale repository information.
- For documentation audits, present proposed changes as small logical diffs and wait for approval before applying them.
- Do not commit changes unless explicitly requested.
- Preserve unrelated user changes in the working tree.

## Git tags

- Tags represent completed learning milestones for individual projects.
- Name new tags `<project>-v<major>-<milestone>`, using lowercase kebab-case.
- Examples: `url-shortener-v8-documentation` and `rate-limiter-v7-resilience`.
- Use annotated tags with a concise description of the milestone.
- Do not create, rename, or delete tags unless explicitly requested.
- Preserve published tag targets when migrating historical names to the convention.
