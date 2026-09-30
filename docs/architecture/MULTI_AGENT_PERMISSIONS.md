# Multi-Agent Permissions and Ownership

> **Status:** Canonical reference for the Momentum multi-agent operating system.
> **Governing rule:** `AGENTS.md` §14.
> **Technical enforcement:** `opencode.json` (project root).
> **Single source of truth:** this file. The four agent definitions in
> `.opencode/agents/*.md` reference this document by path and MUST NOT duplicate
> the matrix.

---

## 1. Purpose

Four specialist agents share one repository. Without an explicit ownership map
they overwrite each other's work, two agents claim the same file, and the
Human-in-the-Loop Git boundary degrades into suggestion. This document is the
one place that answers "who may read this, who may write it, and who is
prohibited from touching it at all".

This document states **intent and policy**. `opencode.json` states
**enforcement**. Both are required: a matrix nobody enforces is advisory, and an
enforcement file nobody can interpret is a puzzle.

---

## 2. Legend

| Symbol | Meaning |
| --- | --- |
| `R` | May read. |
| `W` | May modify. |
| `DENY` | Technically prohibited. The agent cannot perform this even if it attempts to; the operation is refused at the tool level. |
| `—` | Outside the agent's responsibility. The agent has no role in this artifact and no permission is granted or withheld on its account. |

Notes on how to read a cell:

- `R` never implies `W`. An agent that may read an artifact MUST NOT modify it
  unless the cell says `W`.
- For the twelve `task.md` section rows, `DENY` denotes a **behavioral**
  prohibition, not a technical one, because OpenCode path permissions are
  file-level and cannot address Markdown sections. See §5.
- For the Git rows, `R` means "permitted to inspect".

---

## 3. Permissions matrix

| Artifact / capability | Orchestrator | Architect | Developer | Reviewer/Tester |
| --- | --- | --- | --- | --- |
| `task.md` — Task Identity | `W` | `R` | `R` | `R` |
| `task.md` — Objective | `W` | `R` | `R` | `R` |
| `task.md` — Scope | `W` | `R` | `R` | `R` |
| `task.md` — Requirements | `W` | `R` | `R` | `R` |
| `task.md` — Knowledge References | `W` | `R` | `R` | `R` |
| `task.md` — Current Issues | `W` | `R` | `R` | `R` |
| `task.md` — Architectural Decision | `R` | `W` | `R` | `R` |
| `task.md` — Implementation Plan | `R` | `R` | `W` | `R` |
| `task.md` — Implementation Notes | `R` | `R` | `W` | `R` |
| `task.md` — Review Findings | `R` | `R` | `R` | `W` |
| `task.md` — Verification | `R` | `R` | `R` | `W` |
| `task.md` — Handoff History | `W` | `R` | `R` | `R` |
| `task.md` — Final Outcome | `W` | `R` | `R` | `R` |
| `PROJECT_STATUS.md` | `W` | `DENY` | `DENY` | `DENY` |
| `ISSUES.md` | `W` | `DENY` | `DENY` | `DENY` |
| `docs/architecture/decisions/DECISIONS.md` | `R` | `W` | `R` | `R` |
| `knowledge/**` | `R` | `R` | `R` | `R` |
| `app/**` — production code | `R` | `R` | `W` | `R` |
| `app/**` — tests | `R` | `DENY` | `W` | `R` |
| `AGENTS.md` | `DENY` | `DENY` | `DENY` | `DENY` |
| `.opencode/**` — agent definitions | `DENY` | `DENY` | `DENY` | `DENY` |
| Git inspection (`git status`, `git diff`, `git log`, `git show`, other non-mutating) | `R` | `R` | `R` | `R` |
| Git commit (`git commit`, including `-m …` and `--amend`) | `DENY` | `DENY` | `DENY` | `DENY` |
| Git push (`git push`, including `git push origin main`) | `DENY` | `DENY` | `DENY` | `DENY` |
| Subagent delegation (`task` tool) | `W` | `DENY` | `DENY` | `DENY` |

Every agent may read every artifact. Read access is not the contested resource;
write access and delegation are.

### 3.1 Read-only that MUST NOT become write

The following are `R` in the matrix and are therefore **read-only for that
agent**. An agent that needs to change one of these artifacts does not change
it; it reports the required change to its owner.

- `docs/architecture/decisions/DECISIONS.md` is read-only for the Orchestrator,
  the Developer, and the Reviewer/Tester. Only the Architect writes it.
- `knowledge/**` is read-only for **all four** agents, including the
  Orchestrator. Knowledge files are authored by the human or by a task that
  explicitly assigns knowledge authoring; no agent writes them as a side
  effect.
- `app/**` production code and `app/**` tests are read-only for the
  Orchestrator, the Architect, and the Reviewer/Tester. The Orchestrator is
  additionally barred from implementing (`AGENTS.md` §16); the Reviewer/Tester
  is barred from fixing what it finds, and reports instead.
- The `AGENTS.md`, `.opencode/**`, `PROJECT_STATUS.md`, and `ISSUES.md` rows are
  `DENY` for the three specialist agents: those are hard tool-level
  prohibitions, not merely duties.

---

## 4. Ownership by role

### 4.1 Orchestrator

The Orchestrator is a **router and coordinator**. It is not a mandatory
participant in every specialist decision; it routes to the minimum required set
of agents and then evaluates the result.

Owns:

- workflow coordination, task classification, and routing;
- the `Task Identity`, `Objective`, `Scope`, `Requirements`,
  `Knowledge References`, `Current Issues`, `Handoff History`, and
  `Final Outcome` sections of `task.md`;
- `PROJECT_STATUS.md` — the only agent permitted to modify it;
- `ISSUES.md` — the canonical issue registry;
- subagent delegation via the `task` tool.

MUST NOT:

- own or author architectural decisions or ADR records. ADR authorship belongs
  to the Architect (§6);
- implement application code (`AGENTS.md` §16);
- modify `AGENTS.md` or `.opencode/**`;
- commit or push (§7).

### 4.2 Architect

The Architect is the **authoritative owner of architectural decisions and ADR
records**.

Owns:

- the `Architectural Decision` section of `task.md`;
- creating and updating the canonical ADR entries in
  `docs/architecture/decisions/DECISIONS.md` — the only agent with a technical
  write grant on that file;
- the classification of whether a task requires ARCHITECTURE, and the content
  of any architectural decision that results.

MUST NOT:

- implement application code, UI, business logic, database code, or tests;
- modify `PROJECT_STATUS.md`, `ISSUES.md`, or `knowledge/**`;
- modify `AGENTS.md` or `.opencode/**`;
- modify ADR entries unrelated to the decision it was asked to make (§6.3);
- delegate to another agent;
- commit or push (§7).

### 4.3 Developer

The Developer **consumes** architectural decisions. It never authors or amends
one.

Owns:

- implementation changes under `app/**`, including production code and tests
  required by the assigned task;
- the `Implementation Plan` and `Implementation Notes` sections of `task.md`.

MUST NOT:

- create, update, or delete ADR entries in
  `docs/architecture/decisions/DECISIONS.md`, or make independent architectural
  decisions where the task requires architectural approval;
- modify `PROJECT_STATUS.md`, `ISSUES.md`, or `knowledge/**`;
- modify `AGENTS.md` or `.opencode/**`;
- close or reopen issues independently;
- delegate to another agent;
- commit or push (§7).

### 4.4 Reviewer/Tester

The Reviewer/Tester **verifies** the implementation against the recorded
architectural decision. It never modifies that decision.

Owns:

- the `Review Findings` and `Verification` sections of `task.md`.

MUST NOT:

- modify application code or tests, including to make a failing test pass;
- modify the ADR, `PROJECT_STATUS.md`, `ISSUES.md`, or `knowledge/**`;
- modify `AGENTS.md` or `.opencode/**`;
- change acceptance criteria or task scope;
- close or reopen issues;
- delegate to another agent;
- commit or push (§7).

---

## 5. Behavioral versus technical enforcement

> Section-level ownership inside `task.md` is enforced behaviorally through
> agent instructions; OpenCode permissions enforce file/path-level boundaries.

This is the single most important caveat in this document, so it is stated
plainly:

- **Technical enforcement** is `opencode.json`. It matches glob patterns
  against worktree-relative paths and tool names, and the **last matching rule
  wins**. A catch-all `"*": "deny"` therefore MUST appear *before* narrower
  `allow` rules in every `edit` block. This layer is unforgiving: a denied call
  is refused by the tool, and no amount of intent changes the outcome.
- **Behavioral enforcement** is the agent instructions in
  `.opencode/agents/*.md` plus `AGENTS.md` §14.7. It covers everything the
  technical layer structurally cannot express: which sections of `task.md` an
  agent may edit, which existing entries inside `DECISIONS.md` it may leave
  alone, and which of several read-only artifacts a given role is reasoning
  about.

Consequence: an agent can hold a technical write grant on `task.md` and still
be forbidden by the behavioral layer from editing the `Objective` section. The
grant is file-level; the prohibition is section-level; the behavioral layer is
the one that resolves it.

---

## 6. ADR ownership and the `DECISIONS.md` append-only rule

The Architect is the authoritative owner of architectural decisions and ADR
records.

### 6.1 Five-step flow

When a decision qualifies for an ADR:

1. The Architect makes the decision.
2. The Architect records the decision in `task.md`, in the
   `Architectural Decision` section.
3. The Architect creates or updates the canonical ADR entry in
   `docs/architecture/decisions/DECISIONS.md`.
4. The Developer implements the approved decision.
5. The Reviewer independently verifies the implementation against the decision.

Steps 2 and 3 are both required. A decision recorded only in `task.md` is not
yet durable; a decision recorded only in `DECISIONS.md` is not yet the accepted
decision of the current task.

### 6.2 Scope of ADR authorship

An ADR is warranted only for a decision meeting the project's documented ADR
criteria. An agent MUST NOT create an ADR for every minor implementation
detail. Conversely, `AGENTS.md` §3 and §12 already require that a replacement of
working architecture be proposed and recorded in the decisions document before
it is implemented — that requirement is unchanged, and the Architect is now the
named owner of the record.

### 6.3 Append-only, and no unrelated edits

`docs/architecture/decisions/DECISIONS.md` is **append-only**. New decisions are
new entries; existing entries are never silently edited or removed. The
Architect MUST NOT modify ADR entries unrelated to the decision it was asked to
make. Separate ADR files MUST NOT be created unless the existing project
convention explicitly requires one; the single canonical file is the convention.

---

## 7. Human-in-the-Loop Git boundary

> Agents may inspect Git state, but committing and pushing are
> Human-in-the-Loop operations. Only the human may perform them after
> reviewing the verified result.

**Allowed — read-only inspection, for all four agents:**

- `git status`
- `git diff`
- `git log`
- `git show`
- any other non-mutating inspection command.

**Forbidden — for all four agents:**

- `git commit`
- `git push`
- **any alternative command or workflow that creates a commit or performs a
  push.**

That last clause is the operative one. The prohibition is on the *outcome*, not
on the two spellings of it. It therefore also covers `git commit-tree`,
`git merge --commit`, `git rebase` that fabricates commits, writing to
`.git/refs` or an index by hand, driving `gh pr create` with a commit attached,
and using `bash` to perform any of the above.

`opencode.json` denies `git commit*` and `git push*` in the `bash` block of all
four agents. It deliberately does **not** deny `git *`, `git`, or `*`, because an
over-broad pattern would also block the read-only inspection that agents are
required to perform. A narrow deny-list is the correct enforcement shape here.

---

## 8. Bootstrap history and the one-time bootstrap exception

### 8.1 What happened

The multi-agent system could not bootstrap itself, because the very rules it
must establish also prevented the files that state those rules from being
written:

- **Pass 1.** The active `opencode.json` denied `edit` on `*` for every agent,
  granting only narrow paths, so the seven infrastructure files were
  unreachable by all four agents. The Developer's `bash` was unrestricted and
  could have written them; it refused, because routing around `edit` would have
  made the permission model cosmetic.
- **Pass 2.** The human unblocked the path in `opencode.json`, but the change
  was not in force — OpenCode reads permissions at session start, so an edit
  made after a session begins does not affect that session. The file also failed
  to parse. The Developer again refused the `bash` bypass and left every target
  file byte-untouched.
- **Pass 3.** The human corrected `opencode.json` to valid JSON using `deny`
  rather than `ask` for the Git rules, restored the Developer's `"*": "deny"`,
  added a **narrow temporary grant for the seven infrastructure paths**, and
  restarted the session.

### 8.2 The exception, and its removal

Under that temporary grant the Developer performed this one-time bootstrap. The
grant existed only so the system could create itself, and **the Developer
removed it as part of the deliverable**: the Developer's `edit` block in
`opencode.json` is back to exactly `"*": "deny"`, `task.md: allow`,
`app/**: allow`.

### 8.3 Non-precedential

**This exception is non-precedential and MUST NOT be cited as precedent.**

- It applied to exactly one task and exactly seven paths.
- It does not license any agent to edit `AGENTS.md`, `.opencode/**`, or
  `opencode.json` in any later task.
- The prohibitions in §3 are in force now and are not suspended.
- The correct way to change a permission is for the **human** to edit
  `opencode.json`.

### 8.4 Operational note

A change to `opencode.json` takes effect **only after the OpenCode session is
restarted**, because permissions are read at startup. A grant added mid-session
is inert, and a removed grant stays live until restart. Any future permission
change must account for this, and the change must be verified after a restart
rather than assumed.

---

## 9. Cross-references

| Concern | Authoritative location |
| --- | --- |
| Multi-agent operating rules, section ownership, knowledge system, issues, project status, handoff protocol, stop rule | `AGENTS.md` §14 (governing rule) |
| This permissions and ownership matrix | `docs/architecture/MULTI_AGENT_PERMISSIONS.md` (this file) |
| Technical enforcement of every `DENY` and `W` in §3 | `opencode.json` (project root) |
| Per-agent behavioral instructions | `.opencode/agents/orchestrator.md`, `.opencode/agents/architect.md`, `.opencode/agents/developer.md`, `.opencode/agents/reviewer.md` |
| ADR criteria and append-only decision history | `docs/architecture/decisions/DECISIONS.md` |
| Product and design documentation | `docs/` |

The four agent definitions **reference** this file. They do not restate the
matrix. If the two ever disagree, this file is the intent of record, `AGENTS.md`
§14 is the governing rule, and `opencode.json` is what the tool actually
enforces — report the disagreement rather than editing around it.
