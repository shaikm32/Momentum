# Project Status

## Purpose

Canonical project-level record of implemented and verified Momentum capabilities.

This file describes what exists and its verification state.

It is not a task log, issue tracker, architecture document, or knowledge base.

## Status Definitions

- `NOT_IMPLEMENTED` — capability does not exist.
- `IN_PROGRESS` — implementation is currently underway.
- `IMPLEMENTED_NOT_TESTED` — implementation exists but verification evidence is incomplete.
- `PARTIALLY_VERIFIED` — some required behavior has been verified, but verification is incomplete.
- `FULLY_VERIFIED` — implementation and required verification are complete.
- `BLOCKED` — progress is blocked by a known dependency or unresolved problem.

## Features

| Feature ID | Feature | Status | Evidence | Notes |
|---|---|---|---|---|
| F-HOME | Home dashboard — Phase 2C Aurora Hero (one-surface composition, readability, spacing, Reach a Target dialog) | FULLY_VERIFIED | TASK-006 Reviewer verification (`task.md` §11): A1 build OK, A2 259/0 unit tests, A3 instrumentation 7/7, A4(a)–(i) measured on emulator-5554 in Light AND Dark, A5 scope check, A6 root causes | Issues ISSUE-014/015/016/018/019 CLOSED. Decision record: ADR-37/38/39. Deferred MINOR: ISSUE-020 (date-label contrast, Light) |
| F-HABITS | Habits screen — Aurora collapsing Hero (Phase 2B artwork/composition/spacing) | FULLY_VERIFIED | TASK-006 Reviewer A4(h) re-verification: artwork at y=0 both themes (Light Δ−55…−63 / Dark Δ+55…+161 vs background), no seam at status-bar boundary, bottom fade intact, icons 7.87:1–15.90:1, copy clears status bar | ISSUE-017 CLOSED via ADR-39/§7.7; `HabitsHero.kt` composition unchanged by TASK-006 |
| F-CONCEPT-2A | Momentum Premium Redesign — **Phase 2A visual concept exploration**: 4 design directions (A *Press*, B *Instrument*, C *Loom*, D *Aurora, Governed* = controlled evolution), 5 key screens each, 20 image-generation briefs, Q1–Q14 mapped per direction | IMPLEMENTED_NOT_TESTED | `MOMENTUM_CONCEPT_EXPLORATION.md` (549,926 B · 8,008 lines) — structural/acceptance verification **18 CONFIRMED / 0 PARTIALLY / 0 REFUTED** (TASK-008 §11, final pass). **Verification is structural only: no image was generated and none could be.** No feature behaviour exists to verify | Deliberately **not** `FULLY_VERIFIED`: the deliverable is a design strategy, not implemented capability, and nothing in it is visually verified (no rendered reference exists in-repo — blocker B2). No application code changed; `git status` byte-identical to the TASK-007 baseline. **Superseded as the Phase 2B handoff by F-CONCEPT-2B** (Phase 2B was redefined from image generation to device-rendered Compose prototypes — TASK-009 `task.md` §1.1, `ADR-40` clause 1). Document unchanged and still authoritative on all four directions' visual language. Open inputs carried forward: `ISSUE-020`, `ISSUE-021`, `ISSUE-022`; Q6 icon-family conflict unresolved |
| F-CONCEPT-2B | Phase 2B Round 1 — **four device-rendered Compose Home/Today prototypes** (A *Press*, D *Aurora Governed*, B *Instrument*, C *Loom*) implementing briefs A1/D1/B1/C1, plus a content-only shared fixture and a debug-source-set-only preview host | FULLY_VERIFIED | TASK-009 §11 (final pass, `VERIFIED`): all four directions **PASS**; all 10 first-pass findings closed across 2 correction cycles. Every Developer-asserted figure independently re-measured from PNG bytes. `assembleDebug` SUCCESS; `testDebugUnitTest` **259 tests / 0 failures** re-executed with `--rerun` and counted from JUnit XML. **Non-convergence PASS** — 8 of §4.1's nine axes four-way distinct, 7 observable in a still (floor 7); cold read confirms all four identifiable unlabelled. **Production isolation proven**: both merged release manifests contain 0 × `Phase2BPreviewActivity` and 0 × `exploration`; `git diff --stat` shows only the 13 pre-existing files | Light only, by Round 1 design; Dark addability structurally confirmed but **not verified** (no dark render exists). Evidence: `docs/phase2b/round1/*.png` (4 PNGs, 1080×2400, AVD `Pixel_7`) — **retained** as the evidence backing this row after the code was removed. Architecture: `ADR-40` + Addenda 1–2, amended by **`ADR-46`** (TASK-014). **SUPERSEDED/TERMINATED (TASK-014):** the human ruled all four directions obsolete; the four prototype packages, the shared fixture/preview scaffolding and the entire `app/src/debug` source set (17 files) were **deleted**. The entry remains `FULLY_VERIFIED` for the work that *was* performed, but **no direction was ever selected and Round 2 will not proceed** — Round 2 is abandoned, not pending. `ISSUE-025` CLOSED as moot, `ISSUE-026` CLOSED as resolved-by-removal (its premise was already invalidated at TASK-011 Phase F when R8 began stripping the unreachable classes), `ISSUE-027` CLOSED as resolved-by-removal. `ISSUE-006` remains **OPEN** and is now scoped to production `HomeHero.kt` / `HabitsHero.kt` / `MomentumHeroArtwork.kt` only. *(The "259 tests" figure in the evidence column is TASK-009-era; the current verified suite is 41 files / 254 tests — see F-RELEASE.)* |

| F-RELEASE | Release hardening — **R8 minify + resource shrink, keep rules, baseline profile generation and packaging** | FULLY_VERIFIED | TASK-011 Phase F + TASK-012 Reviewer verification (`task.md` §12, §13): `:app:assembleRelease` SUCCESS (52 tasks, 43 executed; 4,237,684 B; **still unsigned** — no `signingConfigs`, no keystore). R8 full mode, **0 warnings**; 2 `Routes`/`Routes$*` keep rules proven necessary **and** sufficient at runtime (NavKey back-stack restored across process death on the minified APK). **Real baseline profile generated**: `app/src/main/generated/baselineProfiles/baseline-prof.txt` — 2,486,193 B, **23,427 rules** (1,649 `com/momentum/app`), journey = onboarding skip → Home → Habits → Tasks → Insights → Home + cold-start pass. Packaged in the release APK as `assets/dexopt/baseline.prof` 6,231 B + `baseline.profm` 805 B (stored), SHA-256 **byte-identical** to AGP's `binary_art_profile` release output; 228 `com.momentum.app` classes seeded at the R8 stage | **Baseline-only, no startup profile**: neither `collect()` block sets `includeInStartupProfile = true`, so `baseline.profm` carries no AOT startup rules (matches the approved §8.7.5 sample; deferred). Profile is a **committed generated artifact** — regenerate on a booted API 33+ device (`:baselineprofile:collectNonMinifiedReleaseBaselineProfile`) whenever the startup/navigation journey or R8 names change, or the build stays green while the profile silently stops matching. `profileinstaller 1.4.1` reads what is packaged. Release **signing** remains Human-in-the-Loop. ADR-45 |

## Update Rules

- The Orchestrator owns this file.
- Do not infer status without evidence.
- `FULLY_VERIFIED` requires Reviewer/Tester evidence.
- Do not remove historical feature entries merely when implementation changes.
- Keep entries concise.
- A documentation or design-strategy artifact cannot reach `FULLY_VERIFIED`. Use
  `IMPLEMENTED_NOT_TESTED` unless a capability is both implemented and
  independently verified; visual-concept material is additionally bounded by
  having no rendered evidence at all.
