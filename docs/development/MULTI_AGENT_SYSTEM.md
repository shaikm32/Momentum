# Momentum Multi-Agent Development System

**Purpose:** Canonical documentation of how the Momentum Android application is developed using the custom OpenCode multi-agent system.

**Document scope:** Development-process infrastructure. This document does **not** define Momentum's application architecture. Application architecture belongs under `docs/architecture/`.

**Current system:** Four-agent system — Orchestrator, Architect, Developer, Reviewer/Tester.

---

## 1. Why this system exists

Momentum is being developed with a deliberate separation of responsibilities so that:

- planning does not get mixed with implementation;
- architectural decisions are made by a dedicated specialist;
- implementation is performed only within approved scope;
- verification is independent from implementation;
- agents receive only the context they need;
- repeated investigation and agent loops are prevented;
- project status is based on evidence rather than claims;
- Git history and permission boundaries remain human-controlled.

The governing principle is:

> **Route the minimum necessary work required to produce a verified result. Then stop.**

The system is intentionally not a fixed pipeline where every task must pass through every agent.

---

# 2. System at a glance

```text
                         USER
                           │
                           ▼
                    ORCHESTRATOR
                           │
              ┌────────────┼────────────┐
              │            │            │
              ▼            ▼            ▼
          ARCHITECT    DEVELOPER    REVIEWER
              │            │            │
              └────────────┴────────────┘
                           │
                           ▼
                    VERIFIED RESULT
```

The actual routing is dynamic.

### Typical flows

Simple implementation/UI bug:

```text
Orchestrator → Developer → Reviewer
```

Architecture-heavy implementation:

```text
Orchestrator → Architect → Developer → Reviewer
```

Architecture investigation only:

```text
Orchestrator → Architect
```

Verification-only task:

```text
Orchestrator → Reviewer
```

Simple implementation where verification is explicitly unnecessary:

```text
Orchestrator → Developer
```

The Orchestrator must not invoke an agent merely because the agent exists.

---

# 3. Core design principles

## 3.1 Minimum-agent principle

Before invoking an agent, the Orchestrator must be able to answer:

1. What exact responsibility does this agent need to perform?
2. Why can the current agent not perform that responsibility?
3. What concrete output is required?

If those questions cannot be answered, the agent should not be invoked.

This prevents unnecessary handoffs, token consumption, repeated discovery, and model loops.

---

## 3.2 Context minimization

Agents should not receive the entire project context by default.

A delegated task should contain only what is required:

- objective;
- scope;
- out-of-scope items;
- acceptance criteria;
- relevant files;
- relevant knowledge IDs;
- relevant issue IDs;
- approved decisions/constraints;
- required output.

Do not routinely pass:

- the entire project documentation set;
- the entire knowledge directory;
- unrelated issues;
- unrelated ADRs;
- irrelevant conversation history;
- large tool outputs;
- duplicated task descriptions.

This is especially important because broad context can cause unnecessary repository exploration and repeated reasoning.

---

## 3.3 Bounded investigation

For one unresolved problem:

1. Delegate the investigation.
2. Receive the first hypothesis and evidence.
3. If confirmed, proceed.
4. If disproved, allow **one** alternative investigation.
5. If the second investigation fails, stop.

A third investigation requires an explicit reassessment and genuinely new evidence.

The system must not repeatedly send agents back to investigate the same problem without new evidence.

---

## 3.4 Scope discipline

Agents must:

- work only within the defined task;
- preserve unrelated working-tree changes;
- not invent product requirements;
- not redesign unrelated behavior;
- not silently expand scope;
- not reset/revert/discard user work.

If completing a task requires a materially breaking change, deletion of existing functionality, or another consequential change outside the agreed scope, the issue must be surfaced rather than silently performed.

---

## 3.5 Evidence over claims

The system does not accept:

- "looks good";
- "probably fixed";
- "should work";
- "I think this is complete."

Completion requires concrete evidence appropriate to the task.

A green test suite is not automatically proof that a changed path was exercised. Verification must actually cover the changed behavior.

---

# 4. The four agents

## 4.1 Orchestrator

### Purpose

The Orchestrator is the **router, coordinator, and project-state manager**.

It does not implement application code.

### Responsibilities

The Orchestrator:

1. Reads the governing project rules.
2. Understands the user's requested outcome.
3. Classifies the task.
4. Determines the minimum required specialist agents.
5. Retrieves only relevant project knowledge.
6. Creates/updates `task.md`.
7. Delegates through OpenCode's `task` mechanism.
8. Evaluates specialist results.
9. Routes follow-up work only when necessary.
10. Updates `ISSUES.md` when issue state changes.
11. Updates `PROJECT_STATUS.md` when verified project status changes.
12. Records the final task outcome.
13. Stops when the task is complete or blocked.

### What it must not do

The Orchestrator must not:

- implement application code itself;
- perform another agent's specialist role unnecessarily;
- invoke all agents automatically;
- repeatedly investigate the same problem;
- expand task scope;
- claim verification that was not performed;
- commit or push.

### State ownership

The Orchestrator owns the project-level coordination state:

- `task.md` Orchestrator-owned sections;
- `PROJECT_STATUS.md`;
- `ISSUES.md`.

---

# 5. Architect

## Purpose

The Architect owns **architectural reasoning and architectural decisions**.

The Architect is required when the task may alter or establish architectural behavior.

### Invoke Architect for

Examples include:

- domain model changes;
- business-rule changes with architectural impact;
- Clean Architecture layer changes;
- navigation architecture;
- persistence/data architecture;
- shared application infrastructure;
- dependency strategy;
- cross-feature contracts;
- changes to established architectural decisions;
- significant ambiguous implementation approaches.

### Do not invoke Architect for

Normally avoid Architect for:

- isolated UI fixes;
- straightforward bug fixes;
- simple implementation changes;
- localized refactoring with no architectural impact;
- ordinary tests that require no architectural decision.

### Responsibilities

Architect:

- studies the supplied task and targeted project context;
- determines the architectural implications;
- produces a concrete implementation plan;
- identifies exact files;
- identifies constraints;
- defines validation expectations;
- records architectural decisions where appropriate.

### ADR ownership

**Architect is the authoritative owner of architectural decisions and ADRs.**

The workflow is:

```text
Architect
   │
   ├── task.md: Architectural Decision
   │
   └── docs/architecture/decisions/DECISIONS.md
             │
             ▼
         Developer
             │
             ▼
         Reviewer
```

The Orchestrator coordinates the workflow but does not author architectural decisions.

The Architect should create/update an ADR only when the decision genuinely qualifies as an architectural decision. It should not modify unrelated ADRs.

---

# 6. Developer

## Purpose

The Developer implements the approved solution.

### Responsibilities

The Developer:

- implements the supplied requirements;
- follows the approved architecture/implementation plan;
- modifies only required files;
- preserves existing behavior outside scope;
- performs targeted implementation validation;
- reports changed files and validation.

### Developer must not

- redesign an approved solution without cause;
- invent product behavior;
- perform broad repository discovery;
- modify unrelated working-tree changes;
- create unrelated documentation;
- invoke another agent;
- expand scope because another improvement looks desirable;
- commit or push.

If the implementation plan is insufficient to implement safely, the Developer should report the missing information rather than compensate with unrestricted discovery.

---

# 7. Reviewer / Tester

## Purpose

The Reviewer is the **independent verification gate**.

It does not implement fixes.

### Responsibilities

The Reviewer verifies:

- explicit requirement compliance;
- functional correctness;
- regression risk;
- documented architecture compliance;
- security/privacy concerns relevant to the change;
- required edge cases;
- changed behavior validation;
- acceptance criteria.

### Reviewer principles

The Reviewer must:

- start with changed files;
- inspect additional files only when directly required;
- avoid repository-wide discovery;
- avoid repeated searches;
- avoid speculative findings;
- avoid subjective style preferences as defects;
- avoid redesigning working behavior;
- focus on actionable defects.

### Findings

Each actionable defect should include:

- severity;
- file;
- location;
- specific defect;
- required correction.

If no actionable defect is found, the Reviewer should explicitly report that the review passed.

### Important verification rule

The Reviewer must distinguish between:

- a test existing;
- a test compiling;
- a test being executed;
- the test actually proving the changed behavior.

For example, an `UP-TO-DATE` Gradle result is not sufficient evidence that tests executed. When necessary, the Reviewer should force execution, such as using the project's appropriate rerun mechanism.

---

# 8. Agent permission model

Permissions are defined in the project-level `opencode.json`.

The permission boundary is deliberately restrictive.

## 8.1 General model

Every agent's edit configuration starts with a catch-all deny:

```json
"*": "deny"
```

Specific paths are then explicitly allowed.

This creates a default-deny model for the `edit` tool.

### Important limitation

OpenCode permissions are path/tool based. They do **not** provide semantic ownership such as:

> "Agent may edit only §7 of this Markdown file."

Therefore:

- technical permissions enforce file/path boundaries;
- behavioral instructions enforce section ownership and role boundaries.

Both are necessary.

---

# 9. Orchestrator permissions

The Orchestrator is permitted to edit:

```text
task.md
PROJECT_STATUS.md
ISSUES.md
```

Its edit boundary is effectively:

```text
edit:
  "*": "deny"
  "task.md": "allow"
  "PROJECT_STATUS.md": "allow"
  "ISSUES.md": "allow"
```

The Orchestrator can use the `task` mechanism to delegate to:

- Architect;
- Developer;
- Reviewer.

It cannot commit or push:

```text
git commit* → deny
git push*   → deny
```

The Orchestrator therefore coordinates Git state but does not control project history.

---

# 10. Architect permissions

Architect can edit:

```text
task.md
docs/architecture/decisions/DECISIONS.md
```

Conceptually:

```text
edit:
  "*": "deny"
  "task.md": "allow"
  "docs/architecture/decisions/DECISIONS.md": "allow"
```

Architect cannot:

- modify application implementation;
- modify arbitrary documentation;
- modify `opencode.json`;
- invoke other agents;
- commit;
- push.

The explicit ADR exception exists because Architect is the authoritative owner of architectural decisions.

---

# 11. Developer permissions

Developer can edit:

```text
task.md
app/**
```

Conceptually:

```text
edit:
  "*": "deny"
  "task.md": "allow"
  "app/**": "allow"
```

This means the Developer can implement Momentum application changes but cannot directly modify the infrastructure configuration, project governance files, or ADRs.

Developer cannot:

- modify `opencode.json`;
- modify arbitrary `docs/**`;
- modify `AGENTS.md`;
- modify agent definitions;
- modify project status/issue registries;
- invoke another agent;
- commit;
- push.

---

# 12. Reviewer permissions

Reviewer can edit only:

```text
task.md
```

Conceptually:

```text
edit:
  "*": "deny"
  "task.md": "allow"
```

Reviewer is intentionally unable to modify application code.

Reviewer also cannot:

- invoke another agent;
- use the `task` tool;
- commit;
- push.

The Reviewer is therefore structurally separated from the implementation it is reviewing.

---

# 13. Git boundary

All four agents have:

```text
git commit* → deny
git push*   → deny
```

This is deliberate.

The workflow is:

```text
Agents
  │
  ├── inspect Git state
  ├── modify permitted files
  └── verify
          │
          ▼
       HUMAN
          │
          ├── review
          ├── commit
          └── push
```

The human retains final control over project history.

Agents may inspect status/diff/log as allowed, but they do not get to turn a verified working tree into a committed or pushed change automatically.

---

# 14. Human-controlled permission boundary

`opencode.json` is intentionally **not writable by any normal project agent**.

This is important because otherwise an agent could potentially weaken its own restrictions.

The human is the owner of:

- agent permission boundaries;
- permission changes;
- enabling/disabling capabilities;
- commit/push decisions.

Temporary permission grants used during infrastructure bootstrap/testing must be removed after the task.

---

# 15. External-directory behavior

OpenCode has a separate `external_directory` permission concept for paths outside the worktree.

The current project configuration contains narrow OpenCode-related external-directory rules, with a catch-all ask rule.

The important distinction discovered during TASK-005 is:

### `external_directory` is not the same as `edit`

`external_directory` governs access to paths outside the project boundary.

`edit` governs whether an agent can use the edit/write mechanism on a permitted path.

Therefore:

```text
external_directory allow
        ≠
edit allow
```

An external path can pass one gate and still fail another.

### Bash is a special consideration

TASK-005 demonstrated that Bash-mediated filesystem access did not behave like the `edit` tool's path restrictions. In particular, Bash writes outside the explicit edit allow-list succeeded during the probes.

Therefore the system **must not claim that `external_directory` or `edit` alone constitutes a complete sandbox for Bash filesystem access**.

This is a known limitation of the current setup and should be treated accordingly.

Do not broaden external access merely to make an unrelated Bash probe pass.

---

# 16. `task.md` — canonical active-task state

`task.md` is the canonical state for the **current task**.

It is not intended to become a permanent historical log.

Current sections are:

1. Task Identity
2. Objective
3. Scope
4. Requirements
5. Knowledge References
6. Current Issues
7. Architectural Decision — Architect
8. Implementation Plan — Developer
9. Implementation Notes — Developer
10. Review Findings — Reviewer
11. Verification — Reviewer
12. Handoff History
13. Final Outcome

### Behavioral ownership

| Section | Owner |
|---|---|
| Task Identity | Orchestrator |
| Objective | Orchestrator |
| Scope | Orchestrator |
| Requirements | Orchestrator |
| Knowledge References | Orchestrator |
| Current Issues | Orchestrator |
| Architectural Decision | Architect |
| Implementation Plan | Developer |
| Implementation Notes | Developer |
| Review Findings | Reviewer |
| Verification | Reviewer |
| Handoff History | Orchestrator |
| Final Outcome | Orchestrator |

The Orchestrator coordinates the whole document, but specialized agents should only modify their assigned sections.

This is a **behavioral ownership rule**, because OpenCode cannot enforce Markdown section-level permissions.

---

# 17. Handoff contract

Every specialist delegation should contain:

```text
OBJECTIVE:
SCOPE:
OUT_OF_SCOPE:
ACCEPTANCE_CRITERIA:
RELEVANT_FILES:
KNOWLEDGE_IDS:
ISSUE_IDS:
CONSTRAINTS:
REQUIRED_OUTPUT:
```

This prevents the receiving agent from having to reconstruct the task from conversation history.

---

# 18. Handoff result contract

Every specialist result should provide concrete state, including:

```text
RESULT:
DECISIONS:
FILES_CHANGED:
KNOWLEDGE_USED:
TESTS_RUN:
BLOCKERS:
NEXT_AGENT_ACTION:
```

If a field has no value, use:

```text
None
```

Results should not be vague.

---

# 19. Knowledge system

The project uses targeted knowledge IDs rather than requiring agents to read a giant knowledge base.

Canonical index:

```text
knowledge/index.md
```

Current knowledge ID families include:

### Architecture

```text
KNOW-ARCH-001
KNOW-ARCH-002
KNOW-ARCH-003
KNOW-ARCH-004
```

### Business

```text
KNOW-BUSINESS-001
KNOW-BUSINESS-002
KNOW-BUSINESS-003
```

### UI

```text
KNOW-UI-001
KNOW-UI-002
KNOW-UI-003
```

### Data

```text
KNOW-DATA-001
KNOW-DATA-002
KNOW-DATA-003
```

### Offline

```text
KNOW-OFFLINE-001
KNOW-OFFLINE-002
KNOW-OFFLINE-003
```

### Testing

```text
KNOW-TEST-001
```

### Features

```text
KNOW-FEAT-001
KNOW-FEAT-002
KNOW-FEAT-003
```

The index contains 20 knowledge IDs pointing to 11 distinct existing documentation sources.

### Retrieval rule

Do not read the entire knowledge system.

Instead:

```text
knowledge/index.md
        ↓
identify required KNOW-* IDs
        ↓
retrieve only those sources
        ↓
pass only relevant context to the agent
```

If required knowledge does not exist:

- do not invent it;
- do not substitute unrelated documentation;
- identify the missing knowledge;
- determine whether work can safely continue;
- otherwise stop and report the blocker.

---

# 20. `ISSUES.md` — canonical issue registry

`ISSUES.md` is the canonical registry of known issues.

Stable IDs use:

```text
ISSUE-NNN
```

The Orchestrator owns the registry.

### Creating an issue

When a new unresolved issue is discovered:

1. Search for an existing matching issue ID.
2. If one exists, use it.
3. Otherwise create a new `ISSUE-NNN`.
4. Record sufficient evidence.

### Closing an issue

A discovered fix does not automatically mean the issue is closed.

The lifecycle is:

```text
OPEN
  ↓
implementation
  ↓
verification
  ↓
CLOSED
```

The issue should be closed only when its verification criteria are satisfied.

---

# 21. `PROJECT_STATUS.md` — canonical project status

`PROJECT_STATUS.md` tracks verified implementation status at the project level.

The Orchestrator owns it.

Allowed statuses:

```text
NOT_IMPLEMENTED
IN_PROGRESS
IMPLEMENTED_NOT_TESTED
PARTIALLY_VERIFIED
FULLY_VERIFIED
BLOCKED
```

### Critical rule

Developer output alone cannot justify:

```text
FULLY_VERIFIED
```

That status requires the appropriate verification evidence.

This prevents the project status document from becoming a collection of optimistic implementation claims.

---

# 22. ADR system

Application architectural decisions belong under:

```text
docs/architecture/decisions/
```

The canonical decision document is:

```text
docs/architecture/decisions/DECISIONS.md
```

The Architect owns architectural decision authoring.

### Flow

```text
Task
 ↓
Architect analyzes
 ↓
Architect records decision in task.md
 ↓
Architect creates/updates ADR when warranted
 ↓
Developer consumes approved decision
 ↓
Reviewer verifies implementation against decision
```

The multi-agent system itself is **not application architecture**. Its documentation belongs under `docs/development/`.

---

# 23. `AGENTS.md`

`AGENTS.md` is the top-level project instruction and governance contract.

It remains authoritative over the agent-specific prompts.

The multi-agent system is documented in its dedicated section, currently under the multi-agent development system section of `AGENTS.md`.

Important principle:

> Agent-specific prompts specialize the roles; they do not override `AGENTS.md`.

The multi-agent system also respects the project's existing Android architecture, testing, Git, offline-first, UI/UX, and other development requirements.

---

# 24. Agent definition files

The four custom agent definitions live under:

```text
.opencode/agents/
```

with:

```text
orchestrator.md
architect.md
developer.md
reviewer.md
```

Their purposes are:

| File | Agent | Purpose |
|---|---|---|
| `orchestrator.md` | Orchestrator | Routing, coordination, task/project state |
| `architect.md` | Architect | Architectural analysis and decisions |
| `developer.md` | Developer | Approved implementation |
| `reviewer.md` | Reviewer | Independent verification |

These files contain the behavioral instructions for each role.

The actual technical permission boundary is configured separately in:

```text
opencode.json
```

This separation is intentional.

---

# 25. `opencode.json`

`opencode.json` is the project-level technical permission configuration.

It defines the hard tool/path boundaries for the four agents.

It is **human-controlled infrastructure**.

Agents must not modify it during normal development.

The important distinction is:

```text
Agent prompt / AGENTS.md
        =
behavioral rules

opencode.json
        =
technical permission boundary
```

The prompt can say:

> "Only modify the approved files."

The permission configuration can enforce:

> "Developer cannot edit anything except task.md and app/**."

The strongest system uses both.

---

# 26. Permissions matrix

The high-level ownership model is also documented in:

```text
docs/architecture/MULTI_AGENT_PERMISSIONS.md
```

That file is the canonical permissions/ownership matrix.

It covers:

- all four agents;
- task.md sections;
- project artifacts;
- read/write/deny boundaries;
- Git boundaries;
- behavioral vs technical ownership;
- human-controlled configuration.

The matrix is complementary to this document:

- **permissions matrix:** precise reference table;
- **this document:** explanation of how the entire development system works.

---

# 27. What is technically enforced vs behaviorally enforced?

This distinction is fundamental.

## Technically enforced

OpenCode permission configuration can enforce things such as:

- which paths an agent can edit;
- whether an agent can invoke another agent;
- whether an agent can use particular tools;
- whether Git commit/push commands are denied;
- external-directory access rules.

Examples:

```text
Developer cannot edit arbitrary docs.
Reviewer cannot edit app code.
Agents cannot commit/push.
```

## Behaviorally enforced

OpenCode does not provide semantic ownership for:

- one section of a Markdown file;
- one subsection of a document;
- "only investigate this subsystem";
- "don't redesign unrelated behavior";
- "don't use all agents unnecessarily";
- "use only these knowledge IDs."

Those rules live in:

- `AGENTS.md`;
- agent prompts;
- `task.md`;
- the permissions matrix;
- the development process itself.

Therefore a robust system requires both layers.

---

# 28. Correction cycle

When Reviewer finds actionable defects:

```text
Reviewer
   ↓
Orchestrator records findings
   ↓
Developer receives only actionable corrections
   ↓
Developer fixes
   ↓
Reviewer verifies again
```

The system permits at most **two correction cycles after validation** for the same task.

If the implementation still fails after the permitted correction cycles:

- stop;
- report the unresolved failure;
- do not enter an infinite Developer ↔ Reviewer loop.

---

# 29. Blocker handling

When an agent reports a blocker:

1. Determine whether another existing specialist can resolve it.
2. If yes, delegate only the specific blocker.
3. If no, report it to the user.

Do not repeatedly send agents against the same blocker without changing the evidence or approach.

Do not invent missing information.

---

# 30. Stop conditions

The system should stop when:

1. Acceptance criteria are satisfied and required verification passes.
2. The assigned specialist's work is complete and another specialist must continue.
3. The task is blocked.
4. Required information is unavailable.
5. User clarification is required.
6. Further work would be outside scope.

After completion, agents should not continue with:

- unrelated refactoring;
- unrelated UI improvements;
- unrelated investigations;
- new tasks;
- unnecessary agent invocations;
- reconsideration of completed decisions.

Stopping is a feature of the system, not a failure.

---

# 31. Final completion checklist

Before the Orchestrator declares a task complete:

- [ ] Objective satisfied.
- [ ] Scope respected.
- [ ] Acceptance criteria satisfied.
- [ ] Required implementation completed.
- [ ] Required tests actually executed.
- [ ] Reviewer result received when required.
- [ ] Actionable findings resolved.
- [ ] Relevant `ISSUES.md` entries updated.
- [ ] `PROJECT_STATUS.md` updated if status changed.
- [ ] `task.md` contains final outcome.
- [ ] No unrelated working-tree changes were discarded.
- [ ] No unrequested commit/push occurred.

---

# 32. Known lessons from system validation

The multi-agent infrastructure itself was tested through TASK-001 through TASK-005.

## TASK-001 — architecture investigation

Purpose:

- test architecture investigation routing;
- validate targeted architectural analysis;
- avoid production changes.

Result:

- Orchestrator → Architect workflow worked.
- Architecture layer separation was largely confirmed.
- Several real implementation/documentation ambiguities were identified.
- No production changes were made.

Important lesson:

> Architecture investigation can reveal inconsistencies without automatically turning every inconsistency into a code change.

---

## TASK-002 — simple implementation workflow

Purpose:

- validate the simple implementation path;
- prove Developer → Reviewer separation;
- add a focused test without production changes.

Result:

- Developer added `ActivityValidationTest`.
- Reviewer verified the intended invariants.
- The initial test run was `UP-TO-DATE`, so the Reviewer correctly reran with forced execution.
- Tests then executed successfully.

Important lesson:

> A cached/UP-TO-DATE test result is not sufficient proof that the test actually ran.

---

## TASK-003 — architecture-heavy implementation

Purpose:

- validate Architect → Developer → Reviewer;
- perform a real architecture-related implementation.

Result:

- Architect selected a concrete `InsightsViewModel` DI convergence.
- Developer made a narrowly scoped implementation.
- Reviewer independently verified the change against acceptance criteria.
- No unrelated production changes were introduced.

Important lessons:

- Architectural decisions must be explicit before implementation.
- Reviewer must verify actual changed behavior rather than relying blindly on a green suite.
- Test-count/report anomalies should not be accepted without evidence.
- ADR ownership needed clarification and was subsequently assigned to Architect.

---

## TASK-004 — permission and ownership infrastructure

Purpose:

- establish and validate the technical permission boundary;
- document the permissions matrix;
- ensure agents cannot modify their own permission boundary.

Result:

- `docs/architecture/MULTI_AGENT_PERMISSIONS.md` was created.
- Architect received the narrowly scoped ADR write permission.
- Developer's temporary bootstrap grants were removed after infrastructure work.
- Git commit/push remained denied for all four agents.
- The permission configuration remained human-controlled.
- The system validated the distinction between technical path permissions and behavioral section ownership.

Important lesson:

> Permission bootstrap is itself a privileged operation and should remain human-controlled.

---

## TASK-005 — temporary ADR lifecycle / restoration test

Purpose:

- test the ADR lifecycle without risking real application changes;
- test creation, consumption, verification, cleanup, and exact restoration;
- investigate permission behavior around OpenCode temporary files.

Result:

- Temporary ADR was created, consumed, and removed.
- `task.md` and `DECISIONS.md` were restored byte-for-byte.
- Hash verification exposed a line-ending mutation that ordinary `git diff` did not make obvious.
- Restoration therefore used verified backup content and SHA-256 comparison.
- No application files were changed.
- Git HEAD/index remained unchanged.
- The external-directory investigation showed that `edit`, `external_directory`, and Bash-mediated filesystem access must not be conflated.

Important lesson:

> For temporary infrastructure work, exact byte-level restoration is stronger evidence than `git diff`, file size, or line count alone.

---

# 33. Important operational lessons

## 33.1 Never trust `git diff` alone for exact restoration

Line-ending changes can be hidden or represented unexpectedly depending on Git configuration.

For infrastructure cleanup:

```text
hash + byte count + byte comparison
```

is the reliable standard.

---

## 33.2 Do not assume a green test suite proves changed behavior

A suite may:

- be cached;
- include unexpected report artifacts;
- omit the changed path;
- contain tests that do not discriminate the intended failure.

Reviewers must inspect what actually executed and whether the test proves the acceptance criterion.

---

## 33.3 Do not broaden permissions speculatively

When a permission failure occurs, identify which gate caused it before changing the configuration.

In particular:

```text
external_directory
edit
bash
```

must be treated as distinct mechanisms.

---

## 33.4 Permission boundaries should remain human-controlled

Agents should never be able to rewrite the rules that govern their own authority.

---

## 33.5 Do not use every agent just because the system has four agents

The system is deliberately dynamic.

A one-line UI fix does not need an Architect.

A verification-only request does not need a Developer.

An architecture investigation does not need a Developer unless implementation is actually required.

---

# 34. Recommended workflow for every new Momentum task

## Step 1 — User defines the desired outcome

The Orchestrator determines:

- what needs to change;
- what "done" means;
- what must not change.

---

## Step 2 — Classify the task

Determine whether it is:

```text
INVESTIGATION
ARCHITECTURE
IMPLEMENTATION
VERIFICATION
```

or a combination.

---

## Step 3 — Establish task state

Create/update:

```text
task.md
```

with:

- objective;
- scope;
- requirements;
- acceptance criteria;
- relevant knowledge;
- relevant issues.

---

## Step 4 — Retrieve targeted knowledge

Use:

```text
knowledge/index.md
```

Find the required `KNOW-*` IDs.

Do not load the entire knowledge base.

---

## Step 5 — Route minimum agents

Examples:

```text
Simple bug:
Orchestrator → Developer → Reviewer

Architecture change:
Orchestrator → Architect → Developer → Reviewer

Architecture-only:
Orchestrator → Architect

Verification-only:
Orchestrator → Reviewer
```

---

## Step 6 — Architect, when required

Architect:

- studies only the relevant context;
- decides the approach;
- records the architectural decision;
- identifies exact implementation files;
- defines validation.

---

## Step 7 — Developer implements

Developer receives the approved plan and:

- changes only approved files;
- implements only approved scope;
- validates the affected behavior;
- records implementation status.

---

## Step 8 — Reviewer verifies

Reviewer independently checks:

- requirements;
- implementation;
- architecture;
- regression risk;
- actual validation evidence.

If defects exist, return only the actionable corrections to Developer.

---

## Step 9 — Update project state

Orchestrator updates:

```text
ISSUES.md
PROJECT_STATUS.md
task.md
```

as applicable.

---

## Step 10 — Stop

Once the task is genuinely verified:

```text
STOP
```

Do not turn a completed task into an opportunity for unrelated cleanup.

---

# 35. Directory map

The development infrastructure is conceptually organized like this:

```text
Momentum/
│
├── AGENTS.md
│   └── Project-wide rules + multi-agent governance
│
├── opencode.json
│   └── Human-controlled technical permission boundary
│
├── task.md
│   └── Canonical current-task state
│
├── PROJECT_STATUS.md
│   └── Canonical project implementation status
│
├── ISSUES.md
│   └── Canonical issue registry
│
├── knowledge/
│   └── index.md
│       └── KNOW-* knowledge routing index
│
├── .opencode/
│   └── agents/
│       ├── orchestrator.md
│       ├── architect.md
│       ├── developer.md
│       └── reviewer.md
│
└── docs/
    ├── architecture/
    │   ├── decisions/
    │   │   └── DECISIONS.md
    │   └── MULTI_AGENT_PERMISSIONS.md
    │
    └── development/
        └── MULTI_AGENT_SYSTEM.md   ← this document
```

The important conceptual separation is:

```text
docs/architecture/
    = How the Momentum application is designed.

docs/development/
    = How the Momentum application is built and maintained.
```

---

# 36. What this system gives us

The resulting development model has four independent responsibilities:

```text
             ORCHESTRATOR
          "What needs to happen?"
                    │
                    ▼
               ARCHITECT
          "How should it be designed?"
                    │
                    ▼
               DEVELOPER
            "Implement it."
                    │
                    ▼
                REVIEWER
            "Did it actually work?"
```

with:

```text
task.md
    = current task state

knowledge/index.md
    = targeted project knowledge

ISSUES.md
    = unresolved/resolved issue registry

PROJECT_STATUS.md
    = verified project status

DECISIONS.md
    = architectural decisions

opencode.json
    = technical permission boundary

AGENTS.md
    = authoritative project/process rules
```

The system is intentionally designed so that **no single agent is trusted to decide, implement, and certify its own work without appropriate separation**.

The human remains the final authority over:

- permission boundaries;
- destructive or materially breaking changes;
- Git history;
- commits;
- pushes;
- ambiguous product decisions.

---

# 37. Final principle

The system is not intended to maximize agent activity.

It is intended to maximize:

**clarity → bounded work → independent verification → evidence → stop.**

The correct outcome is not:

> "Four agents worked on the task."

The correct outcome is:

> **"Only the agents required for the task worked on it, within explicit boundaries, and the result was independently verified."**
