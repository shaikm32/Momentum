# Knowledge Index

## Purpose

Entry point for targeted project knowledge retrieval.

This index maps stable knowledge IDs to specific knowledge files and sections.

Agents must retrieve only the knowledge required for the current task.

Do not turn this index into a large knowledge document.

## Knowledge IDs

| Knowledge ID | Topic | Source | Section | When Required |
|---|---|---|---|---|
| KNOW-ARCH-001 | Layer and package boundaries | `docs/architecture/ARCHITECTURE.md` | §2 Layers & packages; §3 Data flow (unidirectional) | Any change that adds or moves code into `presentation/`, `domain/`, `data/`, or `notification/` |
| KNOW-ARCH-002 | Component responsibilities and DI | `docs/architecture/ARCHITECTURE.md` | §5 Repositories; §6 Use cases; §7 ViewModels; §14 DI | Adding or changing a repository, use case, ViewModel, or `AppContainer` wiring |
| KNOW-ARCH-003 | Recurrence and analytics engines | `docs/architecture/ARCHITECTURE.md` | §10 Recurrence engine; §11 Analytics engine | Changing recurrence or streak/statistics engine behavior |
| KNOW-ARCH-004 | Architectural decision record | `docs/architecture/decisions/DECISIONS.md` | ADR-01 … ADR-35 (append-only) | Before changing architecture, persistence, DI, recurrence, or notification strategy. Locate the ADR by number with `grep`; do not read the file end to end. |
| KNOW-BUSINESS-001 | Activity domain concept | `docs/product/PRODUCT_SPEC.md` | §2 The Activity; §3 Activity fields (v1) | Any change to the Activity model, its types, or its fields |
| KNOW-BUSINESS-002 | Recurrence, completion, statistics | `docs/product/PRODUCT_SPEC.md` | §4 Recurrence; §5 Completion history; §6 Statistics & analytics | Changing recurrence rules, completion recording, or streak/statistics behavior |
| KNOW-BUSINESS-003 | v1 scope boundaries | `docs/product/PRODUCT_SPEC.md` | §9 Non-goals (v1); §10 Success criteria (v1) | Deciding whether a proposed change belongs in v1 |
| KNOW-UI-001 | Visual language and tokens | `docs/design/MOMENTUM_DESIGN_SYSTEM_V1.md` | §2 Design principles; §3 Theme; §4 Typography; §5 Spacing; §6 Shapes; §7 Surfaces and depth | Any Compose UI work; required before choosing a color, type style, spacing, or shape |
| KNOW-UI-002 | Motion, haptics, accessibility | `docs/design/MOMENTUM_DESIGN_SYSTEM_V1.md` | §28 Motion system; §29 Haptics and sound; §30 Accessibility | Adding animation, gestures, haptics, or any interactive element |
| KNOW-UI-003 | Rejected patterns and UI acceptance | `docs/design/MOMENTUM_DESIGN_SYSTEM_V1.md` | §33 Explicitly rejected patterns; §35 Design-system acceptance criteria | Reviewing UI work, or when a proposed UI pattern is not already defined |
| KNOW-DATA-001 | Room schema | `docs/architecture/DATA_MODEL.md` | Conventions; Tables; Relationships | Any entity, DAO, query, or column change |
| KNOW-DATA-002 | Activity edit semantics | `docs/architecture/DATA_MODEL.md` | Edit semantics for recurring activities (hard rule) | Any code that edits, archives, or deletes an activity or its completion records |
| KNOW-DATA-003 | Migration policy | `docs/architecture/DATA_MODEL.md` | §Migrations | Any schema change that requires a Room migration |
| KNOW-OFFLINE-001 | Offline-first and privacy requirements | `docs/product/PRODUCT_SPEC.md` | §7 Offline-first (HARD REQUIREMENT); §8 Privacy | Any change touching networking, sync, permissions, or on-device data storage |
| KNOW-OFFLINE-002 | Notification scheduling | `docs/architecture/NOTIFICATION_ARCHITECTURE.md` | Components; Behavior contracts; Manifest | Any change to reminders, alarms, WorkManager jobs, or manifest entries |
| KNOW-OFFLINE-003 | Privacy enforcement | `docs/architecture/PRIVACY_AND_SECURITY.md` | Enforcement; Data safety | Any change involving permissions, storage location, or user-data exposure |
| KNOW-TEST-001 | Test scope and commands | `docs/testing/TESTING_STRATEGY.md` | What we test and why; Conventions; Commands | Writing tests, or deciding which verification to run for a change |
| KNOW-FEAT-001 | Home dashboard behavior | `docs/product/features/dashboard/DASHBOARD_SPECS.md` | Status: Finalized; §1 Overview … §6 Home Hero | Work on the Home screen, goals, or Home header/hero |
| KNOW-FEAT-002 | Habits management UI | `docs/product/features/Habits/HABITS_MANAGEMENT_UI_SPEC.md` | §Status; §1 Habits Screen … §7 Streak | Work on the Habits screen, Habit Details, or weekly completion |
| KNOW-FEAT-003 | Create-item flow | `docs/product/features/create-item/CREATE_ITEM_SPECS.md` | §1 Overview; §2 Habit; §3 Task | Work on habit or task creation, templates, or schedule capture |

## Retrieval Rules

1. Identify the required knowledge ID before retrieval.
2. Retrieve only that knowledge item.
3. Do not load unrelated knowledge files.
4. Do not invent knowledge IDs.
5. If required knowledge does not exist, report the missing knowledge item.
6. Knowledge files should contain one coherent topic rather than broad project documentation.

## Naming Convention

Use stable IDs such as:

- `KNOW-ARCH-001`
- `KNOW-UI-001`
- `KNOW-DATA-001`
- `KNOW-BUSINESS-001`

IDs must remain stable once referenced by agents or task history.
