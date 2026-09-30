# AGENTS.md — Permanent instructions for AI coding agents working on Momentum

> This file defines the permanent operating rules for AI coding agents working in this repository.
> Agent-specific `.opencode/agents/*.md` files define each agent's role and tool permissions.
> `task.md` defines the state of the current task.
>
> **Core principle:** build what has been decided, not what the agent thinks should be built.

---

## 1. Project Purpose

Momentum (`com.momentum.app`) is a premium, offline-first Android app for habits, routines, tasks, medicines/supplements, learning activities, and personal goals. The fundamental domain concept is `Activity` — one unified system, not separate silos.

See `docs/PRODUCT_SPEC.md` for product behavior and `docs/DESIGN_SYSTEM.md` for visual language.

Do not turn Momentum into a generic Material Design CRUD application. Generic lists, checkbox-row screens, FAB-centric layouts, gray-card defaults, or other departures from the approved design language are regressions.

---

## 2. Source of Truth

Use the most authoritative applicable source:

1. Explicit user requirement for the current task.
2. Approved product decisions/specifications.
3. Relevant feature/design documentation.
4. Approved architecture documentation and ADRs.
5. Current task state in `task.md`.
6. Existing code as evidence of current implementation.

When approved documentation and existing code disagree, do not silently preserve the old implementation when the task explicitly requires the documented behavior.

Do not use obsolete or historical material as current requirements.

Do not invent missing product decisions. If an unresolved product decision materially blocks implementation, report it to the Orchestrator.

Minor implementation details may follow established project patterns without requiring a new product decision.

---

## 3. Hard Product Requirements

- Core functionality must work completely offline.
- No backend, account, login, cloud service, Firebase, Supabase, REST API, authentication, analytics SDK, ads, or tracking.
- User data remains on-device through Room and DataStore.
- `INTERNET` must not be added to the manifest.
- Never use fake persistence to make a screen appear functional.
- Screens must use real repositories/data sources or be clearly labeled placeholders for a later phase.
- Privacy and local-only behavior are mandatory.
- Future synchronization requires an architectural decision before implementation.

---

## 4. Architecture

Momentum uses Clean Architecture + MVVM in a single `app` module:

```text
UI (Compose)
    ↓
ViewModel
    ↓
Use Case
    ↓
Repository
    ↓
Local Data Source
    ↓
Room / DataStore
```

Rules:

- `presentation/` contains Compose UI, components, theme, and navigation.
- `domain/` contains models, repository interfaces, use cases, and pure engines.
- `data/` contains Room/DataStore implementations.
- `notification/` contains local reminder infrastructure.
- Composables must not call repositories or DAOs directly.
- Business logic belongs in use cases/engines, not composables.
- Dependency injection remains manual through `AppContainer`.
- Do not introduce Hilt, Koin, or multi-module architecture without an ADR.
- Do not add generic base abstractions without a concrete benefit.
- Do not replace working architecture without a documented architectural decision.

When an architectural decision is required, the Architect owns the decision and the canonical ADR record at `docs/architecture/decisions/DECISIONS.md`.

---

## 5. Kotlin and Compose

Follow the project's existing Kotlin and Compose conventions.

- Official Kotlin style; 100-column soft limit.
- Prefer null-safety; avoid `!!` except justified tests.
- Use coroutines and `Flow`/`StateFlow` for asynchronous state.
- Use `java.time.LocalDate` for date logic.
- Prefer immutable state and `val`.
- ViewModels own screen state.
- Screens receive state and event callbacks.
- Use `collectAsStateWithLifecycle()`.
- Use Material 3 and Momentum semantic colors.
- Do not hardcode screen colors.
- Reusable components should remain previewable.
- Keep touch targets accessible and layouts dynamic-type safe.
- Respect reduced-motion preferences.
- Do not add animation that blocks input or delays data.

Follow `docs/DESIGN_SYSTEM.md` for visual decisions rather than inventing a competing visual language.

---

## 6. Data and Persistence

- Room entities must follow `docs/DATA_MODEL.md`.
- Schema changes require the appropriate migration or documented pre-1.0 fallback and committed schema output.
- DAOs use `Flow` for observed data and `suspend` for one-shot operations.
- Never enable `allowMainThreadQueries`.
- Completion history is append-style truth.
- Editing an Activity applies forward from the edit date; do not rewrite historical completion records.
- Activity deletion is `ARCHIVED` by default in v1 unless an approved decision explicitly requires hard deletion.
- Do not store one database row for every future recurrence occurrence.

---

## 7. Notifications and Offline Behavior

- Reminders are local-only.
- Use the approved `AlarmManager` / `WorkManager` strategy for reminders.
- No FCM or remote notification infrastructure.
- Preserve the documented notification architecture.

---

## 8. Dependencies

- `gradle/libs.versions.toml` is the version source of truth.
- Do not hardcode dependency versions in module build files.
- Use the approved Android/Kotlin/Compose/Room/DataStore/Coroutines/Lifecycle/WorkManager stack.
- New dependencies require justification.
- Architectural or infrastructure dependencies require an ADR.
- Networking, analytics, cloud services, and tracking remain prohibited unless an explicit future decision changes the product architecture.

---

## 9. Scope Discipline

Implement the requested scope and nothing more.

Do not silently add:

- unrelated refactors
- cleanup
- speculative abstractions
- future features
- unrelated bug fixes
- redesigns
- dependency changes

If an additional issue is discovered, leave it unchanged and report it separately unless it directly prevents completion of the current task.

Prefer the smallest coherent implementation that satisfies the requirements.

Do not delete, revert, or overwrite unrelated user work.

---

## 10. Product vs. Technical Decisions

Keep these responsibilities separate.

**Product decisions** include:
- user workflows
- feature behavior
- UX
- business rules
- user-facing copy
- feature scope

**Technical decisions** include:
- architecture
- APIs
- persistence
- dependency strategy
- infrastructure
- security implementation
- performance

Agents must not use a technical implementation choice to silently change product behavior.

If a technical solution requires a product decision, report the decision to the Orchestrator instead of inventing it.

---

## 11. Validation

Validation must be proportional to the change.

Before validating, identify:

1. What changed.
2. What behavior it affects.
3. What existing tests/checks cover that behavior.
4. The smallest validation set that provides reasonable confidence.

Examples:

- Documentation/configuration: validate the artifact.
- Isolated logic: run relevant unit tests.
- UI: run relevant tests and perform runtime/manual verification when behavior is visual or interactive.
- Data/schema: validate persistence and migration behavior.
- Cross-cutting or high-risk changes: expand regression coverage appropriately.

Do not run the full suite by ritual.

Do not claim a test was executed when it was only `UP-TO-DATE`. When actual execution matters, rerun it.

If required validation cannot be run, state that clearly.

### Correction limit

When validation finds a concrete defect:

1. Correct it.
2. Validate again.

Allow at most two correction cycles for the same task. If the task still fails after the second correction cycle, stop and report the failure.

Do not use validation cycles to reopen unrelated design questions.

---

## 12. Git and Working Tree

- Preserve unrelated working-tree changes.
- Inspect `git status` and the relevant diff before completion.
- Do not reset, revert, discard, or overwrite user work unless explicitly requested.
- Do not commit or push.
- Never commit secrets, keystores, `google-services.json`, or `local.properties`.
- Agents may inspect Git history when current evidence is insufficient, but historical investigation is not a default step.
- Git history must never be used to reopen a settled decision without new evidence.

Committing and pushing are Human-in-the-Loop operations.

---

## 13. Working Style

Use this default execution loop:

```text
Understand
    ↓
Inspect only what is needed
    ↓
Decide
    ↓
Implement
    ↓
Verify
    ↓
Report
    ↓
STOP
```

### Act when clear

If enough information exists to perform the assigned role, act.

Do not wait for perfect certainty.

Do not manufacture ambiguity.

Do not investigate merely because more information exists.

### Investigation

Investigation is justified only when its answer can change the current implementation or verification decision.

Start from the files, requirements, symbols, and documentation already supplied by the task.

Read additional material only when directly required.

Do not:

- recursively enumerate the repository
- read every project document
- read every knowledge file
- follow unrelated dependency chains
- repeat the same search using different wording
- investigate hypothetical problems
- perform historical Git archaeology without a concrete reason

Once sufficient evidence exists, stop investigating and perform the task.

If sufficient context cannot be established within the relevant scope, report the concrete blocker rather than widening discovery indefinitely.

### Reconsideration

Do not reopen a decision because another approach is possible.

Reconsider only when new concrete evidence shows that the current decision is wrong, insufficient, or incompatible with an explicit requirement.

### Completion

When the assigned work and required validation are complete, stop.

Additional improvements are not a reason to continue.

---

## 14. Multi-Agent System

Momentum uses four independent specialist agents:

- Orchestrator — coordinates.
- Architect — decides architecture.
- Developer — implements.
- Reviewer — verifies.

They are not a hierarchy.

The Orchestrator selects the minimum workflow required for the task.

### Routing

Use the smallest sufficient workflow:

- Simple implementation/test/UI task: Developer → Reviewer when independent verification adds value.
- Architecture decision required: Architect → Developer → Reviewer.
- Architecture-only task: Architect.
- Verification-only task: Reviewer.
- Documentation/configuration-only task: Developer, with Reviewer only when useful.

There is no mandatory Architect → Developer → Reviewer pipeline for every task.

The Orchestrator should not change the workflow unless new concrete evidence makes the selected workflow insufficient.

Agents must not invoke other agents.

---

## 15. Authoritative Task Artifacts

### `AGENTS.md`

Permanent project rules and multi-agent operating rules.

### `task.md`

Current task state and execution record.

### `PROJECT_STATUS.md`

Current project-level implementation and verification status. The Orchestrator owns it.

### `ISSUES.md`

Canonical OPEN/CLOSED issue registry. The Orchestrator owns it.

### `knowledge/`

Modular project knowledge referenced by stable IDs.

### `docs/`

Human-facing product, design, data, architecture, roadmap, and decision documentation.

These artifacts have distinct responsibilities. Do not create duplicate sources of truth.

---

## 16. Task State and Ownership

Every active task must have `task.md`.

Agents must not silently change the task objective, scope, acceptance criteria, priority, or required deliverables.

If the task definition is incomplete or incorrect, report it to the Orchestrator.

### `task.md` ownership

**Orchestrator**
- Task Identity
- Objective
- Scope
- Requirements
- Knowledge References
- Current Issues
- Handoff History
- Final Outcome

**Architect**
- Relevant Files
- Architecture / Implementation Plan

**Developer**
- Implementation Status

**Reviewer**
- Review Findings
- Validation

OpenCode cannot enforce Markdown section ownership. These ownership rules are therefore also defined in the corresponding agent prompts.

Replace stale state rather than appending transcripts or reasoning history.

---

## 17. Knowledge System

`knowledge/` contains small, topic-specific knowledge units.

Knowledge should be retrieved by stable ID, not by reading the entire directory.

When knowledge is required:

1. Identify the relevant knowledge ID.
2. Read only that knowledge.
3. Use it for the current task.
4. Retrieve more only when the current information is demonstrably insufficient.

Do not invent missing project rules.

Do not replace missing knowledge with unrelated documentation.

Knowledge must not become a duplicate copy of `docs/`. It should point to authoritative documentation where appropriate.

Knowledge retrieval is a means to complete a task, not a task in itself.

---

## 18. Issues

`ISSUES.md` is the canonical issue registry.

Each issue has a stable `ISSUE-NNN` ID and remains OPEN until its verification criteria pass.

Agents report issue findings through the current task and handoff. They do not independently modify the canonical issue registry unless their role explicitly has that ownership.

Do not create issue records for hypothetical problems.

---

## 19. Project Status

`PROJECT_STATUS.md` is the canonical project-level status.

Tracked features use:

- `NOT_IMPLEMENTED`
- `IN_PROGRESS`
- `IMPLEMENTED_NOT_TESTED`
- `PARTIALLY_VERIFIED`
- `FULLY_VERIFIED`
- `BLOCKED`

Only verified evidence may move a feature to `FULLY_VERIFIED`.

Developer completion alone is not sufficient for `FULLY_VERIFIED`.

The Orchestrator owns this artifact.

---

## 20. Handoffs

Agent handoffs must be concise and factual.

Use:

- `RESULT`
- `DECISIONS`
- `FILES_CHANGED`
- `KNOWLEDGE_USED`
- `TESTS_RUN`
- `BLOCKERS`
- `NEXT_AGENT_ACTION`

Write `None` when a field has no value.

Do not include:

- investigation narratives
- repeated task descriptions
- large tool output
- copied documentation
- speculative future work

Reference existing task, issue, and knowledge IDs instead of duplicating their contents.

The handoff must describe what actually happened, not what the agent intended to do.

---

## 21. ADR Ownership

The canonical ADR file is:

`docs/architecture/decisions/DECISIONS.md`

The Architect is the authoritative owner of architectural decisions and ADR entries.

When an ADR is genuinely required:

1. Architect makes the decision.
2. Architect records it in `task.md`.
3. Architect creates or updates the canonical ADR.
4. Developer implements the approved decision.
5. Reviewer verifies the implementation against the decision.

Do not create an ADR for every minor implementation choice.

Do not modify unrelated ADR entries.

Existing ADRs should not be silently rewritten.

---

## 22. Agent Permission Boundary

Technical permissions are enforced through OpenCode.

The canonical human-readable permission matrix is:

`docs/architecture/MULTI_AGENT_PERMISSIONS.md`

The agent definitions in `.opencode/agents/*.md` define role-specific permissions and behavior.

`opencode.json` defines environment-level external-directory policy.

Agents must not modify the permission boundary or `opencode.json` during ordinary work.

Git commit/push remain Human-in-the-Loop operations.

---

## 23. Infrastructure Work

When explicitly working on the multi-agent infrastructure itself:

- do not modify Momentum application behavior unless the task specifically requires an infrastructure validation test;
- do not change production architecture, UI, dependencies, database schemas, or navigation as collateral work;
- keep infrastructure changes isolated and reversible;
- verify the infrastructure change before returning to application development.

---

## 24. Final Rule

> **Build what has been decided, not what you think should be built.**

Use the approved requirements and project documentation.
Inspect only what is necessary.
Make the smallest correct change.
Validate it appropriately.
Report concrete results.
Stop when the job is done.

Do not turn execution into an endless investigation.
