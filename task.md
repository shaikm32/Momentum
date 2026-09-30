# CURRENT TASK

> State of the currently active task. Not a history, knowledge base, or issue archive.
> Prior task (TASK-010, repository cleanup) is closed; its record lives in
> `docs/development/task_history/`.

---

## 1. TASK IDENTITY

<!-- ORCHESTRATOR ONLY -->

- Task ID: TASK-011
- Title: Compose-Kotlin Modernization (toolchain + architecture + Compose quality)
- Status: **COMPLETE / VERIFIED.** Six implementation phases (A toolchain,
  B Hilt, C Navigation 3, D MVI+Turbine, E1/E1b localization, E2 Compose quality,
  F release/R8) plus an Orchestrator-authorized E1b. Mid-point and final
  Reviewer verification both PASS; final pass additionally verified runtime on a
  Pixel_7 AVD (debug + minified release, 7/7 instrumentation tests). Nothing
  committed or pushed.
- Priority: High
- Created: 2026-09-29
- Last Updated: 2026-09-29
- Classification:
  - **TOOLING** — Kotlin/AGP/Compose/library version migration.
  - **ARCHITECTURE — required** — Navigation 3, Hilt DI adoption, MVI/UDF
    convention → ADR(s) required (AGENTS.md §4, §21).
  - **IMPLEMENTATION** — Compose modernization, strings/typography extraction,
    release/R8, state-pattern conformance.
  - **VERIFICATION** — build + unit tests + androidTest compile + targeted runs.
- Routing: **Orchestrator → Architect → Developer → Reviewer → Orchestrator.**
- Source of truth:
1. The task brief (human requirement, this session).
   2. `TASTE_MODERNIZATION_TARGET.md` (extracted target from installed skill) —
      **since deleted** as an obsolete working document; the decisions it drove
      are recorded in ADR-41…ADR-46 and §8 below.
   3. Installed skill `.opencode/skills/compose-kotlin/` (**compose-kotlin-agent-skills
      v2.2.0**) — the Android modernization authority.
   - The `Leonxlnx/taste-skill` entries in `skills-lock.json` are **web skills, NOT
     the Android authority**. `MODERNIZATION_AUDIT.md` (also since deleted) was
     likewise never authority.

---

## 2. OBJECTIVE

<!-- ORCHESTRATOR ONLY -->

Implement the Compose-Kotlin modernization to the installed skill's 2026 target:
toolchain upgrade (Kotlin 2.1.20/K2, AGP 9.0.0, JDK 17, Compose BOM 2025.05.00,
Lifecycle 2.9.0, Coroutines 1.10.2, Room 2.7.1, DataStore 1.1.7), Navigation 3
migration, Hilt DI migration, MVI/UDF conformance where the skill requires it,
Compose implementation-quality pass (strings/typography/state/list/a11y),
release/performance (R8, keep rules, profileinstaller). Preserve user-visible
behavior and Momentum's approved visual identity. No audit, no new planning doc —
implement.

---

## 3. SCOPE

<!-- ORCHESTRATOR ONLY -->

**In scope:**
- Version catalog + Gradle/AGP/Kotlin/KSP/JDK 17 toolchain migration (incl.
  Gradle wrapper bump only if AGP 9 requires it).
- Compose BOM, Lifecycle, Coroutines, Room, DataStore, WorkManager-compatible bumps.
- Navigation 3: NavKey, `@Serializable` type-safe routes, ID-only args,
  Nav3 back-stack/state model, bottom-nav behavior, existing deep-link/intent
  behavior preserved.
- Hilt migration (AppContainer → Hilt) — the skill's target DI architecture.
- MVI/UDF: apply per skill criteria screen-by-screen (see §7 Conflict 2).
- Compose pass: K2/modern compiler config, atomic state, `collectAsStateWithLifecycle`,
  lazy-list `key`/`contentType`, modifier ordering, state hoisting, side-effect
  discipline, semantics, ≥48dp targets, reduced-motion, Material 3 usage.
- Localization/typography: literal UI strings → `stringResource` resources; raw
  `sp`/`fontSize` → design-system typography tokens. **Wording/content unchanged.**
- Release/performance: `isMinifyEnabled`/`isShrinkResources` in release, keep
  rules (Room, kotlinx.serialization, Hilt), `profileinstaller 1.4.1` (baseline
  profile direction where applicable), recomposition fixes.
- Turbine 1.2.0 adoption for ViewModel tests where it replaces hand-rolled flow
  testing (justified per AGENTS.md §8).
- SDK: compileSdk/targetSdk 35+, edge-to-edge correctness. minSdk stays 26.

**Out of scope:**
- Another audit or another planning document.
- Screen redesigns / visual-identity changes / copy rewording.
- Unrelated product features, refactors, dependency additions Momentum does not
  need (networking, Paging, CameraX, Coil, Ktor, etc.).
- Multi-module split (skill RECOMMENDED-qualified "large apps" — NOT adopted;
  record as documented deviation only).
- Phase 2B `presentation/exploration/phase2b/**` (ADR-40) — touch ONLY if the
  modernization makes a strictly necessary compatibility change (e.g. compile
  error from toolchain), nothing else.
- Room destructive migrations, identity-hash manual fixes, schema rewrites.
- Commit/push (Human-in-the-Loop).

---

## 4. TARGET TOOLCHAIN (skill pins — do not substitute newer versions)

```toml
kotlin = "2.1.20"           # K2 only, no K1
agp = "9.0.0"
jvmTarget = "17"
composeBom = "2025.05.00"    # or newer *compatible stable* only if forced by AGP/Compose interplay — justify
navigation = "Navigation 3"  # see §7 Conflict 4 (artifact resolution by Architect)
kotlinx-serialization-json = "1.8.1"
room = "2.7.1"
lifecycle = "2.9.0"
coroutines = "1.10.2"
datastore = "1.1.7"
turbine = "1.2.0"
hilt = "2.56.2"              # + hilt-navigation-compose 1.2.0 (if A1 adopted)
ksp = "SEE §7 Conflict 1"    # KSP, never kapt
profileinstaller = "1.4.1"
compileSdk = 35, targetSdk = 35, minSdk = 26
release: isMinifyEnabled = true, isShrinkResources = true
```

---

## 5. BASELINE (recorded by Orchestrator, pre-modernization)

- Environment: JDK 21.0.12.1 on PATH (AGP 9 requires JDK 17+ → OK); Android SDK
  at `C:\Users\shaik\AppData\Local\Android\Sdk`; Gradle wrapper **8.9**.
- TASK-010 final green state: `assembleDebug` 38/38 executed;
  `testDebugUnitTest` 41 files / **253 tests / 0 failures / 0 errors**;
  `compileDebugAndroidTestKotlin` 29/29. Nothing committed (working tree holds
  all TASK-010 changes as staged/unstaged edits).
- Current catalog: AGP 8.7.3, Kotlin 2.0.21, Compose BOM 2024.09.00,
  navigation-compose 2.7.0, lifecycle 2.8.6, room 2.6.1, datastore 1.1.1,
  coroutines 1.9.0, work 2.9.0, KSP plugin inline `2.0.21-1.0.25`, no Hilt,
  no serialization, no Turbine, no profileinstaller, `minifyEnabled=false`.

---

## 6. KNOWLEDGE REFERENCES

- `.opencode/skills/compose-kotlin/SKILL.md` (toolchain table, MVI guardrails,
  mandatory defaults) + `skills/android-kotlin-{architecture,compose,testing}/SKILL.md`
  + `references/00,01,04,05,06,07,10,11,13,17,18-*.md`.
- `TASTE_MODERNIZATION_TARGET.md` §§3–7, §12, §13 (derived migration order) —
  **document since deleted** as an obsolete working extract; the conclusions it
  fed are recorded in §8 below and in ADR-41…ADR-46.
- `AGENTS.md` §3–§8, §11, §14, §21 (governance).
- ADR-40 (`docs/architecture/decisions/DECISIONS.md`) — Phase 2B retention.

---

## 7. CRITICAL CONFLICTS (must be resolved by Architect, not silently chosen)

1. **KSP pin:** skill shows `2.1.20-2.0.1` (ref 18) vs `2.1.20-1.0.32` (ref 05).
   Architect must determine which is actually supported with Kotlin 2.1.20 + the
   project's Room/KSP usage, by concrete evidence (published KSP release matrix /
   resolvability), and record the resolution + rationale.
2. **MVI vs MVVM:** skill has "MVI Contract (Mandatory)" (architecture sub-skill)
   vs "start MVVM; switch to MVI at 3+ interdependent state fields" (ref 01).
   Architect must classify **each Momentum screen** against the skill's actual
   criteria and prescribe: MVI contract where required, MVVM retained where the
   skill's own criteria say MVVM. Atomic `_state.update` + `collectAsStateWithLifecycle`
   are unambiguously REQUIRED everywhere.
3. **Hilt:** skill's Mandatory Default is `@HiltViewModel` + constructor inject;
   Momentum uses manual `AppContainer` (AGENTS.md forbids Hilt without an ADR).
   Task direction: migrate to Hilt if the skill's target architecture requires it
   — Architect decides, writes the ADR, plans the migration (incl. WorkManager /
   notification components and test impact).
4. **Navigation 3 artifact/API:** extraction lists `navigation-compose:2.9.0`
   while requiring Nav3 APIs (`NavKey`, `@Serializable`, Nav3 back-stack model).
   Architect must resolve the concrete artifact set that implements genuine
   Navigation 3 as the skill describes, using the skill's ref 07 as guidance,
   and record the resolution.

<!-- ARCHITECT: fill §8. DEVELOPER: fill §9. REVIEWER: fill §10. ORCHESTRATOR: §11. -->

---

## 8. ARCHITECTURAL DECISIONS AND PLAN

<!-- ARCHITECTURE OWNER -->

### 8.1 Resolutions of the §7 conflicts (ADR-41 … ADR-44)

**Conflict 1 — KSP pin → ADR-41.** Chosen `ksp = "2.2.10-2.0.2"` (KSP2-only) and
`kotlin = "2.2.10"`. Evidence: AGP 9.0.x release notes make built-in Kotlin the
default, require KGP ≥ 2.2.10 (Gradle auto-upgrades anything lower) and KSP ≥
2.2.10-2.0.2; Maven Central has no KSP1 build (`-1.0.32`) for Kotlin 2.2.10 —
only `2.2.10-2.0.2` (+ RCs) exists. Both skill pins (`2.1.20-1.0.32`,
`2.1.20-2.0.1`) therefore become unresolvable once Kotlin reaches the forced
floor. The `android.builtInKotlin=false` opt-out is rejected (preserves the
deprecated plugin path the skill/AGP 9 direction removes). kapt stays banned.

**Conflict 2 — MVI vs MVVM → ADR-44.** Criteria-driven classification (§8.3).
The skill's own trigger ("3+ interdependent user-mutable state fields **or**
multi-step flows", ref 01) governs *where* the full MVI contract applies;
atomic state mutation, `Channel` effects, `stateIn` and
`collectAsStateWithLifecycle` are unconditional for every screen.

**Conflict 3 — Hilt → ADR-43.** Adopt Hilt **2.57.2** and retire `AppContainer`
(Against AGENTS.md §4 an ADR is required — that is ADR-43; it also satisfies
ADR-09's "migrate only with a new ADR" clause). Version 2.56.2 (skill pin) is
superseded: Dagger 2.57 "unshades the Kotlinx Metadata to support Kotlin 2.2.0",
which is required to process Kotlin 2.2.10 sources, and 2.57.1/2.57.2 stop the
Hilt Gradle Plugin forcing Kotlin 2.2 on consumers. Fallback if Phase B fails:
2.60.1 (latest stable).

**Conflict 4 — Navigation 3 artifact set → ADR-42.** Chosen artifacts:
`androidx.navigation3:navigation3-runtime:1.1.0`,
`androidx.navigation3:navigation3-ui:1.1.0`,
`androidx.lifecycle:lifecycle-viewmodel-navigation3:2.10.0`, plus
`kotlinx-serialization-json:1.8.1` and the serialization Gradle plugin `2.2.10`.
The skill extraction's `navigation-compose:2.9.0` is **Nav2's** type-safe API
(ref 07: `rememberNavController`/`NavHost`/`toRoute`) and cannot express the
`NavKey`/`NavBackStack`/`NavDisplay` model the same skill marks REQUIRED — genuine
Nav3 governs (task §7.4). nav3 **1.1.0** chosen over 1.0.0 (early stable, superseded
by fixes) and over 1.2.0 (hard-requires compose runtime 1.11.2 → forces BOM
≥ 2026.06.01 for no feature Momentum needs). Bridge: Phase A keeps
`navigation-compose = 2.9.0` (skill's own pin; the 2.7.0 → target-compose gap is
two years); Phase C removes it.

### 8.2 Version catalog end-state (per phase)

| Key | From | To | Phase | Why / ADR |
|---|---|---|---|---|
| agp | 8.7.3 | **9.0.0** | A | skill REQUIRED; ADR-41 |
| gradle wrapper | 8.9 | **9.1.0** | A | AGP 9 min+default (verified 200); ADR-41 |
| kotlin | 2.0.21 | **2.2.10** | A | AGP 9 built-in-Kotlin floor (skill 2.1.20 superseded); ADR-41 |
| ksp | 2.0.21-1.0.25 | **2.2.10-2.0.2** | A | KSP2-only for 2.2.x (both skill pins superseded); ADR-41 |
| composeBom | 2024.09.00 | **2026.01.01** | A | nav3 1.1.0 needs compose ≥1.10.0 → ui/anim/runtime 1.10.2, material3 1.4.0; ADR-42 |
| navigation-compose | 2.7.0 | **2.9.0** bridge → **removed** | A → C | ref-07-era parity, then Nav3 artifacts; ADR-42 |
| navigation3-runtime/ui | — | **1.1.0** | C | ADR-42 |
| lifecycle-viewmodel-navigation3 | — | **2.10.0** | C | per-entry ViewModels under Nav3; ADR-42 |
| lifecycle | 2.8.6 | **2.10.0** | A | nav3 hard-depends on lifecycle-runtime 2.10.0 (skill 2.9.0 superseded); ADR-42 |
| room | 2.6.1 | **2.7.1** | A | skill pin + KSP2-era Room; schemas untouched; ADR-41 |
| coroutines | 1.9.0 | **1.10.2** | A | skill pin |
| datastore | 1.1.1 | **1.1.7** | A | skill pin |
| hilt / hilt-navigation-compose / hilt-work | — | **2.60.1 / 1.2.0 / 1.2.0** | B | ADR-43 — 2.57.2's Hilt Gradle Plugin fails under AGP 9 → pre-authorized 2.60.1 fallback adopted (skill 2.56.2 superseded) |
| kotlinx-serialization-json + plugin | — | **1.8.1 + 2.2.10** | C | NavKey serialization; ADR-42 |
| turbine | — | **1.2.0** | D | skill pin, AGENTS §8 justified as replacing hand-rolled flow testing |
| profileinstaller | — | **1.4.1** | F | skill pin |
| work | 2.9.0 | unchanged | — | pairs with hilt-work 1.2.0; bump only if build forces |
| activity-compose | 1.9.3 | **1.12.0** | C | §8.2's "bump only if resolution forces" clause exercised: forced by compose BOM 2026.01.01 → material3 1.4.0 (nav3 POMs show no activity floor) |
| compileSdk | 35 | **36** | C | implementation-forced: nav3 1.1.0 + lifecycle-viewmodel-navigation3 2.10.0 (+ activity-compose 1.12.0) AARs declare `minCompileSdk = 36`; `checkDebugAarMetadata` fails at 35; ADR-42 |
| targetSdk 35, minSdk 26, jvmTarget 17 | | unchanged | A | drop the `kotlinOptions {}` block — AGP 9 built-in Kotlin derives jvmTarget from `compileOptions` (both 17) |

### 8.3 Per-ViewModel prescription (Conflict 2 detail)

MVI contract = one public `StateFlow<UiState>`, one private state holder mutated
only via atomic `update`, sealed `UiEvent` dispatched through `onEvent(event)`,
sealed `UiEffect` via `Channel(BUFFERED)` + `receiveAsFlow()`.

| ViewModel (file) | Pattern | Required changes |
|---|---|---|
| `TodayViewModel` | **MVI** | sealed `TodayEvent` + `onEvent` for its 5 public actions; fold the separate `_targetEntry` holder in as a combine source so the screen collects ONE `StateFlow<TodayUiState>`; keep `Channel<TodayEffect>` |
| `CreationViewModel` | **MVI** | sealed `CreationEvent` + `onEvent` for its 4 public actions; keep atomic `.update` and `Channel<CreationEffect>` (already conforming half) |
| `ActivitiesViewModel` | **MVI** | 4 user-mutable interdependent fields (query/tab/category/sort) + 9-action surface → consolidate the 4 private flows into one filter holder, single public `StateFlow`, sealed `ActivitiesEvent` + `onEvent`; replace `Channel<String>` with a new sealed `ActivitiesUiEffect` |
| `HabitsViewModel` | MVVM retained | derived `combine`/`stateIn` stays; wire the existing (currently dead) sealed `HabitsEffect` into the channel instead of `Channel<String>`; no `onEvent` |
| `CalendarViewModel` | MVVM retained | 2 user-driven inputs (month, selection), rest derived via `stateIn`; keep public methods + `Channel<String>`; **closest call — rationale recorded in ADR-44, do not re-litigate** |
| `SettingsViewModel` | MVVM retained | independent single-field setters (ref 01 explicitly says Settings → MVVM); keep `SettingsEffect`; DI change only |
| `ActivityDetailViewModel`, `HabitDetailViewModel` | MVVM retained | state fully derived (`combine`/`stateIn`), command surface (3/2 funs), typed effects kept; **do not strip** the sealed `UiState`/`UiEffect` types; DI change only |
| `InsightsViewModel` | MVVM retained | read-only derived state; DI change only |
| `OnboardingViewModel` (in `OnboardingScreen.kt`) | MVVM retained | single command action; DI change only |

Unconditional invariants for all 10 (skill "MVI Guardrails"): private holders
mutated only via atomic `update` (0 naked assignments exist today — keep it that
way); one `collectAsState()` call in `MainActivity.kt` becomes
`collectAsStateWithLifecycle()`; effects always `Channel(BUFFERED)` +
`receiveAsFlow()` collected exactly once; no repo/DAO access from composables;
no `stringResource`/resources inside ViewModels; no `!!`/`GlobalScope`/`runBlocking`.

### 8.4 Documented deviations register

1. Kotlin 2.1.20 → **2.2.10** (forced by AGP 9 built-in Kotlin; ADR-41).
2. KSP `2.1.20-*` → **2.2.10-2.0.2** (KSP1 unavailable for 2.2.x; ADR-41).
3. Compose BOM 2025.05.00 → **2026.01.01** (nav3 compose floor; ADR-42).
4. Lifecycle 2.9.0 → **2.10.0** (nav3 hard dependency; ADR-42).
5. Hilt 2.56.2 → **2.57.2** (Kotlin 2.2 metadata; ADR-43) → **2.60.1**
   (2.57.2's Hilt Gradle Plugin fails under AGP 9; pre-authorized fallback
   adopted in Phase B; ADR-43).
6. Skill `navigation-compose:2.9.0` pin → **Nav3 artifact set** (skill's own
   REQUIRED Nav3 target; ADR-42).
7. `buildFeatures.buildConfig = true` retained — `SettingsScreen.kt` reads
   `BuildConfig.VERSION_NAME` (skill recommends `false`; not worth a behavior
   change in this task).
8. **Single-module retained** — skill multi-module layout is RECOMMENDED-"large
   apps" only and is explicitly out of scope (task §3).
9. `hilt-navigation-compose 1.2.0` drags an unused `navigation-compose 2.5.1`
   transitively (dead weight, R8-stripped; do NOT add Nav2 usage).
10. material3 1.3.0 → **1.4.0** is unavoidable with any BOM ≥ 2025.09 — Phase E
    carries an explicit visual-regression watch (runtime check, no redesign).
11. compileSdk 35 → **36** (Phase C; implementation-forced by the approved
    nav3 1.1.0 / lifecycle-viewmodel-navigation3 2.10.0 AARs' `minCompileSdk
    = 36`, independently verified by the Reviewer from cached AAR metadata;
    targetSdk 35 and minSdk 26 unchanged; ADR-42).

### 8.5 Implementation phases

**Phase A — Toolchain (build files only, no Kotlin source changes).**
Files: `gradle/libs.versions.toml`, `gradle/wrapper/gradle-wrapper.properties`
(9.1.0), `app/build.gradle.kts`, `gradle.properties` (only if AGP 9 demands).
Actions: §8.2 Phase-A rows; remove the `org.jetbrains.kotlin.android` plugin
alias/usage (incompatible with built-in Kotlin), remove `kotlinOptions {}`,
keep `buildConfig = true` and `room { schemaDirectory(...) }`; keep KSP.
Validate: `.\gradlew.bat :app:assembleDebug` then `:app:testDebugUnitTest`
(baseline 253 tests). Gates: built-in-Kotlin plugin wiring, KSP2 + Room 2.7.1
schema processing, Gradle 9.1.0/build-tools 36.0.0 download. The pre-existing
Room `identityHash` issue is reported, not fixed (out of scope). The Robolectric
fixture/LEGACY-sqlite workaround forced by Room 2.7.1 is tracked as
**ISSUE-028** (follow-up: complete the fixture DDL, then delete the properties
file).

**Phase B — Hilt migration.**
Files: catalog + `app/build.gradle.kts` (hilt plugin, `ksp(hilt-compiler)`,
androidx.hilt artifacts); new `di/` package (Database / Repository / UseCase /
Settings / Scheduler modules mirroring what `AppContainer` assembles);
`MomentumApp.kt` → `@HiltAndroidApp` + `Configuration.Provider` (WorkManager)
+ one-shot default-category seeding moved from `AppContainer.init`;
**delete** `AppContainer.kt` and `presentation/ViewModels.kt`
(`rememberContainer`/`momentumViewModel` — 26 call sites → `hiltViewModel()`);
all 10 ViewModels → `@HiltViewModel @Inject constructor(<the specific repos/
use cases they use>)` (mechanical `container.x` → named parameter); 3 call sites
in `MainActivity.kt` → `@AndroidEntryPoint` + injected settings source +
`collectAsStateWithLifecycle`; `notification/Reminders.kt` →
`@HiltWorker` workers (AssistedInject) + `@AndroidEntryPoint` receivers;
`notification/ReminderScheduler.kt` → `@Singleton @Inject` scheduler bound to
the `ReminderScheduler` interface; the 3 existing JVM test files construct
ViewModels directly from their (now interface) dependencies — no Hilt in unit
tests (skill's testing guidance), `reminderSchedulerOverride` seam becomes an
ordinary constructor argument.
Validate: `:app:assembleDebug`, `:app:testDebugUnitTest`,
`:app:compileDebugAndroidTestKotlin`. Fallback: Hilt 2.60.1.

**Phase C — Navigation 3.**
Files: catalog (remove `navigation-compose`, add nav3 + `lifecycle-viewmodel-
navigation3` + serialization), `app/build.gradle.kts` (serialization plugin),
`presentation/navigation/Routes.kt` → `@Serializable` `NavKey`s with ID-only
fields, `MomentumNavGraph.kt` → `NavBackStack` + `NavDisplay` +
`entryProvider` (single graph, nested structure preserved), `DetailTypeGate.kt`,
`CreationNav.kt`, `MainActivity.kt` (`EXTRA_OPEN_ACTIVITY` still seeds
Today → detail entry), bottom-bar call sites (ADR-22's four tabs; tab
re-select keeps the `launchSingleTop` behavior, back behavior unchanged).
No string routes may remain.
Validate: `:app:assembleDebug`, `:app:testDebugUnitTest`, then runtime smoke
(Reviewer): tab switch/re-select, create flow, detail open/back, notification
intent open, predictive back.
**Status: runtime navigation smoke remains UNVERIFIED** — no device/emulator
was available at the Reviewer checkpoint (Phases A–C compile/unit gates
verified only); the smoke is deferred to the final verification pass.

**Phase D — MVI conformance + Turbine.**
Files: the three MVI ViewModels (§8.3) + their screen call sites, Habits
effect wiring, their test files (adopt Turbine 1.2.0 where it replaces
hand-rolled flow collection — AGENTS §8 justification recorded).
Validate: `:app:testDebugUnitTest`, `:app:assembleDebug`.

**Phase E — Compose quality pass.**
Files: `presentation/screens/**`, `presentation/components/**`,
`presentation/theme/**`, `app/src/main/res/values/strings.xml`,
`MainActivity.kt`. Work: 53 literal `Text("` → `stringResource` (wording
byte-identical), 34 raw `sp`/`fontSize` → typography tokens (0 outside theme),
modifier ordering, lazy-list `key` + `contentType`, ≥48dp targets,
semantics/contentDescription/mergeDescendants, reduced-motion, edge-to-edge
insets, stability annotations where the skill requires. `presentation/
exploration/phase2b/**` untouched unless a compile error forces a change.
Gates: grep = 0 literals / 0 raw sp; runtime visual check incl. the
material3 1.4.0 regression watch (identical screens before vs after).

**Phase F — Release / perf / final gates.**
Files: `app/build.gradle.kts` (release `isMinifyEnabled = true`,
`isShrinkResources = true`, keep rules for Room/KSP, kotlinx.serialization,
Hilt), catalog `profileinstaller = 1.4.1` dependency (+ baseline-profile
direction where applicable).
Validate: `:app:assembleDebug`, `:app:testDebugUnitTest` (≥253),
`:app:compileDebugAndroidTestKotlin`, `:app:assembleRelease` (R8 gate), plus a
final banned-pattern grep sweep (skill ref 00).

Validation is proportional per phase as listed; correction limit ≤2 cycles per
defect (AGENTS §11). No commits (Human-in-the-Loop).

### 8.6 Risks

- Hilt 2.57.2 × KSP2 × Gradle 9.1.0 is unproven until the Phase B build
  (fallback 2.60.1 pre-authorized).
- Room 2.7.1 under KSP2 is a first-build risk; schema is unchanged, so no
  migration is expected; identityHash remains a reported pre-existing issue.
- material3 1.3.0 → 1.4.0 visual drift cannot be avoided by BOM choice —
  explicit runtime check in Phase E.
- Nav3 behavioral parity (tab re-select, back stack, predictive back, intent
  open) is runtime-verified, not compile-verified.

### 8.7 TASK-012 — Baseline profile architecture and implementation plan (ADR-45)

**Current state.** `androidx.profileinstaller:profileinstaller 1.4.1` is on the
runtime classpath (Phase F), so the installer/receiver are in the merged
manifest — but there is **no profile**: no `app/src/main/baseline-prof.txt`, no
`:baselineprofile` module (`settings.gradle.kts` has only `include(":app")`), no
`androidx.baselineprofile` plugin, zero `BaselineProfile` test sources. The APK
carries no `assets/dexopt/baseline.prof`. The ADR is **ADR-45**; the version
and plugin evidence is recorded there and is not repeated here.

#### 8.7.1 Version pins to add to `gradle/libs.versions.toml`

| Alias | Version | Verified against |
|---|---|---|
| `androidx-baselineprofile` (plugin id `androidx.baselineprofile`) | **1.5.0** | `group-index.xml` latest/release 1.5.0 (`lastUpdated 20260909170753`); marker POM → `benchmark-baseline-profile-gradle-plugin:1.5.0` |
| `androidx-benchmark-macro-junit4` | **1.5.0** | ships `androidx/benchmark/macro/junit4/BaselineProfileRule.class`; `minCompileSdk=34`, `minAndroidGradlePluginVersion=8.1.1`, `minSdkVersion=24` |
| `androidx-benchmark-junit4` | **1.5.0** | supplies `androidx.benchmark.junit4.AndroidBenchmarkRunner`; **not** a transitive dep of macro-junit4 |
| `androidx-uiautomator` | **2.3.0** | exactly the version `benchmark-macro-junit4:1.5.0` requests; pin it rather than 2.4.0 so the classic `device.findObject(By…)` DSL is used, not the new `onElement { }` DSL |
| `androidx-test-ext-junit` | **1.2.1** | latest 1.2.x in `androidx.test.ext:junit` |

Plugin alias to add: `androidx-baselineprofile = { id = "androidx.baselineprofile", version.ref = "androidxBaselineProfile" }`
and `android-test = { id = "com.android.test", version.ref = "agp" }`.

**`androidx.baselineprofile:baseline-profile-junit4` is NOT added — it does not
exist on Google Maven** (404 for 1.1.0…1.5.0 on both `dl.google.com` and
`maven.google.com`; absent from `group-index.xml` and `master-index.xml`).
`BaselineProfileRule` lives inside `benchmark-macro-junit4`.
`profileinstaller` stays **1.4.1** (separate release train, still latest).

#### 8.7.2 `settings.gradle.kts`

Add one line after `include(":app")`:

```kotlin
include(":baselineprofile")
```

#### 8.7.3 `baselineprofile/build.gradle.kts` (new file)

```kotlin
plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.androidx.baselineprofile)
}

android {
    namespace = "com.momentum.baselineprofile"
    compileSdk = 36
    defaultConfig {
        minSdk = 26
        targetSdk = 35
        testNamespace = "com.momentum.baselineprofile"
        testInstrumentationRunner = "androidx.benchmark.junit4.AndroidBenchmarkRunner"
    }
    targetProjectPath = ":app"
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// Producer-side DSL in 1.5.0 has ONLY these two properties.
baselineProfile {
    useConnectedDevices = true
}

dependencies {
    implementation(libs.junit)
    implementation(libs.androidx.test.ext.junit)
    implementation(libs.androidx.test.uiautomator)
    implementation(libs.androidx.benchmark.junit4)
    implementation(libs.androidx.benchmark.macro.junit4)
}
```

Rules for this file:
- **No `org.jetbrains.kotlin.android`, no `kotlin { }`, no `kotlinOptions {}`.**
  AGP 9 built-in Kotlin covers `com.android.test` (`TestComponentImpl extends
  ComponentImpl`, and `builtInKotlinSupportMode` lives on `ComponentImpl`).
  ADR-41 stands; the legacy plugin is not reintroduced.
- **Do not write `targetVariant`, `automaticGenerationDuringBuild` or
  `saveInSrc` here.** They are not producer properties in 1.5.0 (`targetVariant`
  does not exist at all). Setting them here is a configuration error.
- No `managedDevices` — the Pixel_7 AVD is booted by hand.

Files:
- `baselineprofile/src/main/AndroidManifest.xml` — minimal, no components:
  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <manifest />
  ```
- `baselineprofile/src/main/kotlin/com/momentum/baselineprofile/BaselineProfileGenerator.kt`
  — note `src/main/kotlin`, **not** `src/androidTest`: a `com.android.test`
  module's tests *are* its main source set.

#### 8.7.4 `app/build.gradle.kts` — the only app-side change

1. `plugins { … }` — add `alias(libs.plugins.androidx.baselineprofile)`.
2. `dependencies { … }` — add `baselineProfile(project(":baselineprofile"))`.
3. New top-level block (consumer side, after `android { }`):

```kotlin
baselineProfile {
    variants.configureEach {
        automaticGenerationDuringBuild = false   // never require a device at build time
        saveInSrc = true                         // write the profile back into the source tree
        mergeIntoMain = true                     // app/src/main/baseline-prof.txt, not src/release/…
    }
}
```

**No `signingConfig` is added and `release` is left exactly as it is.** The
plugin creates its own extended build types in `:app` and sets
`signingConfig = <debug signing config>` + `isProfileable` on **those** types
(`BuildTypesKt.copySigningConfigIfNotSpecified`,
`BaselineProfileAppTargetAgpPlugin`). No keystore is created, nothing is
invented, and there is no temporary edit to revert (AGENTS.md §12).

If `variants.configureEach { }` does not resolve in the Kotlin DSL, use
`variants.all { … }` (same semantics) and record the substitution — do not
change the property values.

#### 8.7.5 The generator (test design)

```kotlin
package com.momentum.baselineprofile

class BaselineProfileGenerator {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    // Pass 1 — warm-up + the full journey. Never benchmarked, only recorded.
    @Test
    fun journey() = baselineProfileRule.collect(
        packageName = PACKAGE_NAME,
        profileBlock = { journeyToTodayThenAcrossAllTabs() },
    )

    // Pass 2 — cold startup only, so startup classes get top-priority AOT.
    @Test
    fun startup() = baselineProfileRule.collect(
        packageName = PACKAGE_NAME,
        profileBlock = {
            startAppAndWait()
            dismissOnboardingIfPresent()
        },
    )
}
```

`journeyToTodayThenAcrossAllTabs()` (private, in the same file):

1. `startAppAndWait()` — `MainActivity` is the `MAIN`/`LAUNCHER` activity.
2. `dismissOnboardingIfPresent()` — `device.findObject(By.desc("Skip
   onboarding")).click()` if found, then
   `device.wait(Until.hasObject(By.text("Home")), 5_000)`.
   Onboarding is **always** present at the start of each `collect` block
   (`collect` reinstalls the app). `cd_skip_onboarding` = "Skip onboarding" is
   on all four pages and finishes immediately with `seedExamples = false`, so
   the journey is deterministic and independent of page count. Do **not** tap
   through four pages, and do not use "Let's Get Started" (page-4 only).
3. Tab walk, each followed by `device.wait(Until.hasObject(By.text(<tab>)), 5_000)`
   and a short settle (`device.waitForIdle()`):
   `By.text("Habits")` → `By.text("Tasks")` → `By.text("Insights")` →
   `By.text("Home")`.
   These are `R.string.nav_habits` / `nav_tasks` / `nav_insights` / `nav_home`,
   each the `Text` inside a clickable `LiquidGlassTab` column — the four tabs
   are Home, Habits, Tasks, Insights (ADR-22). `Tasks` is the Activities list
   surface; there is no separate "Activities" tab.

Determinism rules: **no** seeded activity data exists (Skip passes
`seedExamples = false`), so the lists are empty and rendering is stable; every
interaction is a `wait`-guarded find, never a fixed `sleep`; both blocks start
from a reinstalled app so block 2 is a true cold start.

**Not in scope for the journey:** create flow, detail, calendar, settings,
themes, scroll. A focused journey is the guidance; widen it only with a
recorded reason.

#### 8.7.6 Commands, in order

```powershell
# 0. sanity — no regression from the plugin being applied to :app
.\gradlew.bat :app:assembleDebug

# 1. boot the device (required: generation needs a booted, non-root device on API 33+)
& "$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe" -avd Pixel_7 -no-snapshot-save
#    (leave it running; in a second shell)
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" wait-for-device
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" devices

# 2. generate
.\gradlew.bat :app:generateBaselineProfile

# 3. confirm the profile landed in the source tree, then prove packaging
.\gradlew.bat :app:assembleRelease
```

#### 8.7.7 Verification

1. **Profile exists in source.**
   `Test-Path app\src\main\baseline-prof.txt` → True.
   *If* the plugin writes `app\src\release\baselineProfiles\baseline-prof.txt`
   instead (the AGP 8.3+ variant-aware location), that is equally valid and
   AGP still packages it — **record the actual path and do not move the file.**
2. **Rule count** (report all three numbers):
   - total lines: `(Get-Content app\src\main\baseline-prof.txt).Count`
   - rule lines (non-blank, not starting with `#`):
     `(Get-Content app\src\main\baseline-prof.txt | Where-Object { $_ -and -not $_.StartsWith('#') } | Measure-Object).Count`
   - the rule count the Gradle task itself prints in `:app:generateBaselineProfile`
     output — quote it verbatim.
3. **The profile is inside the release APK** (this is the acceptance proof;
   `cmdline-tools`/`apkanalyzer` is **not** installed on this machine, so use
   .NET's zip reader):
   ```powershell
   Add-Type -AssemblyName System.IO.Compression.FileSystem
   $apk = "app\build\outputs\apk\release\app-release-unsigned.apk"
   [IO.Compression.ZipFile]::OpenRead((Resolve-Path $apk)).Entries |
     Where-Object { $_.FullName -like 'assets/dexopt/*' } |
     ForEach-Object { "{0}  {1} bytes" -f $_.FullName, $_.Length }
   ```
   Expect `assets/dexopt/baseline.prof` (and/or `baseline.profm` — the
   minified-build form). **Zero entries = the task has not succeeded**, whatever
   the Gradle log says. Also confirm the file grew versus the TASK-011 baseline
   APK (4.04 MB) and record the new size.
4. **No product change.** `git status --porcelain` shows only:
   `settings.gradle.kts`, `gradle/libs.versions.toml`, `app/build.gradle.kts`,
   the new `baselineprofile/**`, `app/src/main/baseline-prof.txt`.
   `git status --porcelain app/schemas` is clean. Zero edits under
   `app/src/main/java/**` and `app/src/main/res/**`.
   `app-release-unsigned.apk` — still unsigned; no `signingConfigs { }` block
   anywhere; no `*.jks`/`*.keystore` file in the tree.
5. **No `INTERNET`** permission added (unchanged from Phase F).
6. **Report the app build type the plugin profiled** (name + whether
   `isProfileable`/`signingConfig` were applied to it) from the generation log —
   the Reviewer needs it to confirm a release-derived, profileable APK was used.

#### 8.7.8 Risks

- **AGP 9.0.0 acceptance is unproven.** The plugin's
  `MIN/MAX_AGP_VERSION_REQUIRED_INCLUSIVE` / `…_RECOMMENDED_EXCLUSIVE` bounds
  could not be read from the bytecode. If 1.5.0 refuses AGP 9.0.0, the
  pre-authorized fallback is **`androidx-baselineprofile` 1.4.1** (both plugin
  and `benchmark-macro-junit4`, kept in lockstep); record which was used. Do
  **not** bump AGP — ADR-41 pins 9.0.0.
- **The generator will only produce a thin profile** if the journey is wrong
  (e.g. onboarding never dismissed → every rule is onboarding-only). Check the
  rule count and the generated file's class names against `com.momentum.app`
  before accepting.
- **Profile staleness.** The profile is now a committed artifact. Anyone who
  changes the startup or navigation journey must re-run
  `:app:generateBaselineProfile` on a booted device. This is the same manual
  discipline as the R8 keep rules.
- **The plugin adds build types to `:app`.** They are not shipped and do not
  affect `assembleRelease`; but if `:app:assembleRelease` output changes
  unexpectedly, check the new build types first.
- **Emulator state.** `Pixel_7` must be booted and left running for step 2; a
  generation attempt with no device fails with an install error, not a
  meaningful test failure.
- **`ISSUE-029` (onboarding re-shows on every cold start)** is untouched. It is
  the reason the generator must dismiss onboarding on every pass; it is *not*
  fixed here and must not be fixed here.

#### 8.7.9 Scope guard

No product/UI/string/resource/navigation/DI/schema/ProGuard change; Phase 2B
untouched; no networking; no keystore; no `signingConfig`; no commit or push.

#### 8.8 TASK-014 (DECIDED, NOT YET EXECUTED) - the four Phase 2B direction explorations are obsolete; delete `exploration/phase2b/**` and the debug source set

<!-- ARCHITECT OWNER -->

**Governing ADR: ADR-46** (appended to `docs/architecture/decisions/DECISIONS.md`;
amends ADR-40's deletion *trigger* only, append-only, nothing rewritten).

**Objective.** The human has ruled that the four Taste-driven UI direction
explorations (A *Press*, B *Instrument*, C *Loom*, D *Aurora Governed*) are
obsolete and must be removed. The app's **current** design direction must not be
removed. No production behaviour, resource, build or Gradle change.

##### 8.8.1 Aurora boundary verdict (highest-risk judgement)

`exploration/phase2b/aurora/**` is **option (i) - one of the four obsolete
explorations - and is DELETED**. The app's live Aurora direction is a different,
unrelated set of files and is **RETAINED**. Evidence:

| | Round-1 prototype (DELETE) | Production direction (RETAIN) |
|---|---|---|
| Package | `presentation.exploration.phase2b.aurora` | `presentation.aurora`, `presentation.components`, `presentation.screens.today`, `presentation.screens.habits`, `presentation.theme` |
| Entry point | debug-only `Phase2BPreviewActivity` only | `MomentumNavGraph` -> `TodayScreen`/`HabitsScreen` |
| Scope | a whole Home screen (`AuroraHome.kt:69`; header, sections, bottom nav) | the hero treatment + theme, not a screen shell |
| Tokens | own `AuroraTokens.kt` `CompositionLocal` | `presentation/theme/Color.kt` `AuroraLight`/`AuroraDark`, registry `theme_id = "aurora"` |
| Artwork | consumes `R.drawable.habits_hero_light` read-only (`AuroraHeader.kt:132`) | `MomentumHeroArtwork.kt:43/82` + `HomeHero.kt:109` + `HabitsHero.kt:70` + both `habits_hero_*.webp` |
| git state | `A ` (added in TASK-009, never released) | long-lived production files |

ADR-40's Addendum already called D's "purple everywhere" treatment a
**Round-1-only bounded exception** requiring re-litigation *if D shipped*. D never
shipped. Deleting it removes the obligation with it. Production Aurora is
untouched, so "do not remove the current design direction" is satisfied by
construction, not by exception.

##### 8.8.2 Cross-package reference audit - ZERO

Repo-wide search for `phase2b`, `Phase2B`, `PressHome`, `PressTokens`,
`PressMaterials`, `PressDepthStrategy`, `InstrumentHome`, `InstrumentTokens`,
`InstrumentChannel`, `LoomHome`, `LoomTokens`, `LoomWeave`, `AuroraHome`,
`AuroraTokens`, `AuroraHeader`, `Phase2BFixture`, `ProtoDay`, `ProtoToday`,
`ProtoActivity`, `ProtoCategory`, `ProtoTracking`, `ProtoDirection` returns hits
**only** in: (a) the package itself, (b) `app/src/debug/**`, (c) prose -
`ISSUES.md`, `PROJECT_STATUS.md`, `task.md`, `MODERNIZATION_AUDIT.md`,
`DECISIONS.md`, and the `app/proguard-rules.pro:30-31` **comment**. **No
production source, no test, no resource, no manifest, no Gradle/catalog file
references this package.** Deletion cannot cause compile or test fallout.
`presentation/theme/DesignSystemPreviews.kt` references
`presentation.aurora.AuroraBackground`/`AuroraState` - production, unrelated.
`presentation/components/HeroAuroraPreview.kt`'s `HeroAuroraHeader` is a
different symbol in a different package (untracked, out of scope, retain).

##### 8.8.3 Resources - nothing to delete (verifies TASK-013)

ADR-40's optional `res/drawable-nodpi/phase2b/**` **was never created** (no such
directory exists). The only `res/` references inside the package are
`R.font.manrope` (also used by `presentation/theme/Type.kt`) and
`R.drawable.habits_hero_light` (also used by `MomentumHeroArtwork.kt` /
`HabitsHero.kt`). **No resource is exclusive to the prototypes**, which
independently confirms TASK-013's "zero unused resources in `app/src/main/res`".
**Touch nothing under `app/src/main/res`.**

##### 8.8.4 Itemised deletion boundary

**DELETE (4 items, 17 files):**

1. `app/src/main/java/com/momentum/app/presentation/exploration/` - whole tree,
   all 15 files: `phase2b/press/{PressHome,PressMaterials,PressTokens}.kt`,
   `phase2b/instrument/{InstrumentHome,InstrumentChannel,InstrumentTokens}.kt`,
   `phase2b/loom/{LoomHome,LoomWeave,LoomTokens}.kt`,
   `phase2b/aurora/{AuroraHome,AuroraHeader,AuroraTokens}.kt`,
   `phase2b/fixture/{Phase2BFixture,Phase2BModels}.kt`,
   `phase2b/preview/Phase2BPreviewScreen.kt`. *Four obsolete explorations plus
   the scaffolding that exists only to host them.*
2. `app/src/debug/AndroidManifest.xml` - *its only declaration is
   `Phase2BPreviewActivity`.*
3. `app/src/debug/java/com/momentum/app/presentation/exploration/phase2b/preview/Phase2BPreviewActivity.kt`
   - *the debug-only entry point; verified the sole file under `app/src/debug`.*
   Items 2+3 mean the **entire `app/src/debug/` source set disappears**, exactly as
   ADR-40 ("and the debug source set") and `ISSUE-026`'s recorded fix prescribe.
   AGP supplies an empty debug manifest; **no Gradle edit permitted.**
4. Nothing else. `presentation/` becomes
   `{aurora, components, navigation, screens, theme}`.

**RETAIN (each with reason):**

- `presentation/aurora/MomentumAurora.kt`, `presentation/components/MomentumHeroArtwork.kt`,
  `presentation/components/HeroAuroraPreview.kt`, `presentation/screens/today/HomeHero.kt`,
  `presentation/screens/habits/HabitsHero.kt`, `presentation/theme/Color.kt`
  (`AuroraLight`/`AuroraDark` + registry), `presentation/theme/DesignSystemPreviews.kt`
  - *the current production Aurora direction; `HeroAuroraHeader` there is a
  different symbol.*
- `res/drawable/habits_hero_light.webp`, `habits_hero_dark.webp`, `res/font/manrope.ttf`
  and all of `app/src/main/res` - *production-shared, never prototype-exclusive.*
- `docs/phase2b/round1/{A-press-home,B-instrument-home,C-loom-home,D-aurora-governed-home}.png`
  - *render evidence, not explorations; the only proof behind `PROJECT_STATUS.md`'s
  `F-CONCEPT-2B = FULLY_VERIFIED` and the findings cited by `ISSUES.md` 025/026/027.
  Deleting them would leave live citations dangling.*
- `design/aurora-concept/**` (8 files, ~5.4 MB) - *`ISSUE-024` is **CLOSED with a
  retain resolution**; the human's decision did not name it; it is not a Phase 2B
  direction. Untouched.*
- `MOMENTUM_CONCEPT_EXPLORATION.md` - *per ADR-40 still the repository's sole
  source of visual truth for concept work; cited by ADR-40 and `ISSUE-025`; not a
  direction exploration.*
- `docs/design/AURORA_CONCEPT_ASSETS.md`, `docs/development/task_history/TASK-008_visual_concept_exploration.md`,
  `MODERNIZATION_AUDIT.md`, `app/proguard-rules.pro` - *historical/audit prose;
  `proguard-rules.pro` lines 30-31 are a stale **comment** only, no rule, no defect,
  out of scope to edit.*
- `app/build.gradle.kts`, `settings.gradle.kts`, `gradle/libs.versions.toml`,
  `gradle.properties`, `app/src/main/AndroidManifest.xml`, `app/schemas/**`,
  `baselineprofile/**` - *untouched; no build change is required or permitted.*

##### 8.8.5 Issue consequences (for the Orchestrator; `ISSUES.md` NOT edited here)

- `ISSUE-026` - **CLOSED as resolved by removal.** Corrected premise (ADR-46
  Correction Note 1): the issue's "accepted cost" **had already ceased to exist
  at TASK-011 Phase F**, when R8 (`isMinifyEnabled = true` since
  `app/build.gradle.kts:40-41`) began stripping the 47 unreachable `phase2b`
  classes from the release build — before this task. This deletion removes the
  *source* and the debug-only entry point, not release bytes. Recommend
  **CLOSED**; `ISSUES.md` already records the Phase F caveat.
- `ISSUE-027` - defect lived only in `InstrumentHome.kt:217-230`, now deleted; the
  issue has **no remaining subject**. Recommend **CLOSED as resolved-by-removal**,
  explicitly not "fixed".
- `ISSUE-025` - its criterion was ratifying the derived palette **when a direction
  is selected**; no direction is selected and the code is gone, so it can never be
  met. Recommend **CLOSED as moot**, disclosure retained as historical record.
- `ISSUE-006` - **unchanged, still OPEN, still needs a human ruling.** ADR-40's
  *provisional-for-the-prototype-only* adoption of the bundled side is deleted with
  D; `ISSUE-006` now concerns production `HomeHero`/`HabitsHero`/`MomentumHeroArtwork` only.
- `ISSUE-024` - **unchanged, CLOSED/retained.** Untouched by this task.
- `PROJECT_STATUS.md` `F-CONCEPT-2B` row - Orchestrator-owned; recommend it be
  annotated as "exploration abandoned, prototypes deleted under ADR-46" rather than
  left implying Round 2 is pending.

##### 8.8.6 Developer verification plan

**Baseline first (before deleting anything):**
`gradlew :app:testDebugUnitTest --rerun-tasks --offline` - expect **41 files /
254 tests / 0 failures** (TASK-013-verified figure). Record the release APK byte
size: `Get-Item app\build\outputs\apk\release\*.apk | Select-Object Name,Length`.

**Three gates after deletion:**
1. `gradlew :app:assembleDebug --offline`
2. `gradlew :app:testDebugUnitTest --rerun-tasks --offline` - **41 files /
   254 tests / 0 failures, identical to the baseline.** Any count change means
   something outside the approved boundary was touched.
3. `gradlew :app:assembleRelease --rerun-tasks --offline`

**Post-deletion checks:**
- Repo-wide search for `phase2b` / `Phase2B` / `AuroraHeader` / `AuroraTokens`
  returns **0 hits outside prose** (`ISSUES.md`, `PROJECT_STATUS.md`, `task.md`,
  `MODERNIZATION_AUDIT.md`, `DECISIONS.md` - `MODERNIZATION_AUDIT.md` lines 110,
  624, 718, 738 mention the path and are historical audit prose; do not edit them).
- `git status --short` shows the exploration files as deleted and **no other
  unexpected modification**; the pre-existing unrelated working-tree changes are
  preserved untouched.
- Merged release manifest still contains exactly 1 activity and 0 × `exploration`
  (now trivially true - still confirm no merge error from the removed debug
  manifest).
- Debug build still packages and installs; absence of `app/src/debug` must not
  break `assembleDebug`.
- APK size before/after reported as **measured bytes**. **No size win is
  promised, and `0` is the expected result** — but not for the reason originally
  recorded here. **Corrected premise (ADR-46 Correction Note 1):** release has
  been minified since TASK-011 Phase F (`app/build.gradle.kts:40-41`,
  `isMinifyEnabled = true` + `isShrinkResources = true`), and Phase F verified
  **47 `phase2b` classes removed in the release `usage.txt`**, 0 in `mapping.txt`.
  R8 had therefore already stripped the prototype classes before this task, so
  there are no prototype bytes left to reclaim and a `0`-byte delta is the
  expected, correct outcome. (The onboarding WebPs 1,519,994 B, hero WebPs
  372,250 B and `manrope.ttf` 164,700 B remain untouched and unaffected; they
  simply were never the operative variable.) Report the measured bytes either
  way; do not characterise the delta as a benefit, and do not present `0` as a
  surprise.
- No commit, no push, no Gradle/ProGuard/manifest/resource edit.

---

## 9. IMPLEMENTATION STATUS

<!-- DEVELOPER OWNER -->

**Phase A (toolchain) — COMPLETE, verified (2026-09-29).** Build files only; no
Kotlin/Compose source edits, no Android resource edits, no schema changes.

Changes:
- `gradle/libs.versions.toml` — §8.2 Phase-A rows: agp 9.0.0, kotlin 2.2.10,
  ksp 2.2.10-2.0.2 (kept inline: TOML cannot compose the prefix from
  `version.ref`), composeBom 2026.01.01, navigationCompose 2.9.0 (bridge),
  lifecycle 2.10.0, room 2.7.1, coroutines 1.10.2, datastore 1.1.7; removed the
  `kotlin-android` plugin alias (kotlin-compose / ksp / room kept).
- `gradle/wrapper/gradle-wrapper.properties` — Gradle 9.1.0.
- `build.gradle.kts` (root) — removed `alias(libs.plugins.kotlin.android)
  apply false` (forced by the alias removal).
- `app/build.gradle.kts` — removed the `org.jetbrains.kotlin.android` plugin and
  the `kotlinOptions {}` block; kept `buildConfig = true`,
  `room { schemaDirectory }`, compileSdk/targetSdk 35, minSdk 26, release
  minify settings unchanged.
- `gradle.properties` — added only `android.disallowKotlinSourceSets=false`
  (AGP 9's documented flag for KSP's `kotlin.sourceSets` registration; NOT the
  `builtInKotlin=false` opt-out ADR-41 forbids). Otherwise untouched.

Validation (JDK 21, Gradle 9.1.0, one-time downloads completed):
- `:app:assembleDebug` → **BUILD SUCCESSFUL** (39/39 executed).
- `:app:testDebugUnitTest` → **BUILD SUCCESSFUL — 41 result files / 253 tests /
  0 failures / 0 errors** (all executed; baseline parity).
- `:app:compileDebugAndroidTestKotlin` → **BUILD SUCCESSFUL**.
- `app/schemas/**` unchanged (`copyRoomSchemas NO-SOURCE`; git-clean).

Two test-runtime defects were fixed inside Phase A (1 correction cycle each,
both build/test config — no test sources touched):
1. **Room 2.7.1 × Robolectric (Windows defaults to LEGACY SQLite) ×
   kotlinx-coroutines-test → 7 `UncaughtExceptionsBeforeTest` failures**
   (CreationViewModelTest ×5, HabitDetailViewModelTest ×2): Room 2.7's
   cross-thread connection pool breaks LEGACY's per-thread connection tracking;
   the resulting background coroutine failure is collected globally by
   coroutines-test. Fix: `robolectric.sqliteMode=NATIVE` via
   `testOptions.unitTests` in `app/build.gradle.kts` (NATIVE is Robolectric's
   default on every non-Windows host).
2. **With NATIVE, post-migration schema validation became truthful → 3
   migration tests failed. Pre-existing fixture gap, not a product bug:**
   `Migration1To2/2To3/3To4Test` hand-write the v1 DDL but omit
   `index_completion_records_activityId` and
   `index_completion_records_occurrenceDate`, which `schemas/<db>/1.json` has
   (real v1 installs therefore match the expected post-migration schema; the
   migrations only ALTER `activities`, which is correct). Baseline depended on
   LEGACY-mode behavior masking this. Fix: package-scoped
   `app/src/test/resources/com/momentum/app/data/local/robolectric.properties`
   (`sqliteMode=LEGACY`) restoring exact baseline semantics for that package.
   **Recommended follow-up (test-source, out of Phase A scope): complete the
   fixture DDL to match `1.json`, then delete that properties file.**

**Phase B (Hilt DI, ADR-43) — COMPLETE, verified (2026-09-29).** Behavior-preserving swap of the manual service locator for Hilt; no product/UI/schema changes.

Version note: ADR-43's primary **2.57.2 fails under AGP 9** ("Android BaseExtension not found"), so the **pre-authorized 2.60.1 fallback** was applied (androidx.hilt 1.2.0, KSP only, never kapt).

Changes:
- `gradle/libs.versions.toml` + `app/build.gradle.kts` — hilt 2.60.1 (plugin, `hilt-android`, `hilt-android-compiler`), androidx.hilt `hilt-navigation-compose` / `hilt-work` / `hilt-compiler` (1.2.0); `ksp(...)` only.
- `app/src/main/AndroidManifest.xml` — `xmlns:tools` + `tools:node="remove"` for `androidx.work.WorkManagerInitializer`, so WorkManager starts from `MomentumApp.Configuration.Provider` with the injected `HiltWorkerFactory` (confirmed absent from the freshly merged debug manifest).
- New `di/`: `DatabaseModule` (Room `momentum.db` + 3 migrations, P1#2 no destructive fallback), `RepositoryModule` (4 Room repos + `UserDataReset` @Singleton — `RepositoryImpls.kt` untouched), `UseCaseModule` (unscoped use cases), `SettingsModule`, `SchedulerModule` (`AlarmGateway` → `AlarmReminderScheduler`), `AppEntryPoint` (@EntryPoint for the two non-ViewModel call sites).
- New `presentation/HiltViewModelSupport.kt` — `ViewModelArgs` keys, `hiltViewModelWithArgs` (merges caller args into `DEFAULT_ARGS_KEY` on the owner's default extras, then creates the VM through the same Hilt factory androidx's internal `createHiltViewModelFactory` builds, via the public `androidx.hilt.navigation.HiltViewModelFactory(context, delegate)`), `rememberActivityRepository`.
- `MomentumApp` — `@HiltAndroidApp`, `Configuration.Provider` (injected `HiltWorkerFactory` + `CategoryRepository`), notification-channel check, and the idempotent default-category seeding moved from the old `AppContainer.init`. `MainActivity` — `@AndroidEntryPoint` with injected settings/reminder deps; launch-time reminder re-arm preserved.
- `notification/` — `@HiltWorker` + `@AssistedInject` on `FireReminderWorker`/`RescheduleWorker`; `@AndroidEntryPoint` on `CompleteActionReceiver` (the only receiver that used the container); `ReminderScheduler.create` deleted, `AlarmReminderScheduler` takes repositories instead of a locator lookup.
- 10 ViewModels → `@HiltViewModel @Inject constructor`; runtime route args travel through `SavedStateHandle` (`ViewModelArgs`: enums as `.name`, `activityId` as String).
- Call sites → `hiltViewModel()` / `hiltViewModelWithArgs(...)`; `DetailTypeGate` and `CreationNav` → `rememberActivityRepository()`.
- Deleted `AppContainer.kt` and `presentation/ViewModels.kt`.
- Tests: new `app/src/test/java/com/momentum/app/TestGraph.kt` (test-only mirror of the retired locator, `reminderSchedulerOverride` seam, VM factory helpers); `CreationViewModelTest`, `HabitDetailViewModelTest`, `HabitsViewModelTest` construct VMs from interface deps + `SavedStateHandle` (assertions unchanged).

Validation (all executed this session, JDK 21 / Gradle 9.1.0):
- `:app:assembleDebug` → **BUILD SUCCESSFUL** (Hilt's `hiltJavaCompileDebug` + `transformDebugClassesWithAsm` ran).
- `:app:testDebugUnitTest` → **BUILD SUCCESSFUL — 41 result files / 253 tests / 0 failures / 0 errors** (baseline parity; includes the 3 migrated suites).
- `:app:compileDebugAndroidTestKotlin` → **BUILD SUCCESSFUL**.
- Grep `AppContainer|rememberContainer|momentumViewModel` over `app/src` → **1 hit, 0 code refs**: a pre-existing *comment* in `presentation/exploration/phase2b/preview/Phase2BPreviewActivity.kt` (debug source; `presentation/exploration/phase2b/**` is explicitly out of scope).
- Generated wiring inspected: `Hilt_CompleteActionReceiver` + `CompleteActionReceiver_MembersInjector`, `FireReminderWorker_HiltModule` / `RescheduleWorker_HiltModule`, `Hilt_MainActivity`, `Hilt_MomentumApp`.

Two correction cycles used, both compile-time fixes: (1) restored the `androidx.compose.runtime.getValue` import dropped in `MainActivity`, and replaced androidx.hilt's `internal createHiltViewModelFactory` with the public `androidx.hilt.navigation.HiltViewModelFactory(context, delegate)` factory function (Kotlin spelling verified against the shipped artifact); (2) `TestGraph` — default scheduler now built the way `SchedulerModule` does (the old `ReminderScheduler.create` companion was deleted) and `SavedStateHandle` args use `set` (`putString` does not exist on it).

**Phase C (Navigation 3, ADR-42) — COMPLETE, verified (2026-09-29).** String routes / `NavHost` → `@Serializable` `NavKey`s + `NavDisplay`; no user-visible navigation behavior change; no schema, resource, or screen-content changes.

Changes:
- `gradle/libs.versions.toml` — removed `navigationCompose` + the `androidx-navigation-compose` library (the §8.2 A-phase bridge); added `navigation3 = 1.1.0` (`navigation3-runtime`, `navigation3-ui`), `androidx-lifecycle-viewmodel-navigation3`, `kotlinx-serialization-json = 1.8.1`, plugin `kotlin-serialization` (version.ref kotlin); `activityCompose` 1.9.3 → 1.12.0 — §8.2's own "bump only if resolution forces": `dependencyInsight` re-run with the pin temporarily back at 1.9.3 shows `1.9.3 → 1.12.0`, because `navigation3-ui-android:1.1.0` and `lifecycle-viewmodel-navigation3-android:2.10.0` both declare `androidx.activity:activity-compose:1.12.0` (POM + Gradle module metadata). **Correction (Phase D):** the original wording attributed the floor to "compose BOM 2026.01.01 → material3-android:1.4.0" and claimed "nav3 POMs declare no activity floor" — both verified wrong: the compose-bom POM contains no `androidx.activity` entry and `material3-android:1.4.0` requests only `activity-compose:1.8.2`.
- `app/build.gradle.kts` — `alias(libs.plugins.kotlin.serialization)`; four nav3/serialization `implementation(...)`; removed `implementation(libs.androidx.navigation.compose)`; **`compileSdk` 35 → 36** (ratification requested below; targetSdk 35 / minSdk 26 unchanged).
- `presentation/navigation/Routes.kt` — `object Routes` now holds 12 `@Serializable : NavKey` destinations (10 `data object`s — Today, Habits, Tasks, Insights, Calendar, Settings, Onboarding, Themes, CreateIntent, CreateTask — + `Edit(activityId: Long)` / `Detail(activityId: Long)`); string constants deleted; helpers `edit(id)` / `detail(id)` kept so call sites read the same. *(Correction, Phase D: originally recorded here as "11 … (9 `data object`s)" — grep over `Routes.kt` confirms 12.)*
- `presentation/navigation/MomentumNavGraph.kt` — `rememberNavBackStack(...)` + a single `NavDisplay` with 12 `entryProvider` entries (flat graph, same nesting of content); `onBack` = pop-never-empty; tab switch = pop-to-root + push-if-not-top (Nav2 `popUpTo(start){saveState}` + `launchSingleTop` + `restoreState` equivalent, re-select = no-op); onboarding `popUpTo(inclusive)` → clear + push Today; crossfade preserved (200 ms in / 150 ms out, `Enter/ExitTransition.None` under reduced motion) on `transitionSpec` / `popTransitionSpec` / `predictivePopTransitionSpec`; decorators = local retaining saveable-state decorator (first) + `rememberViewModelStoreNavEntryDecorator()`. `EXTRA_OPEN_ACTIVITY` deep-open, Create Item sheet routing, onboarding status-bar inset opt-out, and bottom-bar visibility logic are unchanged. `CreationNav.kt` and `MainActivity.kt` needed **no** changes (callback-only / extra-passing call sites) — confirmed by grep over `app/src`.
- `presentation/navigation/DetailTypeGate.kt` — KDoc only (`Routes.DETAIL` → `Routes.Detail`, `NavBackStackEntry` wording → "navigation entry").
- `app/src/test/.../RoutesTest.kt` — same single test (test count held at 253): key/factory equality + `@Serializable` JSON round-trip via `Routes.Detail.serializer()` + the Android restore path via `NavKeySerializer` (FQCN + concrete serializer), including a `data object` key.

Implementation decisions recorded for the Reviewer:
1. **Retaining saveable-state decorator.** Nav3's stock `SaveableStateHolderNavEntryDecorator` calls `removeState` on pop, which would discard a tab's scroll position on tab switch — Nav2's `saveState`/`restoreState` kept it. A local copy of that decorator without the pop-removal is used; ViewModels are still cleared on pop by `rememberViewModelStoreNavEntryDecorator`, matching Nav2 (popped tab kept state, lost VM). Stock decorator stays available if the runtime pass prefers it.
2. **No new ViewModel abstraction** — `HiltViewModelSupport.hiltViewModelWithArgs` untouched; `NavDisplay`'s per-entry owner supplies `HasDefaultViewModelProviderFactory`, so the existing `hiltViewModel(...)` call sites keep working.

**Requires Architect/Orchestrator ratification (§8.2 amendment): `compileSdk` 35 → 36.** Forced by `aarMetadata.minCompileSdk = 36` declared by `navigation3-runtime-android:1.1.0`, `navigation3-ui-android:1.1.0` and `lifecycle-viewmodel-navigation3-android:2.10.0` (also `activity-compose:1.12.0`), verified by reading `META-INF/com/android/build/gradle/aar-metadata.properties` inside each AAR; at compileSdk 35 `:app:checkDebugAarMetadata` fails, so ADR-42's approved nav3 artifacts cannot be consumed. §8.2's "compileSdk/targetSdk 35 … unchanged" row needs updating (targetSdk 35 and minSdk 26 are untouched).

Validation (all executed this session, JDK 21 / Gradle 9.1.0; platform 36 downloaded by AGP, license auto-accepted):
- `:app:assembleDebug` → **BUILD SUCCESSFUL** (42/42 executed; `checkDebugAarMetadata` passed at compileSdk 36).
- `:app:testDebugUnitTest` → **BUILD SUCCESSFUL — 41 result files / 253 tests / 0 failures / 0 errors**; `RoutesTest` actually executed (0.039 s, not `UP-TO-DATE`).
- `:app:compileDebugAndroidTestKotlin` → **BUILD SUCCESSFUL** (executed).
- Grep over `app/src` + build files: `composable(Routes.` → **0**; `navigate("` → **0**; `rememberNavController` → **0**; code-level `NavHost` → **0** (one KDoc prose mention of Nav2's `NavHost`); `import androidx.navigation.compose` → **0**; `NavKey` / `@Serializable` / `entryProvider` / `NavDisplay` present in `Routes.kt`, `MomentumNavGraph.kt`, `RoutesTest.kt`.
- `git status` / `git diff` reviewed: this phase touched only its own files; all unrelated working-tree changes preserved.
- **0 correction cycles needed** (first compile + test pass succeeded).

Handoff notes:
- **Phase F must add R8 keep rules for back-stack restore**: Android's `rememberNavBackStack` deserializes through `NavKeySerializer` → `Class.forName(FQCN).kotlin.serializer()`, so the `com.momentum.app.presentation.navigation.Routes` key classes must not be renamed/stripped in release builds.
- Pre-existing §8.4 #9 unchanged: `hilt-navigation-compose 1.2.0` still drags unused `navigation-compose 2.5.1` transitively (do not add Nav2 usage).
- Runtime smoke (tab switch/re-select + scroll restore, create flow, detail open/back, notification deep-open, predictive back) is **Reviewer** work per §8.5 Phase C — not performed here. Watch item: tab scroll restoration depends on decision 1.

**Phase D (MVI conformance + Turbine, ADR-44) — COMPLETE, verified (2026-09-29).** Today, Creation and Activities moved to sealed events + a single `onEvent`, atomic state, and typed one-shot effects; Habits' effect channel exposed. The seven MVVM-retained ViewModels (ADR-44) are structurally unchanged apart from scope items 2–3 below.

Changes:
- `today/TodayViewModel.kt` — `TodayEvent` (`Refresh`, `CompletionTap`, `SaveTargetEntry`, `ClearTargetEntry`, `DismissTargetEntry`) + `fun onEvent`; `refresh` / `onCompletionTap` / `saveTargetEntry` / `clearTargetEntry` / `dismissTargetEntry` made **private**. `TodayUiState` gained the interface property `targetEntry: TargetEntryState?` (`Loading` / `Error` → `get() = null`; `Success` override defaults to `null`), so the screen reads one object: `val state: StateFlow<TodayUiState> = combine(refreshTick.flatMapLatest { combine(3 sources) }, _targetEntry) { base, targetEntry -> … }.stateIn(viewModelScope, WhileSubscribed(5000), Loading)`. The separate public `targetEntry` StateFlow was removed; `_targetEntry` is written only through `.update`.
- `today/TodayScreen.kt` — one `state.collectAsStateWithLifecycle()`; dialog renders `state.targetEntry`; all five actions dispatch `TodayEvent`.
- `creation/CreationViewModel.kt` — `CreationEvent` (`Update((CreationForm) -> CreationForm)`, `SelectIntent`, `SelectTemplate`, `Save`) + `onEvent`; `update` / `selectIntent` / `selectTemplate` / `save` made private. Call sites in `HabitConfigScreen.kt` and `TaskConfigScreen.kt` dispatch events.
- `activities/ActivitiesViewModel.kt` — the four independent filter `MutableStateFlow`s consolidated into one private `ActivityFilters` holder (`_filters` = query / tab / categoryId / sort, one `.update` per event); `ActivitiesEvent` = `QueryChanged`, `TabChanged`, `CategoryChanged`, `Archive`, `Pause`, `Resume`, `Restore`, `ToggleToday`, `DeleteHard`; `ActivitiesUiEffect.ShowMessage` replaces the former `Channel<String>`; exposed as `val effect` plus typed `val state: StateFlow<ActivitiesUiState>` (3-source combine). `ActivitiesScreen.kt` collects `effect` through `when` and dispatches all nine events.
- `habits/HabitsViewModel.kt` — `Channel<HabitsEffect>` exposed as `val effect` (renamed from `messages`); `HabitsScreen.kt` collects `vm.effect`. Habits stays MVVM (ADR-44) — no `onEvent`.
- `calendar/CalendarViewModel.kt` — the three remaining naked `x.value =` writes (scope item 3) → `.update {}`; its `Channel<String>` is retained (MVVM).
- `gradle/libs.versions.toml` + `app/build.gradle.kts` — `app.cash.turbine:turbine:1.2.0` as **`testImplementation` only**. AGENTS §8 justification: test-only assertion library that replaces hand-rolled flow collection (§8.5 Phase D, skill ref 11 pin); no production usage, no architectural dependency.

Tests (Turbine adoption — assertion mechanism only, no test added, removed, or re-specified; count held at 253):
- `CreationViewModelTest.kt` — 56 action calls rewritten to dispatches (`vm.save()` ×25, `vm.update {…}` ×23, `vm.selectIntent(…)` ×2, `vm.selectTemplate(…)` ×6 → `vm.onEvent(CreationEvent.…)`); 15 `vm.effect.first()` → file-local Turbine `awaitEffect(vm)` helper.
- `HabitDetailViewModelTest.kt` — 4 `vm.state.first { … }` → Turbine `awaitState(flow) { … }`; 1 `vm.effect.first()` → `awaitEffect`.
- `HabitsViewModelTest.kt` — 7 `vm.state.first { … }` → `awaitState`.
- Decision: each suite's existing `Dispatchers.setMain(StandardTestDispatcher(testScheduler))` + `resetMain` kept; the skill's `MainDispatcherRule` was **not** introduced (its Unconfined default would change scheduler timing in these timing-sensitive Room suites).

Validation (JDK 21 / Gradle 9.1.0, all executed this session):
- `:app:assembleDebug` → **BUILD SUCCESSFUL** (only the pre-existing `MenuAnchorType` deprecation warnings).
- `:app:testDebugUnitTest` → **BUILD SUCCESSFUL — 41 result files / 253 tests / 0 failures / 0 errors / 0 skipped** (task executed, not `UP-TO-DATE`; `CreationViewModelTest` 36, `HabitsViewModelTest` 4, `HabitDetailViewModelTest` 4, `TodaySectionsTest` 8, `TodaySchedulingTest` 12 — the Today/`TodayScreen` source guards passed, including the literal `refreshTick.value += 1` contract).
- `:app:compileDebugAndroidTestKotlin` → **BUILD SUCCESSFUL** (executed).
- Grep over `app/src/main`: naked `…value =` writes in `*ViewModel*.kt` → **0**; `collectAsState()` → **0**; `Channel<String>` → only `CalendarViewModel` (MVVM-retained) + one Activities KDoc mention; `sealed interface TodayEvent` / `CreationEvent` / `ActivitiesEvent` with `onEvent` present in the three MVI ViewModels; `val effect` present in Habits and Activities; `refreshTick.value += 1` still present in `TodayViewModel.kt`.
- **0 correction cycles** for the Phase D source and test changes (first compile + test pass succeeded).

Reviewer corrections applied in this phase:
1. **Phase C NavKey/entryProvider counts** — corrected 11 → **12** `@Serializable` `NavKey`s (10 `data object`s + 2 `data class`es) and 11 → **12** `entryProvider` entries, both re-verified by grep.
2. **Phase C `activityCompose` forcing attribution** — the prescribed wording ("forced by compose BOM 2026.01.01 → material3-android:1.4.0; nav3 POMs declare no activity floor") was re-verified and is **factually wrong**; it was replaced in `gradle/libs.versions.toml`, `app/build.gradle.kts` and the Phase C entry above with the verified path: `navigation3-ui-android:1.1.0` and `lifecycle-viewmodel-navigation3-android:2.10.0` each declare `androidx.activity:activity-compose:1.12.0` in **both** their POM and Gradle module metadata, `dependencyInsight` re-run with this pin temporarily set back to `1.9.3` still selected `1.12.0`, the `compose-bom:2026.01.01` POM contains no `androidx.activity` entry, and `material3-android:1.4.0` requests only `activity-compose:1.8.2`. The 1.9.3 → 1.12.0 bump remains justified under §8.2 ("bump only if resolution forces") — only the attribution changed.

Handoff notes:
- Pre-existing defect discovered, **not fixed** (out of scope): `presentation/screens/today/TodayScreen.kt` contains double-encoded (mojibake) character sequences on 8 lines — 124, 143, 156, 170, 212, 337, 364, 409; line 124 `Text("Loading today…")` is **user-visible**. Recommend an `ISSUE` record and a Phase E fix (wording is otherwise untouched).
- Runtime verification of the target-entry dialog, the Activities filter/sort sheet and the snackbar effects is **Reviewer** work — not performed here.
- Phase E should keep `TodayScreen.kt` line 104's KDoc em dash (proper `—`) intact while fixing the mojibake above.

**Phase E1 — localization + typography — COMPLETE, verified (2026-09-30).** Two of the
three prescribed gates were **already at target before this pass**; the string-extraction
baseline recorded in the task brief was stale. What remained was a small set of literal
UI copy in helper/data classes that the `Text("` gate never covered. No product, layout,
color, navigation, ViewModel or animation change.

**Baseline correction (material to the §8.5 Phase E record).** §8.5 prescribed "53 literal
`Text("` → `stringResource`" and "34 raw `sp`/`fontSize` → typography tokens". Measured on
the working tree at the start of E1: `Text("` with a non-empty literal in `app/src/main`
(excluding `exploration/phase2b/**`) = **0**, and raw typography `*.sp` / `fontSize =` /
`lineHeight =` outside `presentation/theme/**` = **0** (all 18 raw `sp` values live in
`presentation/theme/Type.kt`, which *is* the token definition — the seven Design System
tokens Display 40/48, Headline 28/34, Title 20/26, Body 16/24, Body Small 14/20, Label
13/18, Caption 12/16, plus two `titleMedium` variants 16/22 and 17/26). Both extractions
had already landed in the uncommitted TASK-010/TASK-011 working tree; E1 therefore did not
re-do them and did **not** add a single typography token.

The **mojibake premise is also stale.** A codepoint-level scan of every `.kt` file in
`app/src/main` (every code point > U+007F printed with its hex value) found **zero
double-encoded sequences**. Every non-ASCII character in the codebase is a correct
typographic code point — `—` U+2014, `…` U+2026, `·` U+00B7, `§` U+00A7, `→` U+2192,
`×` U+00D7, `≥` U+2265, `≈` U+2248, `α` U+03B1, `±` U+00B1, `−` U+2212. The Phase D
handoff note ("8 lines of double-encoded characters in `TodayScreen.kt`") described the
**console rendering** of those lines, not the bytes; `TodayScreen.kt` line 124's
user-visible `Loading today…` is already `stringResource(R.string.today_loading)` with a
correct U+2026 ellipsis. No corruption fix was needed and none was made. **The Phase D
defect record is hereby discharged as not-a-defect; no ISSUE should be opened for it.**

Strings actually extracted in E1 (all literals reachable from a `@Composable` without
putting resources in a ViewModel):
- `MomentumIconOption.label: String` → `@param:StringRes labelRes: Int` (`MomentumIcons.kt`),
  18 picker labels as `icon_*`; the three `CreationFields.kt` call sites resolve them with
  `stringResource(option.labelRes)`.
- `completionActionLabel` split into a pure `@StringRes completionActionLabelRes(activity,
  completed)` plus a `@Composable completionActionLabel(...)` wrapper (`ActivityCard.kt`);
  4 TalkBack labels as `cd_complete_activity` / `cd_mark_incomplete` / `cd_enter_progress` /
  `cd_edit_progress`.
- `CalendarScreen.kt` day-cell `contentDescription`: the inline
  `", ${completed} of ${scheduled} complete"` → `calendar_day_complete_suffix`
  (`, %1$d of %2$d complete`). `calendar_day_complete` already existed without the comma
  and was reused nowhere, so a separate resource keeps the leading comma byte-identical.
- `ActivityDetailScreen.kt` schedule row: `" · from %1$s"` / `" to %1$s"` as
  `detail_schedule_from` / `detail_schedule_to`, sharing one `DateTimeFormatter`.
- `HabitTemplate.label: String` → `@get:StringRes labelRes: Int` (`HabitTemplates.kt`);
  `templateDisplayLabel` collapses from a 6-branch id `when` to
  `stringResource(template.labelRes)`. New: `template_custom`, `template_social_media`,
  `template_sugar`, `template_spending`, `template_screentime`; the existing `template_read`
  / `_exercise` / `_meditate` / `_hydrate` / `_learn` now back the data directly instead of
  the removed `when`.
- `MomentumThemeDefinition.name: String` → `@get:StringRes nameRes: Int` (`theme/Color.kt`)
  + `theme_name_aurora`; the two consumers (`SettingsScreen.kt`,
  `ThemeGalleryScreen.kt`) resolve it with `stringResource`.

Decisions worth recording:
1. **Data stayed literal.** `HabitTemplate.defaultTitle` ("Read books", "Social media", …)
   and `Activity`'s seeded titles are written onto the user's rows and matched back by
   `matchTemplate` in edit mode — persisted data, not UI copy, so they were deliberately
   left alone and the KDoc now says so. Same reasoning kept `CreationViewModel.SupportedUnits`
   and `RepositoryImpls`' default `Category(name = …)` seeds literal.
2. **`stringResource` in an inline lambda.** `ActivityDetailScreen`'s schedule value is
   still `buildString { … }`; `stringResource` is called inside that inline lambda in the
   composable scope, matching the existing `MomentumIconOptions.forEach { … }` pattern. No
   control-flow change, so the rendered line is unchanged.
3. **Leading-whitespace strings.** `detail_schedule_from` / `detail_schedule_to` begin with
   a space, which `aapt2` would trim from an unquoted value. They use the documented
   quote-preserving form (`<string name="…">" · from %1$s"</string>`). **Verified against
   the built APK**, not assumed — `aapt2 dump resources app-debug.apk` shows
   `string/detail_schedule_from = " · from %1$s"` and `string/detail_schedule_to = " to %1$s"`
   with the leading space and the `·` intact. `cd_enter_progress` / `cd_edit_progress` were
   checked the same way and the `\'` escapes resolved to `'`.
4. **Separators stayed literal.** `" · "` in the Activities / Calendar / Today / Activity-detail
   metadata joins is punctuation joining dynamic values, not copy; it was left as-is rather
   than split across resources, which would break word order in any translation.
5. **`@Preview` fixtures untouched** (`DesignSystemPreviews.kt`, `ActivityCard.kt`,
   `HabitsScreen.kt`, `HomeHero.kt`, …): not shipped UI, and every one is already inside a
   `@Preview` function.

Not done, and why (reported, not silently dropped):
- **ViewModel-produced user-visible strings are still literals.** `TodayViewModel`
  (4 greetings, 6 motivational lines, "All done — momentum secured.", "Couldn't load
  today's plan.", 3 × "Couldn't save — try again."), `ActivitiesViewModel` ("Not scheduled
  today."), `CalendarViewModel`, `HabitsViewModel`, `ActivityDetailViewModel`,
  `HabitDetailViewModel`, `SettingsViewModel`, `CreationViewModel` (4 validation messages +
  2 effects), and `InsightsViewModel`'s "Uncategorized". These are blocked by the two
  Phase-E1 guardrails meeting head-on: "move ALL user-visible literal strings" versus
  "ViewModels stay resource-free" + "NO changes to ViewModels". Extracting them requires
  changing 8 effect/state contracts to `@StringRes` (or a message-key enum) resolved at the
  screen — an architecture-shaped decision this phase must not make unilaterally. **This is
  the one genuine Phase E1 finding and it needs an Orchestrator/Architect call.**
- `ActivityCard.recurrenceLabel` ("One-time", "Daily", "Weekdays", "Every N days",
  "Weekly", "Every N weeks", "Monthly") is still literal for the same reason: it is called
  from `HabitsViewModel.kt:124`, so it cannot become `@Composable` without a ViewModel
  change. Same call to make; resolving it also fixes `HabitsViewModel`'s
  `"$frequency · $target · $reminder"` join.

Tests: **one suite adjusted, no test added or removed — the 253 total is unchanged.**
`CompletionLabelTest`'s four `completionActionLabel` tests asserted English sentences that
moved into `strings.xml`. Each now asserts the `completionActionLabelRes` mapping *and*
pins the exact sentence plus its `%1$s` slot by reading `res/values/strings.xml` (the
file's existing source-guard idiom), so both halves of the contract stay covered. The
`ActivityCardSurfaceTest` source guard
(`contentDescription = completionActionLabel(activity, completed)`) needed no change — the
call site text is identical.

Validation (all executed this session, JDK 21 / Gradle 9.1.0):
- `:app:assembleDebug` → **BUILD SUCCESSFUL**. One correction cycle: `@get:StringRes` on a
  top-level function is rejected by the compiler → `@StringRes`; one follow-up to silence
  KT-73255 by pinning `@param:StringRes` on the `MomentumIconOption` constructor property.
  Only the pre-existing `MenuAnchorType` deprecation warnings remain.
- `:app:testDebugUnitTest --rerun-tasks` → **BUILD SUCCESSFUL — 41 result files / 253 tests /
  0 failures / 0 errors / 0 skipped**. `CompletionLabelTest` executed (7 tests, 0.013 s),
  not `UP-TO-DATE`.
- `:app:compileDebugAndroidTestKotlin --rerun-tasks` → **BUILD SUCCESSFUL**.
- `aapt2 dump resources app/build/outputs/apk/debug/app-debug.apk` → all 32 new/changed
  string values confirmed byte-identical to the literals they replaced.
- Grep over `app/src/main` excluding `phase2b`: `Text("` with a non-empty literal **0**;
  `stringResource(` **334**; raw `.sp` **0**, `fontSize =` **0**, `lineHeight =` **0**,
  `letterSpacing` **0** outside `presentation/theme/**`; mojibake `â€` sequences **0**
  across all of `app/src/main` (codepoint scan).
- No correction cycles beyond the two compile fixes above.

Handoff notes:
- Phase E2 (structural Compose quality: modifier ordering, lazy-list `key`/`contentType`,
  ≥48dp targets, semantics, reduced-motion, edge-to-edge insets, stability) is untouched
  and remains the open half of Phase E.
- The material3 1.3.0 → 1.4.0 visual-regression watch (§8.4 #10) still needs a runtime
  check; E1 changed no sizing, so it does not move that risk.
- `git status` reviewed: E1 touched only its own 12 files; all Phases A–D and
  TASK-010 working-tree changes preserved. No commits.

**Phase E1b — ViewModel string-resource contracts + recurrenceLabel layering — COMPLETE,
verified (2026-09-30).** Authorized follow-up to E1's escalation. Every user-visible
string a ViewModel produces is now resource data the screen resolves; the
`recurrenceLabel` layering inversion is fixed. **No rendered wording changed** (proved
byte-for-byte below), no navigation/DI/state-shape/color/layout/animation change, no
dependency added, Phase 2B untouched. ADR-44 is unchanged — only the *representation* of
the strings moved.

**The pattern (one mechanism, reused 34×).** New `presentation/UiText.kt`:

```kotlin
data class UiText(@StringRes val resId: Int, val args: List<Any> = emptyList())
@Composable fun UiText.resolve(): String = stringResource(resId, *args.toTypedArray())
fun UiText.resolve(context: Context): String = context.getString(resId, *args.toTypedArray())
```

Two resolvers, not two mechanisms: the `@Composable` one for anything rendered directly,
the `Context` one for one-shot effects (every effect flow is collected inside
`LaunchedEffect`, where `stringResource` is not callable — `getString` is exactly what
`stringResource` delegates to, so the wording is identical). A ViewModel picks the
resource; it never holds the sentence and never touches a resource API. `UiText?` (null
= "say nothing") carries today's Hero-line-empty cases, which were the `""` sentinels.

**Contracts converted (9 ViewModels, every string in the E1 finding list).**
- `TodayViewModel` — `greeting`, `message` (`String` → `UiText`/`UiText?`), `Error.message`,
  and both `TodayEffect`s. 4 greetings + 6 motivational lines + "All done — momentum
  secured." + "Couldn't load today's plan." + 3× "Couldn't save — try again.".
- `ActivitiesViewModel` — `ActivitiesUiEffect.ShowMessage`; "Not scheduled today.".
- `CalendarViewModel` — `Channel<String>` → `Channel<UiText>`; `val messages` retyped.
- `CreationViewModel` — `CreationForm.titleError` / `.measureError` (`String?` → `UiText?`),
  `CreationEffect.Error`; 5 validation messages + "Activity not found." + "End date can't be
  before start date." + the save failure.
- `HabitsViewModel`, `ActivityDetailViewModel`, `HabitDetailViewModel`, `SettingsViewModel`
  — `ShowMessage.message` → `UiText`.
- `InsightsViewModel` — `CategoryPerformance.name` → `String?`; null (no category, *or* a
  deleted one — both mapped to "Uncategorized" before) is resolved at `InsightsScreen`.
  The name is user data, so it stays literal.

**The layering fix (scope item 2).** `recurrenceLabel` was called from
`HabitsViewModel.kt:124` — a ViewModel reaching into presentation-layer display logic.
Removed: `HabitListItem.metadata: String` and the whole private `habitMetadata()` join are
gone from the ViewModel, along with its `recurrenceLabel`/`formatAmount` imports. The
`Recurrence`, target and unit were already in state on `item.activity`, so nothing new is
carried. `recurrenceLabel` became a `@Composable` (`stringResource` per branch) and
`HabitsScreen.HabitCard` now composes the line through `R.string.habits_metadata`
(`%1$s · %2$s · %3$s`). The four remaining call sites (Activity/Habit-detail, Activities,
Calendar) were already composables and needed no change — the `stringResource`-inside-
`buildString` precedent is E1's.

**String reuse vs. duplication.** "Weekly"/"Monthly"/"One-time"/"Weekdays" reuse the
existing `pattern_weekly`/`pattern_monthly`/`pattern_once`/`preset_weekdays` rather than
copying the value. "Activity not found." is a new `creation_error_activity_not_found`
(the existing `detail_activity_not_found` carries the same value but is detail-scoped, and
renaming it would have widened the diff); the duplication is deliberate and named.

**Tests: 253 → 254 (+1). No test was deleted or weakened; three suites adjusted.**
- `CreationViewModelTest` (36, unchanged count) — the 4 inline-error assertions now assert
  `UiText(R.string.creation_error_*)` **and** pin the exact English sentence in
  `strings.xml` via a new `assertString` helper (E1's `CompletionLabelTest` idiom), so both
  halves of the resource contract stay covered.
- `HomeHeroAssetsTest` (10, unchanged) — `the greeting still follows the local clock` no
  longer greps the four greetings out of the ViewModel. It still pins the `LocalTime.now()`
  clock branch, now also pins that the ViewModel names all four `today_greeting_*` resources,
  and pins each resource's wording in `strings.xml`.
- `HabitsViewModelTest` 4 → 5 (**the +1**) — the metadata test now asserts the structured
  inputs the ViewModel actually produces (recurrence, target, unit, reminder) instead of the
  pre-composed line, and a new `the metadata row is composed in the screen from a format
  resource` covers the moved half: the exact `%1$s · %2$s · %3$s` value, the HabitsScreen
  call site, **and** that `HabitsViewModel` contains neither `recurrenceLabel` nor
  `formatAmount`. Equivalent coverage, split along the new layer boundary.

**Byte-identity proof.** `aapt2 dump resources` turned out to be unusable for this on
Windows — it writes its console output in the OEM codepage, so every em dash (U+2014) and
middot (U+00B7) comes back as 3 mangled code units. Verification was therefore done at the
byte level instead: the pre-E1b sources were restored byte-exactly from HEAD (`git show`
via `cmd` redirection, to avoid PowerShell's re-encoding) and each new resource was
compared **ordinally** against the literal that file used to contain. **43 mappings
checked, 0 differences** — including all 12 em-dash strings, the middot-separated
`habits_metadata`, both `%1$d` recurrence formats, and every `\'`-escaped apostrophe
(`Couldn't`, `today's`, `You're`, `can't`) resolving back to the straight `'` the Kotlin
literal had.

Validation (all executed this session, JDK 21 / Gradle 9.1.0):
- `:app:compileDebugKotlin` → **BUILD SUCCESSFUL**.
- `:app:assembleDebug` → **BUILD SUCCESSFUL** (only the pre-existing `MenuAnchorType`
  deprecation warnings).
- `:app:testDebugUnitTest --rerun-tasks` → **BUILD SUCCESSFUL — 41 result files / 254 tests /
  0 failures / 0 errors / 0 skipped** (baseline 253 + the 1 added; `HabitsViewModelTest` 5
  @0.156 s, `CreationViewModelTest` 36 @1.154 s, `HomeHeroAssetsTest` 10 @0.033 s,
  `CompletionLabelTest` 7 @0.012 s — all executed, not `UP-TO-DATE`).
- `:app:compileDebugAndroidTestKotlin --rerun-tasks` → **BUILD SUCCESSFUL** (executed).
- Grep over every `*ViewModel*.kt` in `app/src/main` for `Couldn't|Couldn't load|All done|
  Uncategorized|Give it a name|Keep it under|Enter an amount|Pick a unit|Keep the unit short|
  End date can|Activity not found|Not scheduled today|Good (morning|afternoon|evening|night)|
  Everything complete|Small steps|One small win|One more|First win|building momentum|
  All data cleared` → **0 hits**. The only literals left in ViewModels are empty strings,
  persisted-field defaults (`"spark"`, `"18:00"`, `"08:00"`, `SupportedUnits`), an
  exception message, and a `""` title — i.e. data, not copy, which is E1 decision #1.
- `HabitsViewModel.kt` for `recurrenceLabel|formatAmount|metadata` → **0 hits**.
- 32 new/4 reused strings all present in `strings.xml`; 34 `UiText(` constructions and 17
  `.resolve(` call sites across 9 ViewModels + 10 screens.
- E1 gates re-confirmed not regressed: literal `Text("` = **0**; `stringResource(` = **346**
  (was 334, +12 for the new resolution sites); raw `.sp` = **0** and `fontSize =` = **0**
  outside `presentation/theme/**`.
- 1 correction cycle: three screens (`ActivityDetailScreen`, `HabitDetailScreen`,
  `SettingsScreen`) were missing the `LocalContext`/`resolve` imports after the first edit.
- `git status` reviewed: E1b touched only its own 22 production files + 3 test files +
  `strings.xml`; every Phases A–E1 and TASK-010 working-tree change is preserved. No commits.

Handoff notes:
- `HomeHero(greeting: String, message: String)` is deliberately **unchanged** — `TodayScreen`
  resolves the `UiText` at the call site, so the component, its 4 `@Preview` fixtures and
  `HomeHeroCompositionTest` (androidTest) needed no change and the Hero's layout is
  untouched. Same call was made for `TodayError`.
- `CategoryPerformance.name` is now nullable; there is no other consumer in the app, but a
  future Insights view must resolve `R.string.insights_uncategorized` for `null`.
- `recurrenceLabel` is `@Composable` — any *new* non-UI caller must be refactored the way
  `HabitsViewModel` was, not reach for it.
- Phase E2 (structural Compose quality: modifier ordering, lazy-list `key`/`contentType`,
  ≥48dp targets, semantics, reduced-motion, edge-to-edge insets, stability) is untouched and
  remains the open half of Phase E. The material3 1.3.0 → 1.4.0 visual-regression watch
  (§8.4 #10) still needs its runtime check; E1b changed no sizing or layout, so it does not
  move that risk.
- Runtime verification of the resolvable effect snackbars, the Today Hero greeting/message
  across all four time bands, the inline creation validation errors, and the Habits metadata
  row is **Reviewer** work — not performed here (no device/emulator).

**Phase E2 — structural Compose quality — COMPLETE, verified (2026-09-30).** A
checklist pass against the installed skill (`android-kotlin-compose` §REVIEW MODE
6-point checklist, `references/00`, `/02`, `/10`, `/17`). **15 production files +
1 test file touched; 0 strings, 0 resources, 0 colors, 0 typography, 0 spacing
values, 0 animations, 0 navigation targets and 0 ViewModel behaviours changed.**
No new dependency. `presentation/exploration/phase2b/**` untouched (no compile
error forced a change). The material3 1.3.0 → 1.4.0 drift (§8.4 #10) is
invisible in code and still needs the runtime check.

**Checklist 1 — lazy-list `key` / `contentType`: 2 → 0 keyless calls; 0 → 9
contentTypes.** Nine `items(...)` calls exist in `app/src/main` (excl. phase2b);
before this pass two had no `key`:
- `ActivitiesScreen` — `items(ActivityTab.entries.toList())` → `items(ActivityTab.entries,
  key = { it.name }, contentType = { "tabChip" })`. The `EnumEntries` list is
  passed directly now, so the list is no longer re-copied on every recomposition.
- `ActivityDetailScreen` — `items(recent)` (the 14-day history `LazyRow`) →
  `items(recent, key = { it }, contentType = { "historyDay" })`; the date itself
  is the identity.
`contentType` was added to **all nine** (skill: "add `contentType` even where all
rows share a type"): `tabChip` / `categoryChip` / `activityRow` / `todayRow` /
`habitCard` / `historyDay` / `themeCard`. Today passes the **same**
`contentType = { "todayRow" }` on both `remaining` and `finished` on purpose —
they are one layout, and equal types are what let the row's composition be
reused when a row migrates between the two sections. No `verticalScroll` /
`rememberScrollState` exists anywhere in `app/src/main` (0 occurrences), so there
is no nested-scrollable hazard; every list is a single `LazyColumn`/`LazyRow`.
Filtering and sorting stay in `ActivitiesViewModel.combine` — nothing sorts
inside an `items {}` block.

**Checklist 2 — modifier ordering: audited, 0 violations, 0 changes.** Every
chain in the app already runs `size/fill → clip → background/border →
glassPress → clickable → padding`, i.e. `clickable` before the inner padding so
the touch target keeps the margin (skill §Anti-Patterns row 14 / ref 02
§Ordering). Two chains deviate cosmetically and were deliberately left:
`CompletionButton` puts `.shadow()` after `.clip()/.background()/.border()`, and
`IconChoice`/`SelectionRing` put `.clip()` before `.liquidGlass()` (which already
clips). Both draw identically as written — `Modifier.shadow` with an explicit
`shape` paints outside the shape's bounds, so the chain position cannot change
the result — and moving them risks exactly the visual drift this pass must not
cause.

**Checklist 3 — state hoisting / side effects: 0 violations, 0 changes.**
`collectAsState()` → **0** (every route uses `collectAsStateWithLifecycle`, one
collection each). Every ViewModel effect flow is collected exactly once inside a
`LaunchedEffect(vm)`. No repository/DAO/DB/network call in a composable body; the
one repository read in the UI (`CreationNav.EditRoute`) is inside
`LaunchedEffect(activityId)`, not in composition. Every `remember { mutableStateOf }`
holds ephemeral UI state (dialog visibility, dropdown/menu open, draft text,
onboarding page index) or documented layout/scroll plumbing (`collapsePx` /
`maxCollapsePx` / `heroPx` / `headerPx` / `contentPx` / `heroTopPx`) — **no
composable holds business state.**

**Checklist 4 — stability: 0 → 11 `@Immutable` annotations.** All-`val` UiState /
data classes that are **parameters of child composables** were annotated, which
is the skill's actual rule (ref 02 §Stability, ref 10 §Stability):
`ActivitiesUiState`, `HabitsUiState`, `HabitListItem`, `CalendarUiState`,
`DayCell`, `InsightsUiState`, `CreationForm`, `TodayUiState.Success`,
`DetailUiState.Success`, `HabitDetailUiState.Success`,
`theme/MomentumThemeDefinition`. **No `@Stable` was added anywhere** — the skill
makes it a promise of snapshot-observable mutation and there is no such class in
this codebase; annotating a plain-`var` class would be the documented lie.
Nested value types (`DayRate`, `CategoryPerformance`, `ActivityPerformance`,
`ActivityDetailStats`, `HabitOverview`, `HabitTemplate`) were **not** annotated:
they are reached only through an already-`@Immutable` parent, so annotating them
would be speculative.

**Checklist 5 — recomposition: 1 hot spot fixed.** `ActivitiesScreen`'s list row
was a ~55-line inline block inside `items {}` that captured the whole
`ActivitiesUiState`; because the state object changes identity on every emission,
every visible row re-ran on **every keystroke in the search field**. Extracted to
a private `ActivityListRow(activity, categoryName, tab, onOpen, onToggle,
onPause, onResume, onArchive, onRestore, onDelete)` composable — all-stable
parameters, so a row whose own inputs are unchanged now skips (skill ref 02
§Recomposition rule 1). Rendered output is byte-identical.
**No `derivedStateOf` was added.** The only frequently-changing-input /
less-frequent-output case is the hero collapse progress, and there the output
(`collapsePx / maxCollapsePx`) changes at exactly the same rate as its inputs,
which ref 10 §derivedStateOf names as the case to **skip** ("Skip when: input
changes at same rate as output"). Adding it would be pure overhead. The Home /
Habits collapse plumbing, the `HomeHero` veil and the Aurora transitions were
inspected and left exactly as they are — they are deliberately documented as
layout, not flourish.
**Stable lambdas in list items were deliberately NOT hand-`remember`ed.** The
skill's own ref 02 §Stability records that Compose compiler strong skipping
(on by default since Kotlin 2.0.20, so in Kotlin 2.2.10) auto-remembers lambdas;
the manual `remember(id) { { onClick(id) } }` form is the pre-2.0 workaround that
the same section marks as no longer needed, and it would only add per-item slot
groups.

**Checklist 6 — accessibility: ≥48dp 0 violations; 8 clickable rows merged; 1
live region added.** Every user-actionable target already measures ≥48dp through
a shared token (`MomentumHeights.iconTarget`/`controlMin` = 48dp,
`MomentumHeights.button` = 52dp, the two FABs 56dp, onboarding CTA 56dp / skip
48dp, Material3 `IconButton`/`Switch`/`TextButton` defaults) — **nothing had to be
resized and no layout intent changed.**
`mergeDescendants = true` added to the eight clickable **rows** whose descendants
are text/decorative only, so TalkBack reads the row once instead of the row and
then each of its texts:
`MomentumSectionHeader`'s trailing action, `MomentumSettingRow` (in its
`onClick != null` branch only — the `Switch` rows pass `onClick = null`, so their
interactive trailing control is never merged away),
`ActivityDetailScreen.DetailActionRow`, `HabitConfigScreen.SelectionRing` (both
intent and template cards), `CreationFields.CompactIconRow` / `DateRow` /
`TimeRow`, `CreateItemSheet.CreateItemOption`,
`ThemeGalleryScreen.ThemePreviewCard`.
**Deliberately not merged:** `ActivityCard` and `HabitsScreen.HabitCard`, because
each contains its own interactive descendant (`CompletionButton`, the overflow
`IconButton` + `DropdownMenu`) and merging would fold that control out of the
accessibility tree. `mergeDescendants` count 5 → **12**.
`LiveRegionMode.Assertive` added to the one genuinely dynamic, silent error —
`HabitConfigScreen.HabitMeasureSection`'s `measureError` text, which appears only
after the tap that caused it. The other error surfaces were left alone: the
`titleError` and Target-entry progress texts are `OutlinedTextField`
`supportingText`, which Material 3 already binds to the field's own semantics, and
`TodayUiState.Error` / `DetailUiState.NotFound` are whole-screen state swaps
announced on arrival, not inserts.
`LiveRegionMode` / `LiveRegionMode.Assertive` count 0 → **1**.

**Functional icons missing `contentDescription`: 0 → 0 (unchanged, re-verified).**
All 20 functional icons carry a `cd_*` string resource. The 17
`contentDescription = null` sites were each classified and are correct: every one
is a **decorative** glyph inside a control that is already labelled by adjacent
text, a merged `contentDescription`, or its own `onClickLabel` — `ActivityCard`'s
icon tile, `HabitIconTile`, `EmptyState`'s tile, `MomentumHeroArtwork`'s artwork,
`InsightsScreen`'s metric tile, `ActivityDetailScreen`'s two tiles,
`HabitConfigScreen.AccentBadge`, `HabitsScreen`'s three dropdown leading icons,
`HabitDetailScreen`'s week-strip check, `OnboardingBackgrounds`' backdrop,
`OnboardingLiquidGlass`'s CTA arrow, `SettingsScreen`'s chevron beside "Change",
`ActivitiesScreen`'s search leading icon, `CreationFields`' three icon-choice
glyphs (the choice itself is named by `IconChoice`'s `onClickLabel`, which the
onboarding buttons already pair with `semantics { contentDescription = … }`).

**Checklist 7 — edge-to-edge: compliant, 0 changes.** `enableEdgeToEdge()` is
called on `MainActivity` in `onCreate` **and** in `onConfigurationChanged`.
There is **no hardcoded status-bar dp anywhere**: every inset is
`WindowInsets`-derived — `ScaffoldDefaults.contentWindowInsets` and
`WindowInsets.systemBars.only(Bottom + Horizontal)` in `MomentumNavGraph`
(ADR-33's behind-the-bar opt-in for Home/Habits),
`windowInsetsPadding(WindowInsets.navigationBars.only(Bottom))` on the floating
bottom bar, `statusBarsPadding()` / `navigationBarsPadding()` on the Habit-detail
overlay chrome, `windowInsetsPadding(WindowInsets.navigationBars)` on the
onboarding CTA, and `WindowInsets.statusBars.asPaddingValues()` inside
`HomeHero` / `HabitsHero`. Nothing to fix.

**Checklist 8 — reduced motion: honoured, 0 changes, ISSUE-022 untouched.**
Every animation in the app reads `rememberReduceMotion`: `glassPress`,
`LiquidGlassTab`'s icon scale, `ActivityCard`'s tile tint, `CompletionButton`'s
pop, `ProgressOrb`'s glow + sweep, `CreationFields.Expandable`, the Aurora
background, `HomeHero` (ring scale, artwork zoom/parallax), `HabitsHero` (hero
text inset), `TodayScreen`'s list motion, `MomentumNavGraph`'s crossfade, and the
whole onboarding surface. This pass **introduced no animation**, so nothing new
had to be plumbed. The Settings `reduceMotionOverride` still reaches only
`TodayScreen` (every other caller passes `null`) — that is **ISSUE-022**, left
untouched as instructed.

**Tests: 254 → 254 (no test added, removed or weakened).** One source-guard
assertion adjusted, with equivalent coverage: `HomeAccessibilityTest`'s
`rows are grouped by completion state rather than tinted in place` pinned the
literal text `items(remaining, key = { it.activity.id })`; adding `contentType`
moved the closing paren, so both needles now stop after the key expression
(`items(remaining, key = { it.activity.id }` / `items(finished, …`). The
assertions still pin that each section is driven by its own state list. No other
suite referenced the changed code.

Validation (all executed this session, JDK 21 / Gradle 9.1.0):
- `:app:compileDebugKotlin` → **BUILD SUCCESSFUL** (only the pre-existing
  `MenuAnchorType` deprecation warnings).
- `:app:assembleDebug` → **BUILD SUCCESSFUL** (42 tasks, 7 executed).
- `:app:testDebugUnitTest --rerun-tasks` → **BUILD SUCCESSFUL — 41 result files /
  254 tests / 0 failures / 0 errors / 0 skipped** (executed, not `UP-TO-DATE`).
  `HomeAccessibilityTest` 4, `QuickAddTest` 5, `HomeSectionMotionTest` 4,
  `HabitsViewModelTest` 5, `CreationViewModelTest` 36 — all green.
- `:app:compileDebugAndroidTestKotlin --rerun-tasks` → **BUILD SUCCESSFUL**.
- Codepoint sweep of every `.kt` in `app/src` for U+FFFD → **0 files**, and the
  eight files touched by a scripted import insert re-verified as CRLF / no BOM /
  U+2014 intact (`CreationViewModel` 30, `theme/Color.kt` 35,
  `TodayViewModel` 9, `HabitDetailViewModel` 2, `HabitsViewModel` 1).
- **0 correction cycles** — first compile and first full test pass succeeded.
- Grep evidence (before → after, `app/src/main` excl. phase2b): lazy
  `items(`/`itemsIndexed(` lacking `key =` **2 → 0**; calls carrying
  `contentType` **0 → 9**; `collectAsState()` **0 → 0**;
  `verticalScroll`/`rememberScrollState` **0 → 0**; `@Immutable` **0 → 11**;
  `@Stable` **0 → 0**; `mergeDescendants = true` **5 → 12**; `liveRegion`
  **0 → 1**; literal `Text("…")` **0 → 0**; raw `.sp` outside `theme/**`
  **0 → 0**.

Handoff notes:
- **Still Reviewer work (no device/emulator here):** the material3 1.3.0 → 1.4.0
  visual-regression watch (§8.4 #10) and the Nav3 runtime smoke deferred from
  Phase C. E2 changed no size, colour, spacing or animation value, so it does not
  move either risk. The eight newly-merged rows should be spot-checked in
  TalkBack — merging only removes a duplicate read, but it is the one change here
  that a user can hear.
- `IconChoice` (the creation-flow icon picker) names each choice through
  `onClickLabel` rather than a `contentDescription`. It was **left as written**:
  replacing it would change what TalkBack announces, and the guardrail for this
  phase was to preserve existing announcements. If the Reviewer hears an
  unlabelled node there, the fix is one `contentDescription` per choice.
- `MomentumSettingRow`'s merge is conditional on `onClick != null`. Any future
  caller that passes `onClick` **and** an interactive trailing control must pass
  `onClick = null` or move the control out of the row, or the merge will fold
  that control into the row's node.
- `phase2b/**` compiled untouched; no compatibility change was forced.
- `git status` reviewed: E2 touched only its own 15 production files + 1 test
  file; every Phases A–E1b and TASK-010 working-tree change is preserved.
  No commits.

**Phase F — release / performance / final gates — COMPLETE, verified (2026-09-30).**
`isMinifyEnabled`/`isShrinkResources` on, release builds green through R8, keep
rules reduced to the single reflection path the app actually has, and
`profileinstaller 1.4.1` wired. **No Kotlin source, string, resource, color,
layout, navigation, DI or behavior change**; the only files touched are the two
build files and the ProGuard file. `phase2b/**` untouched and R8-stripped as
expected.

Changes:
- `app/build.gradle.kts` — `release { isMinifyEnabled = true; isShrinkResources
  = true }` (was `false`/unset). `proguardFiles(proguard-android-optimize.txt,
  "proguard-rules.pro")` was already wired and is unchanged. **No `signingConfig`
  added**: release is unsigned by default and signing is Human-in-the-Loop
  (AGENTS.md §12) — no keystore was created. Added
  `implementation(libs.androidx.profileinstaller)`.
- `gradle/libs.versions.toml` — `profileInstaller = "1.4.1"` +
  `androidx-profileinstaller` library entry (skill pin). No `baselineProfile
  { automaticGenerationDuringBuild }` block and no benchmark module (out of scope).
- `app/proguard-rules.pro` — was a one-line placeholder; now documents the
  verification per library and holds **two keep rules**:
  `-keep class …presentation.navigation.Routes { *; }` and
  `-keep class …presentation.navigation.Routes$* { *; }`.

Keep-rule decisions (each verified against the artifact actually on the
classpath, then against the release `mapping.txt`/`seeds.txt`):
1. **kotlinx.serialization + Nav3 — the one rule that is genuinely needed.**
   `androidx.navigation3.runtime.serialization.NavKeySerializer` writes
   `key.javaClass.name` into the persisted back stack and restores it with
   `Class.forName(className).kotlin.serializer()` (confirmed in the shipped
   `NavKeySerializer.class` bytecode: `java/lang/Class.forName` →
   `JvmClassMappingKt.getKotlinClass` → `SerializersKt.serializer`). The FQCN is
   therefore a runtime contract across process death **and app updates**, while
   kotlinx-serialization's own shipped rule
   (`META-INF/com.android.tools/r8/kotlinx-serialization-r8.pro`) keeps
   `@Serializable` classes only with `allowobfuscation`. Without these two rules
   the route names would be renamed. Confirmed in the release mapping: all 12
   `Routes$*` keys, their `$Companion` and `$$serializer` map to themselves
   unrenamed, and each appears in `seeds.txt`. That also discharges the Phase C
   handoff note "Phase F must add R8 keep rules for back-stack restore".
2. **Room — no app rule needed.** `room-runtime-android:2.7.1`'s consumer
   `proguard.txt` is 3 lines; the one that matters is
   `-keep class * extends androidx.room.RoomDatabase { void <init>(); }`, which is
   what saves the KSP-generated impl from
   `androidx.room.util.KClassUtil.findAndInstantiateDatabaseImpl` (`Class.forName`
   + `newInstance` — confirmed as the only `_Impl`/`forName` user in the Room
   runtime). Release evidence: `MomentumDatabase_Impl` is present and
   **unrenamed** in `mapping.txt`, its `void <init>()` survives, and both appear
   in `seeds.txt`. Room 2.7 needs nothing for `@Entity`/`@Dao` — the generated
   code references them directly. Adding a duplicate rule would only be noise.
3. **Hilt — no app rule needed.** `hilt-android:2.60.1`'s consumer
   `proguard.txt` ships the four `-keep,allowobfuscation,allowshrinking
   @dagger.hilt.*` rules covering the reflective EntryPoint cast; everything else
   is a direct bytecode reference from the Gradle plugin's ASM transform, and
   AGP's generated aapt rules keep every manifest-declared component. Release
   evidence: `MainActivity`, `MomentumApp`, `CompleteActionReceiver` all present
   and unrenamed in `mapping.txt`; `Hilt_MainActivity` is
   `R8$$REMOVED$$CLASS$$610` (merged into `MainActivity`, which is correct — it is
   only ever reached as a superclass).
4. **WorkManager — no app rule needed.** `work-runtime:2.9.0`'s consumer rules
   already keep every `ListenableWorker` subclass, its constructors and
   `WorkerParameters`. Release evidence: `FireReminderWorker` /
   `RescheduleWorker` present and unrenamed (their names are persisted in
   WorkManager's own DB, so this is the behaviour that must hold).
5. **navigation3 — no rule needed** (its AAR's `proguard.txt` is literally
   "intentionally empty proguard rules to indicate this library is safe to
   shrink").
6. **Nothing obsolete to remove**: the previous `proguard-rules.pro` contained
   only AGP's placeholder comment, and no other ProGuard configuration exists in
   the repo (root `build.gradle.kts` and `gradle.properties` carry none). No
   `-dontwarn` was needed — R8 reported no missing-class warnings.
7. **`phase2b/**` is deliberately not kept** (ADR-40): 47 phase2b classes appear
   in the release `usage.txt` as removed and 0 in `mapping.txt`, which is the
   intended outcome.

Performance hygiene: no-op, as expected. Phase E2 fixed the one real
recomposition hot spot (`ActivitiesScreen`'s inline list row) and Phase E1/E1b
introduced no new animation or state shape; this phase changed no Compose code at
all, so it cannot have introduced a recomposition problem. The one structural
leftover is §8.4 #9 — `hilt-navigation-compose 1.2.0` still drags an unused
`navigation-compose 2.5.1` transitively; R8 strips it, so it costs debug build
classpath only. Left as documented.

Validation (all executed this session, JDK 21 / Gradle 9.1.0):
- `:app:assembleDebug` → **BUILD SUCCESSFUL** (42 tasks, 42 executed). Only the
  pre-existing `MenuAnchorType` deprecation + KT-73255 warnings.
- `:app:testDebugUnitTest --rerun-tasks` → **BUILD SUCCESSFUL — 41 result files /
  254 tests / 0 failures / 0 errors / 0 skipped** (baseline parity; parsed from
  the XML, task executed, not `UP-TO-DATE`).
- `:app:compileDebugAndroidTestKotlin --rerun-tasks` → **BUILD SUCCESSFUL**.
- `:app:assembleRelease` → **BUILD SUCCESSFUL** (52 tasks; `minifyReleaseWithR8`,
  `optimizeReleaseResources`, `convertShrunkResourcesToBinaryRelease` all ran).
  Output `app/build/outputs/apk/release/app-release-unsigned.apk`, **4.04 MB**
  vs debug 22.77 MB. **Unsigned, not blocked**: AGP emits `-unsigned.apk` when no
  `signingConfig` is declared, so `assembleRelease` succeeds as-is; a keystore was
  not invented.
- `:app:minifyReleaseWithR8 --rerun` → **BUILD SUCCESSFUL with zero R8
  warnings** (no missing classes, no note/warning lines). The only warning in the
  build is the pre-existing Phase A experimental flag
  `android.disallowKotlinSourceSets=false`.
- Merge-manifest inspection: `androidx.profileinstaller.ProfileInstallerInitializer`
  and `androidx.profileinstaller.ProfileInstallReceiver` (with all four
  `androidx.profileinstaller.action.*` intent filters) are present in the merged
  release manifest, so the runtime is actually wired; **no `INTERNET` permission**
  (the single `INTERNET` grep hit in `app/src/main/AndroidManifest.xml` is the
  "no INTERNET permission" comment; the merged release manifest lists only
  POST_NOTIFICATIONS, RECEIVE_BOOT_COMPLETED, SCHEDULE_EXACT_ALARM plus the
  WAKE_LOCK/ACCESS_NETWORK_STATE/FOREGROUND_SERVICE that WorkManager 2.9.0 has
  always contributed).
- Grep over `app/src/main` for `getIdentifier|Class.forName|javaClass.name|
  ::class.java.name|.kotlin.serializer` → **0 hits**: the app itself does no
  reflection, so the two keep rules are genuinely the complete set.
- `git status` reviewed: Phase F touched only `app/build.gradle.kts`,
  `app/proguard-rules.pro` and `gradle/libs.versions.toml`; every Phases A–E2
  and TASK-010 working-tree change is preserved. No commits.
- **0 correction cycles** (one self-inflicted edit slip while inserting the
  catalog entry — a lost newline that joined two TOML lines — repaired before
  the first build; no build or test defect occurred).

Handoff notes:
- **`assembleRelease` is the R8 gate, but "R8 bugs only appear in release builds"
  (skill §Testing R8) still needs the Reviewer**: install
  `app-release-unsigned.apk` on a device and exercise Room open + migration, the
  Nav3 back-stack restore across process death (`Class.forName` path), Hilt
  injection, WorkManager reminders, and the notification-intent deep open. No
  device/emulator was available here. The two nav3 runtime smoke items deferred
  from Phase C and the material3 1.4.0 visual watch (§8.4 #10) are part of the
  same pass and are still **UNVERIFIED**.
- Release is unsigned by design. Signing config + keystore provisioning is
  Human-in-the-Loop and is not part of this task; if a signed artifact is
  ever needed in CI, the skill's env-var `signingConfigs` block is the route and
  must be added by a human with the keystore.
- No baseline profile ships in the APK yet (no `assets/dexopt/baseline.prof`) —
  only the `profileinstaller` runtime. Generating one needs a
  `baselineprofile`/Macrobenchmark module, which was explicitly out of scope.
- `PROJECT_STATUS.md` / `ISSUES.md` are Orchestrator-owned and were not touched.

Pending: the Reviewer runtime pass.

**TASK-012 — baseline profile (ADR-45) — PARTIAL: plugin-resolution defect fixed
(2026-09-30).** No baseline profile was generated in this pass (out of scope for
it): `app/src/main/baseline-prof.txt` does not exist and the release APK still
carries no `assets/dexopt/baseline.prof`. The `baselineprofile/` module and its
`settings.gradle.kts` registration now exist and the build configures.

**Fixed this pass (build configuration only, no version changes).**

1. *The reported failure* — `Plugin [id: 'com.android.test', version: '9.0.0']
   was already on the classpath with an unknown version`. The root
   `build.gradle.kts` puts AGP on the build classpath via
   `alias(libs.plugins.android.application) apply false`, and the module then
   re-declared the same `com.android.tools.build:gradle` artifact under a second
   id with an explicit version. Fix: apply it version-less in the module —
   `baselineprofile/build.gradle.kts:11` `alias(libs.plugins.android.test)` →
   `id("com.android.test")`. Gradle now resolves the id from the classpath.
2. *Surfaced once (1) was fixed* — `Unresolved reference 'testNamespace'` at
   `baselineprofile/build.gradle.kts:21`. Verified against the AGP 9.0.0 API
   sources (`com.android.build.api.dsl`): `testNamespace` exists only on
   `TestedExtension` (i.e. `com.android.application`) and
   `KotlinMultiplatformAndroidLibraryExtension`; `TestDefaultConfig` extends
   `TestBaseFlavor + DefaultConfig` and does **not** declare it. A
   `com.android.test` module derives its test R package from `namespace`, which
   is already `"com.momentum.baselineprofile"` — the exact removed value — so
   removing the property preserves intent. Line removed, with a comment.

**Validation (JDK 21, Gradle 9.1.0).**
- `.\gradlew.bat :app:assembleDebug --console=plain` → **BUILD SUCCESSFUL**,
  42 actionable tasks (11 executed / 31 up-to-date) — baseline parity.
- `.\gradlew.bat :baselineprofile:tasks --all --console=plain` → **BUILD
  SUCCESSFUL**.
- Generation task available: **`:baselineprofile:collectNonMinifiedReleaseBaselineProfile`**
  (group "Baseline Profile tasks" — the only task in it; 1.5.0 registers no
  `generate*BaselineProfile` on the producer side). It needs a booted,
  non-root API 33+ device (§8.7.6 step 2), which is a later pass.

**BLOCKER (permission boundary).** Step 1 of the prescribed pair — adding
`alias(libs.plugins.android.test) apply false` to the **root** `build.gradle.kts`
— could not be applied: the Developer's `edit` grant still has no rule for
`build.gradle.kts` (only `task.md`, `app/**`, `gradle.properties`,
`gradle/libs.versions.toml`, `.agents/**`, `settings.gradle.kts`,
`baselineprofile/**`). The `edit` call was refused. **The build is correct and
green without it** — the AGP version is still resolved exactly once, from the
root's `android.application` alias, which resolves the same artifact. Adding the
root line is hygiene, not correctness, and should be applied by whoever owns that
file together with the `libs.versions.toml:114-115` comment (which still says
"`android-test` is applied by the :baselineprofile module" and is now stale).

**Untouched, as required:** no version changed (AGP 9.0.0, Kotlin, KSP, Compose,
Navigation, Room, benchmark libs all as they were), `settings.gradle.kts`,
`gradle/libs.versions.toml`, root `build.gradle.kts`, `app/**` and all Kotlin
sources unchanged, `alias(libs.plugins.androidx.baselineprofile)` still in both
modules, no commit or push, every pre-existing working-tree change preserved.

**TASK-012 generation pass — COMPLETE, verified (2026-09-30).** A real baseline
profile was generated on a booted Pixel_7 AVD and **is packaged in the release
APK**. Two defects in the generator were found and fixed; no version, no module
configuration and no product code was changed.

**Defect 1 — the generator did not compile (build configuration of the test).**
`import androidx.benchmark.macro.junit4.startActivityAndWait` is unresolved.
Verified against the shipped artifacts: in `benchmark-macro:1.5.0`,
`MacrobenchmarkScope` declares `public final void startActivityAndWait()`
(`javap`, plus `startActivityAndWait$default` / `$lambda$0` in the constant pool)
— it is a **member of the `profileBlock` receiver**, not a top-level import, and
`benchmark-macro-junit4:1.5.0` contains only `BaselineProfileRule`,
`MacrobenchmarkRule` and the two run listeners. Fix: drop the import, add
`androidx.benchmark.macro.MacrobenchmarkScope`, and make
`journeyToHomeThenAcrossAllTabs()` an extension on `MacrobenchmarkScope` so the
member is in scope. `startup()` needed no change (its lambda already has the
receiver).

**Defect 2 — the app's own permission dialog aborted the run (the real blocker).**
With the compile fixed, both real tests reported PASSED and both wrote their
`*-baseline-prof.txt` to `/sdcard/Android/media/…/additional_test_output/`, but
the task still failed on a synthetic `Test mechanism` entry:
`Could not launch intent … androidx.benchmark.IsolationActivity within 45000
milliseconds`, thrown from `AndroidBenchmarkRunner.waitForActivitiesToComplete`.

Root cause, from the archived `logcat-…-journey.txt`:
`MainActivity.requestNotificationPermissionIfNeeded()` fires `POST_NOTIFICATIONS`
on every cold start, so `com.android.permissioncontroller`'s `GrantPermissionsActivity`
is raised on top of the app **repeatedly** (9 `Displayed …GrantPermissionsActivity`
lines in one `journey` block, 6 in `startup`). While that dialog holds the
foreground the app behind it is never the visible window, so every later selector
misses — and the runner's post-run `IsolationActivity` check cannot complete.
`collect` reinstalls the app per block, so the permission is reset each time.

Fix (generator only): `grantNotificationPermission()` runs **before**
`startActivityAndWait()` in both blocks and does
`pm grant com.momentum.app android.permission.POST_NOTIFICATIONS` through
`UiDevice.executeShellCommand`. The shell route was chosen over clicking because
(i) neither `By.res("com.android.permissioncontroller:id/permission_deny_button")`
nor `By.text("Don't allow")` matched while the dialog was demonstrably displayed
(both `Node not found` in the archived logcat — the resource id *is* correct per
a manual `uiautomator dump`, so the mismatch is uiautomator's cross-window
search against the permission dialog, not a wrong selector), and (ii) the dialog
is raised on every relaunch, so one click would not hold for the rest of the
block, while `pm grant` is idempotent. Recorded as a decision, not a workaround:
the app's permission behaviour, its manifest and its permission set are
untouched and the app under test is the same APK.

**Result.** `:baselineprofile:collectNonMinifiedReleaseBaselineProfile` →
**BUILD SUCCESSFUL** (4 m 36 s), "Finished 2 tests on Pixel_7(AVD) - 15", **0
failed**, and no `Test mechanism` entry this time. Then
`:app:copyBaselineProfileIntoSrc` (the `saveInSrc` writer; 1.5.0 registers it
separately from the collect task, so §8.7.6's implicit assumption that one task
does both does not hold) → **BUILD SUCCESSFUL**, and the plugin printed:

> A baseline profile was generated for the variant `release`:
> `app/src/main/generated/baselineProfiles/baseline-prof.txt`

**Profile location — differs from §8.7.7's expectation, recorded, not moved.**
`saveInSrc = true` + `mergeIntoMain = true` produced
`app/src/main/generated/baselineProfiles/baseline-prof.txt` (**2 486 193 bytes**),
**not** `app/src/main/baseline-prof.txt`. This is the AGP 8.3+ generated-source
directory, it is not gitignored, and AGP packages it — so per §8.7.7.1 the
actual path is recorded and the file is left where the plugin wrote it. It is a
new untracked path, so it needs adding to the index (Human-in-the-Loop; no
commit was made).

**Rule count (all three numbers §8.7.7.2 asks for).**
- total lines: **23 427**
- rule lines (non-blank, not `#`): **23 427** — the file has no comment or blank
  lines at all, so the two numbers coincide
- the count the Gradle task prints: the plugin prints **no** rule-count line. Its
  only profile-related output is the variant + path message quoted above, plus a
  *"No startup profile rules were generated for the variant `release`"* warning
  (see handoff notes). The 23 427 figure is therefore measured from the file,
  not quoted from Gradle.

Representative rules (human-readable flag form):
```
Landroidx/activity/ComponentActivity;
SPLandroidx/activity/ComponentActivity;-><clinit>()V
SPLandroidx/activity/ComponentActivity;-><init>()V
SPLandroidx/activity/ComponentActivity;->addOnContextAvailableListener(Landroidx/activity/contextaware/OnContextAvailableListener;)V
```
and the app's own classes (**1 649 rules** reference `com/momentum/app`), e.g.
```
Lcom/momentum/app/DaggerMomentumApp_HiltComponents_SingletonC;
HSPLcom/momentum/app/presentation/navigation/MomentumNavGraph;…
```
The profile is **not** thin and **not** onboarding-only (§8.7.8 risk 2 disproved):
per-screen coverage is `MomentumNavGraph` 193, `ActivitiesScreen` 103,
`TodayScreen` 52, `HabitsScreen` 37, `OnboardingScreen` 33, `InsightsScreen` 22,
`CreationViewModel` 6 — i.e. all four tabs were genuinely walked. `CalendarScreen`,
`SettingsScreen` and the creation flow are 0, which is correct: §8.7.5 puts them
out of scope.

**Release build + APK packaging evidence.**
- `:app:assembleRelease` → **BUILD SUCCESSFUL** (52 tasks, 43 executed); R8 ran
  (`minifyReleaseWithR8`), lint-vital passed.
- `app/build/outputs/apk/release/app-release-unsigned.apk` — still **unsigned**,
  **4 237 684 bytes (4.04 MB)**, i.e. the TASK-011 baseline size is essentially
  unchanged (the 7 KB of profile data is absorbed by R8's other savings; the APK
  is the same artifact path, `LastWriteTime` 12:28:14).
- Entries under `assets/dexopt/` read with .NET's `System.IO.Compression.ZipFile`
  (`apkanalyzer` is not installed, per §8.7.7.3):

  | entry | uncompressed | compressed |
  |---|---|---|
  | `assets/dexopt/baseline.prof` | **6 231 bytes** | 6 231 bytes (stored) |
  | `assets/dexopt/baseline.profm` | **805 bytes** | 805 bytes (stored) |

  Both are **stored, not deflated** — that is why compressed == uncompressed, and
  it is normal for `assets/dexopt/*` (AGP adds them to the no-compress list so ART
  can mmap them directly). 139 entries total in the APK.
- **What was compared to prove the packaged profile is the generated one** — the
  profile is *rewritten* on the way in, so a byte comparison against the source
  file would be meaningless. Three links were checked instead:
  1. **SHA-256 of the packaged entry** `4A6941A1D2324E38FAB5FEBEF4EA2B7C8373DD388596E5D370708173379AD827`
     is **byte-identical** to
     `app/build/intermediates/binary_art_profile/release/compileReleaseArtProfile/baseline.prof`
     (6 231 bytes). Same for `baseline.profm` (805 bytes,
     `CEEAF35621A07385FB87C8D6577E7CE3D2C0AEEEA2F7A8668BC703D013EE2CC2` =
     `binary_art_profile_metadata/release/compileReleaseArtProfile/baseline.profm`).
  2. **The content pipeline**, all freshly written by this run: source
     2 486 193 B (23 427 rules) → `mergeReleaseArtProfile` 2 791 803 B →
     `expandReleaseArtProfileWildcards` 5 742 932 B → `minifyReleaseWithR8`
     **410 082 B / 14 089 rules** → binary 6 231 B. The 23 427 → 14 089 drop is
     R8 rewriting the rules onto the minified/obfuscated names, which is exactly
     what the binary form encodes; the packaged 6 231 B is a subset of that
     410 082 B, not the source text.
  3. **Spot checks through `mapping.txt`**: the R8-stage profile still carries
     `Lcom/momentum/app/MainActivity;` (18 rules), `Lcom/momentum/app/MomentumApp;`
     (5) and `Lcom/momentum/app/presentation/navigation/Routes;` (1) — i.e. the
     Phase F keep rules' unrenamed classes survive, and 96 `com/momentum/app`
     rules survive obfuscation. `MomentumDatabase_Impl` has 0 rules, consistent
     with Room's database impl never being executed during this narrow journey.

**App build type profiled (§8.7.7.6).** `nonMinifiedRelease` — a build type the
plugin creates in `:app`, and the release one it profiles. The generation log
shows `:baselineprofile:checkTestedAppObfuscationNonMinifiedRelease` and
`writeNonMinifiedReleaseSigningConfigVersions` running; the plugin set the debug
signing config and `isProfileable` on that extended type only. The real `release`
build type is untouched: no `signingConfigs { }` block anywhere
(`app/build.gradle.kts:38` and `:77` are comments saying so), no `*.jks` /
`*.keystore` in the tree, and `app-release-unsigned.apk` is still unsigned.

**Scope guard — clean.** New/changed files are only:
`baselineprofile/src/main/kotlin/com/momentum/baselineprofile/BaselineProfileGenerator.kt`
(generator fixes) and the new untracked
`app/src/main/generated/baselineProfiles/baseline-prof.txt`. `git status
--porcelain app/schemas` is empty. `app/build.gradle.kts`,
`gradle/libs.versions.toml`, `settings.gradle.kts`, the root `build.gradle.kts`
and every Kotlin source under `app/src/main/java/**` are byte-unchanged by this
pass (the working-tree modifications listed against them are all pre-existing
Phases A–F / TASK-011 work, preserved). No `INTERNET` permission: the only
`INTERNET` hit in `app/src/main/AndroidManifest.xml` is the pre-existing
"Offline-first: no INTERNET permission" comment; the test APK's own
`INTERNET` (from `benchmark-macro`, localhost-only perfetto) is macrobenchmark's
own manifest and is not shipped in `app-release-unsigned.apk`. No commit, no
push.

**LIMITATIONS / handoff notes.**
- **The plugin prints no rule count**, so §8.7.7.2's third number cannot be
  quoted; the 23 427 is measured from the generated file.
- **No startup-profile rules.** The plugin warns *"No startup profile rules were
  generated for the variant `release`"* because no generator test passes
  `includeInStartupProfile = true`. The `startup()` pass still records
  startup-critical classes, but they land in `baseline.prof`, not the
  higher-priority `baseline.profm` (805 B present but derived from the AOT rules
  actually generated). Making the startup pass a real startup profile is a
  generator change (`collect(packageName, includeInStartupProfile = true)`) and is
  **not** done here — it is a profile-quality improvement, not a defect, and it
  needs an Architect/Orchestrator call because it changes what the second pass
  asserts.
- **The profile is a build artifact and will go stale** (§8.7.8 risk 3): it is
  captured against today's `nonMinifiedRelease` journey, and R8 name changes
  require re-running the collect task on a booted device. It is also a 2.4 MB
  source file — a human should decide whether it is committed or generated in CI
  (Human-in-the-Loop; nothing was committed).
- The generator's `By.res`/`By.text` selectors for the **permission dialog** are
  known not to match through uiautomator; that path is now unused (the permission
  is granted over the shell), but if anyone reverts to clicking, it will fail
  again. This is recorded in the function's KDoc.
- `benchmark-macro-junit4:1.5.0` resolves `androidx.test.uiautomator` to
  **2.4.0**, not the 2.3.0 the catalog pins (verified by
  `:baselineprofile:dependencyInsight`: `2.3.0 -> 2.4.0`). The classic
  `device.findObject(By…)` DSL §8.7.1 wanted is still what the generator uses, so
  no behavioral consequence, but the catalog's 2.3.0 pin is inert and its comment
  overstates the guarantee.
- **Profile staleness across a rebase**: the packaged 6 231 B encodes R8's
  obfuscated names. Any R8-relevant change (new keep rules, mapping change)
  invalidates it silently — the build stays green, the profile just stops
  matching. Same discipline as the ProGuard file.

**Emulator:** left running (`emulator-5554`, Pixel_7, API 35) — the Reviewer may
want it for the still-unverified R8 runtime pass.

---

**TASK-014 — COMPLETE (executed 2026-09-30, §8.8 / ADR-46).** The four obsolete
Taste-driven direction explorations and their debug source set are deleted. Two
`Remove-Item` calls, nothing else. No Gradle, manifest, ProGuard, resource,
schema, or dependency change; no commit; no push.

**The four deleted directions** (option (i) per §8.8.1 — exploration, *not* the
live design direction): **A Press** (`press/`), **B Instrument**
(`instrument/`), **C Loom** (`loom/`), **D Aurora Governed** (`aurora/`). The
live Aurora direction is a different set of files and is retained.

**Files deleted (17).** `app/src/main/.../presentation/exploration/phase2b/` (15):
`press/{PressHome,PressMaterials,PressTokens}.kt`,
`instrument/{InstrumentHome,InstrumentChannel,InstrumentTokens}.kt`,
`loom/{LoomHome,LoomWeave,LoomTokens}.kt`,
`aurora/{AuroraHome,AuroraHeader,AuroraTokens}.kt`,
`fixture/{Phase2BFixture,Phase2BModels}.kt`,
`preview/Phase2BPreviewScreen.kt`; plus `app/src/debug/AndroidManifest.xml` and
`app/src/debug/java/.../preview/Phase2BPreviewActivity.kt` — i.e. the whole
`app/src/debug/` source set.

**Resources deleted: none.** `res/drawable-nodpi/phase2b/**` was never created,
and the package's only `res/` references (`R.font.manrope`,
`R.drawable.habits_hero_light`) are shared with `presentation/theme/Type.kt`,
`MomentumHeroArtwork.kt` and `HabitsHero.kt`. `app/src/main/res` is
byte-for-byte untouched (17 files before and after).

**Tests deleted: none.** No test file referenced the package (verified: a
`presentation\.exploration|phase2b` search over `app/src/{main,test,androidTest,debug}`
returned hits *only* inside the two deleted trees), and the test source-set file
count is 35 before and after. XML totals prove it: 41 files / 254 tests /
0 failures / 0 errors / 0 skipped **before and after** — delta
`41−41=0`, `254−254=0`, `0−0=0`.

**Counts.** `app/src/main` Kotlin 96 → **81** (exactly −15). `app/src/debug` Kotlin
1 → **0**. `app/src/main/res` 17 → **17**. `app/src/test` Kotlin 35 → **35**.
`presentation/` is now `{aurora, components, navigation, screens, theme}` plus the
three root files (`HiltViewModelSupport.kt`, `ReduceMotion.kt`, `UiText.kt`).

**Gates (all executed, none UP-TO-DATE).** Baseline
`:app:testDebugUnitTest --rerun-tasks` → SUCCESS, 41/254/0.
`:app:assembleDebug` → SUCCESS (the absent `app/src/debug` does not break
packaging; AGP supplies an empty debug manifest, no Gradle edit needed).
`:app:testDebugUnitTest --rerun-tasks` → SUCCESS, 41/254/0.
`:app:assembleRelease --rerun-tasks` → SUCCESS.

**APK size: 4 237 684 B → 4 237 684 B, difference 0 B (no win).** Both figures
come from a full `assembleRelease --rerun-tasks`, measured *after* the stale
artifact was discarded, so the comparison is like-for-like. As §8.8.6 predicted,
release is `isMinifyEnabled = false` and the APK is dominated by untouched
assets; the 47 dead prototype classes cost nothing measurable. This is not
claimed as a benefit.

**Post-checks.** Repo-wide `phase2b|Phase2B|AuroraHeader|AuroraTokens` → the only
non-prose hits are `app/proguard-rules.pro:30` (the deliberately retained stale
comment — reported, not edited) and
`presentation/components/HeroAuroraPreview.kt`'s `HeroAuroraHeader`, a different
symbol in a different package (retained per §8.8.2). All other hits are prose in
`task.md`, `ISSUES.md`, `PROJECT_STATUS.md`, `MODERNIZATION_AUDIT.md` and
`DECISIONS.md`, left untouched as instructed. Merged release manifest
(`processReleaseMainManifest`): **exactly 1** `<activity>` (`com.momentum.app.MainActivity`),
**0** `exploration`, **0** `Phase2B`. `git status --short` is 146 lines: the same
**129** pre-existing unrelated entries as before (byte-identical status codes,
including every untracked file), plus the 17 deleted files, which are the only
entries whose status changed — `A ` → `AD`. No collateral modification.

**RETAINED, verified present and untouched.** `presentation/aurora/MomentumAurora.kt`
(1 file), `presentation/components/**` (11 files, incl. `MomentumHeroArtwork.kt`,
`HeroAuroraPreview.kt`), `presentation/theme/**` (8 files, incl. `Color.kt`'s
`AuroraLight`/`AuroraDark` and `DesignSystemPreviews.kt`), `screens/today/HomeHero.kt`,
`screens/habits/HabitsHero.kt`, `habits_hero_light/dark.webp`, `manrope.ttf`,
all of `app/src/main/res`, `docs/**` (incl. the 4 `docs/phase2b/round1/*.png`),
`design/aurora-concept/**`, `MOMENTUM_CONCEPT_EXPLORATION.md`,
`app/schemas/**` (4), `app/proguard-rules.pro`, and every Gradle/catalog file.

**LIMITATIONS / handoff notes.**
- `app/proguard-rules.pro:30-31` still carries the stale ADR-40 comment asserting
  that `presentation/exploration/phase2b/**` exists in the release APK. It is a
  **comment only** — no rule, no defect — and §8.8.4 places it out of scope, so
  it was **reported, not edited**. A future pass may want to retire it.
- `PROJECT_STATUS.md` `F-CONCEPT-2B` still reads as though Round 2 is pending, and
  `ISSUE-025`/`026`/`027` are still OPEN. Per §8.8.5 these are Orchestrator-owned
  and were **not** edited here; the recommended closures (026 → CLOSED,
  027 → CLOSED as resolved-by-removal, 025 → CLOSED as moot) are unchanged and
  still await the Orchestrator.
- The debug variant's UI was not launched on a device; the deleted
  `Phase2BPreviewActivity` had no device-verified behaviour after TASK-009 and
  its removal is covered by `assembleDebug` + manifest inspection only.
- No runtime check of the retained production Aurora hero was performed; it is
  unchanged source, so no behavioural claim is made.

---

## 10. REVIEW FINDINGS AND VALIDATION

<!-- REVIEWER OWNER -->

**RESULT: PASS WITH FINDINGS** — final end-state verification of TASK-011
(2026-09-30, read-only; no implementation file was modified).

All four build/test gates were **executed** (not `UP-TO-DATE`) in one
`--rerun-tasks` invocation and passed. All §8.2 versions, ADR-41…44 and every
conformance gate in the brief were re-derived independently. One pre-existing
product defect was found that is **not** a modernization regression (finding 1,
routed to the Orchestrator's issue registry); everything else is documentation
level.

### Findings

1. **MINOR / pre-existing, NOT a TASK-011 regression — the onboarding gate
   re-shows onboarding on every cold start.**
   `presentation/navigation/MomentumNavGraph.kt:159` resolves
   `rememberNavBackStack(if (onboardingDone) Routes.Today else Routes.Onboarding)`
   once, while `MainActivity.kt:63` seeds
   `collectAsStateWithLifecycle(AppSettings())` with `onboardingDone = false`
   until DataStore's first real emission arrives. Reproduction on the debug APK
   (Pixel_7 AVD, API 35): complete onboarding → Today renders → force-stop →
   cold start → onboarding page 1 again. The flag **is** persisted correctly
   (`run-as … momentum_settings.preferences_pb` contains
   `onboarding_done … 01`); the start key simply never re-seeds.
   **Not a regression:** `git show HEAD:…/MomentumNavGraph.kt` has the
   byte-identical construct (`NavHost(startDestination = if (onboardingDone)
   Routes.TODAY else Routes.ONBOARDING)`) and `git show HEAD:…/MainActivity.kt`
   the identical `collectAsState(initial = AppSettings())`. The Nav3
   translation preserved the existing behaviour exactly.
   **Required correction:** none inside TASK-011 (out of scope). Open an
   `ISSUE-NNN` record for the Orchestrator; the fix is to key the graph on a
   loaded state (e.g. render nothing until the first DataStore emission, or
   `LaunchedEffect(onboardingDone) { if (it) backStack.reset(Routes.Today) }`).
2. **NOTE — material3 1.3.0 → 1.4.0 visual-regression watch (§8.4 #10) remains
   UNVERIFIED.** A before/after comparison is impossible in this repository:
   `HEAD` (`bd1bafb`) predates TASK-010 and TASK-011 entirely, so no
   pre-modernization APK exists and none was built. What *was* verified at
   runtime: every screen renders with no clipping, overlap or metric error under
   1.4.0 (onboarding ×4 pages, Today, Habits, Tasks, Insights, Activity-detail,
   Habit-detail, Habit-config edit, Task-config, Themes-adjacent chrome), and
   non-ASCII copy renders correctly (U+2014 em dash confirmed in the Task-config
   "One-time — monthly bills and weekly chores can repeat" node). E1/E1b/E2
   changed 0 size, spacing, colour and animation values (grep-confirmed), so the
   drift surface is material3's own, not the diff's.
3. **NOTE — TalkBack on the 8 newly merged rows (Phase E2) is structurally, not
   audibly, verified.** `mergeDescendants = true` count is 5 → **12**;
   `MomentumSettingRow`'s merge is correctly conditional on `onClick != null`
   so Switch rows keep their trailing control; `ActivityCard` / `HabitCard` are
   deliberately unmerged. No screen reader was driven on the AVD.
4. **NOTE — predictive back.** The back *contract* was exercised (system back
   from Task-config → Today, from detail → Today, from Edit → detail, at root
   exits). The predictive-back **gesture** and its 150 ms crossfade were not
   driven.
5. **NOTE — `IconChoice` (creation icon picker) is labelled via `onClickLabel`,
   not `contentDescription`.** Left as written by Phase E2 by design (changing
   it would change existing TalkBack announcements). Carried forward unchanged.
6. **NOTE — ISSUE-028's Robolectric `LEGACY` package override is still present**
   (`app/src/test/resources/com/momentum/app/data/local/robolectric.properties`).
   Tracked and open; the recommended follow-up (complete the v1 fixture DDL, then
   delete the file) is test-source work outside every phase of this task. Not a
   TASK-011 defect.

### Validation performed (all executed 2026-09-30, JDK 21 / Gradle 9.1.0)

| Gate | Command | Result |
|---|---|---|
| Debug assemble | `:app:assembleDebug --rerun-tasks` | **executed**, BUILD SUCCESSFUL |
| Unit tests | `:app:testDebugUnitTest --rerun-tasks` | **executed**, BUILD SUCCESSFUL — XML parsed: **41 files / 254 tests / 0 failures / 0 errors / 0 skipped** (expected 41/254/0) |
| androidTest compile | `:app:compileDebugAndroidTestKotlin --rerun-tasks` | **executed**, BUILD SUCCESSFUL |
| R8 gate | `:app:assembleRelease --rerun-tasks` | **executed**, BUILD SUCCESSFUL — `minifyReleaseWithR8` + `optimizeReleaseResources` ran; `app-release-unsigned.apk` 4.04 MB (debug 22.77 MB) |
| Instrumentation | `:app:connectedDebugAndroidTest` | **executed** on Pixel_7 AVD (API 35): **7 tests, 7/7, 0 failed, 0 skipped** |

Conformance re-derived independently (not taken from §9):

- **Toolchain** — `libs.versions.toml` / `app/build.gradle.kts` /
  `gradle-wrapper.properties` / resolved `debugRuntimeClasspath`: AGP 9.0.0,
  Gradle 9.1.0, Kotlin 2.2.10 (no `org.jetbrains.kotlin.android`, no `kapt`,
  no `kotlinOptions {}`), KSP `2.2.10-2.0.2`, Compose BOM 2026.01.01 → material3
  **1.4.0**, nav3 runtime+ui **1.1.0**, `lifecycle-viewmodel-navigation3`
  **2.10.0**, kotlinx-serialization **1.8.1**, lifecycle **2.10.0**, Room
  **2.7.1**, Coroutines **1.10.2**, DataStore **1.1.7**, Hilt **2.60.1**,
  androidx.hilt **1.2.0**, Turbine **1.2.0** (test-only), profileinstaller
  **1.4.1**, activity-compose 1.12.0, compileSdk **36** / targetSdk 35 / minSdk
  26 / jvmTarget 17. Every value matches §8.2 / ADR-41…44. §8.4 #9 confirmed
  still open (`hilt-navigation-compose 1.2.0` → `navigation-compose 2.5.1`,
  no Nav2 usage anywhere).
- **Navigation 3** — 12 `@Serializable : NavKey`s (10 `data object` + `Edit`/
  `Detail` with `activityId: Long` only), 0 string routes, 0
  `import androidx.navigation.compose`, 0 `rememberNavController`, 0
  `composable(Routes.`, 0 `navigate("`; one `NavDisplay` with 12
  `entryProvider` entries. Tab/back/deep-link parity verified **at runtime**,
  not just statically (below).
- **Hilt** — `@HiltAndroidApp` (MomentumApp), `@AndroidEntryPoint`
  (MainActivity, CompleteActionReceiver), `@HiltWorker`
  (FireReminderWorker, RescheduleWorker), 5 `di/` modules + `AppEntryPoint`,
  `AppContainer.kt` / `presentation/ViewModels.kt` deleted, 10
  `@HiltViewModel`, `WorkManagerInitializer` removed via
  `tools:node="remove"` (`app/src/main/AndroidManifest.xml:54-58`), 0 Hilt
  references in `app/src/test`.
- **MVI (ADR-44)** — `TodayEvent` / `CreationEvent` / `ActivitiesEvent` sealed +
  `onEvent` + single `StateFlow` present; `val effect` wired in Habits and
  Activities; the 7 MVVM screens left unconverted; **0** naked `_x.value =`
  writes in any `*ViewModel*.kt`; every effect `Channel(BUFFERED)`;
  `collectAsState()` = **0**, `collectAsStateWithLifecycle` = 26.
- **Strings / typography (E1, E1b)** — literal `Text("…` in `app/src/main` = 0
  outside `phase2b`; raw `.sp` / `fontSize` / `lineHeight` / `letterSpacing`
  outside `presentation/theme/**` = 0 outside `phase2b`;
  `HabitsViewModel` no longer references `recurrenceLabel` (0 hits);
  `recurrenceLabel` is `@Composable` (`ActivityCard.kt:162-181`); U+FFFD across
  every `.kt` in `app/src` = **0 files**. E1b string contracts render correctly
  at runtime (validation error appeared/disappeared correctly on the release
  APK; em dash intact).
- **Compose quality (E2)** — keyless lazy `items` = **0**; `contentType` on all
  **9**; `mergeDescendants` **12**; `liveRegion` 1 site
  (`HabitConfigScreen.kt:589`); actionable targets ≥48dp through
  `MomentumHeights` (`button` 52, `controlMin` 48, `iconTarget` 48);
  **no hardcoded status-bar dp** — every inset is `WindowInsets`-derived;
  `@Immutable` **11**, `@Stable` **0**; `enableEdgeToEdge()` in both
  `onCreate` and `onConfigurationChanged`; `rememberReduceMotion` at 28 sites.
  No visual redesign: 0 size/spacing/colour/animation values changed.
- **Release / perf (F)** — `isMinifyEnabled = true` +
  `isShrinkResources = true`; `app/proguard-rules.pro` holds exactly the two
  Nav3 `Routes` keep rules and each is **justified in-file against the shipped
  consumer rules**; release `mapping.txt` confirms all 12 `Routes$*` + their
  `$Companion` / `$$serializer` are **unrenamed** and present in `seeds.txt`,
  `MomentumDatabase_Impl`, `MainActivity`, `MomentumApp`, `FireReminderWorker`,
  `RescheduleWorker` all unrenamed; 47 phase2b classes removed in release
  `usage.txt`; no `INTERNET` permission in the merged manifest.
- **Room / schemas** — `git status app/schemas` clean; `copyRoomSchemas`
  NO-SOURCE; 0 `fallbackToDestructiveMigration`; 0 `identityHash` edits; Room
  2.7.1 compiles and (runtime) opens + writes.
- **Scope guard** — `git status --porcelain`: every
  `presentation/exploration/phase2b/**` entry is `A ` (staged add, **no**
  unstaged modification); `AGENTS.md` and `opencode.json` are staged-only, from
  the pre-existing multi-agent task; `git log bd1bafb..HEAD` is empty — nothing
  committed.

### Runtime — VERIFIED

Booted the `Pixel_7` AVD (API 35, 1080×2400) for this pass and restored it to
its original state (uninstalled + `emu kill`) afterwards. Nothing in the
workspace was written.

**Debug APK (`app-debug.apk`, installed and driven):**
- Hilt + Room + WorkManager + ProfileInstaller startup — clean, no
  `FATAL EXCEPTION` in logcat across the whole session.
- Onboarding ×4 pages → `Let's Get Started` → Today with the four ADR-22 tabs.
- Tab switch **and re-select** for Home / Habits / Tasks / Insights: correct
  destination, re-select is a no-op (`selectTab`, `MomentumNavGraph.kt:118-122`).
- `+` → Create Item sheet → Habit → intent/template config; back from config
  lands on Today.
- Habit created (Room write) → auto-open Habit detail; back → Today; Today row
  → detail again; `Edit Habit` → `Edit habit` config (ID-only `Routes.Edit`
  arg loads the row) → back → detail; `Pause` persisted and re-rendered.
- E1b metadata row renders: `Daily · 30 pages · —` on Habit detail;
  Today Hero greeting band correct for the local clock (`Good night` at 01:20).
- **Cold deep-open** from a reminder-style intent
  (`am start … --el open_activity_id 1`, after `force-stop`) opened the
  `Detail(activityId)` entry — `MomentumNavGraph.kt:384-389` +
  `MainActivity.kt:61`.
- **Nav3 back-stack restore across process death** (`always_finish_activities
  = 1`, home, relaunch): the `Detail` entry was restored, with **0**
  `SerializationException` / `ClassNotFoundException` in logcat — this is the
  `NavKeySerializer` → `Class.forName(FQCN).kotlin.serializer()` path running
  live.
- Creation validation: empty title showed the inline error; it cleared once a
  title was entered (E1b `UiText` contract).

**Release APK (`app-release-unsigned.apk`, R8 full mode + resource shrink):**
signed with the local debug keystore **into a temp copy** (no build file or
workspace file touched; the copy was deleted afterwards) purely to exercise the
R8-only failure modes that the Phase F handoff flagged:
- Cold start of the minified build: Hilt graph built, Room DB created and read,
  WorkManager initialised, UI rendered. **0** `ClassNotFoundException`,
  `ClassCastException`, `DaggerHilt` or `Unable to instantiate` errors — this
  is the concrete proof for the Phase F handoff note that "R8 bugs only appear
  in release builds".
- Full onboarding, tab switching, create-task flow, and inline validation error
  all work under minification.
- **Nav3 `CreateTask` entry restored across process death on the minified
  APK** with 0 serialization errors — this is the runtime proof that the two
  `Routes` keep rules in `app/proguard-rules.pro` are correct and sufficient.
- Non-ASCII copy renders correctly (U+2014 intact).

**Runtime still UNVERIFIED (and why):** findings 2–5 above — the material3 1.4.0
before/after visual watch (no pre-migration artifact exists to compare against),
TalkBack announcement of the 8 newly merged rows, the predictive-back gesture
itself, and `IconChoice`'s `onClickLabel` announcement. No release *signing*
verification (Human-in-the-Loop, out of scope) and no baseline profile in the
APK (documented, out of scope).

---

## 11. HANDOFF HISTORY

<!-- ORCHESTRATOR ONLY -->

- Orchestrator: task established from the human brief + TASTE_MODERNIZATION_TARGET.md;
  baseline recorded; 4 critical conflicts identified for Architect resolution.
- Orchestrator → Architect: decisions + phased plan brief (KSP pin, AGP 9
  prerequisites, Nav3 artifacts, per-ViewModel MVI/MVVM classification, Hilt
  adoption). Delivered: **ADR-41…ADR-44**, §8.1–§8.6, 4 conflicts resolved with
  live version evidence.
- Orchestrator → Developer (Phase A, toolchain): 3 gates green (42/38 executed;
  41 files / 253 tests / 0 failures). Two build-level judgment calls recorded
  (`android.disallowKotlinSourceSets=false`; Robolectric NATIVE sqlite). The
  NATIVE mode exposed the pre-existing migration-fixture gap → **ISSUE-028**.
- Orchestrator → Developer (Phase B, Hilt): 3 gates green; `AppContainer` +
  `presentation/ViewModels.kt` deleted, 10 `@HiltViewModel`, `@HiltWorker`s,
  manifest WorkManager-initializer removal. Pre-authorized Hilt fallback 2.60.1
  exercised.
- Orchestrator → Developer (Phase C, Navigation 3): 3 gates green; 12
  `@Serializable` NavKeys, single `NavDisplay` graph, `navigation-compose`
  removed, serialization plugin + json 1.8.1. Reported `compileSdk` 35→36 as
  **forced** (nav3 AAR `minCompileSdk=36`) and needed ratification.
- Orchestrator → Reviewer (checkpoint A–C): **PASS WITH FINDINGS**, all
  documentation-level. Runtime UNVERIFIED (no device at that point). Findings:
  compileSdk 36 unratified; Hilt 2.60.1 not in ADR-43; §9 route counts wrong;
  ADR-43 manifest/receiver wording; catalog comment attribution.
- Orchestrator → Architect (ratification): §8.2/§8.4/§8.5 corrected, ADR-42
  consequences (compileSdk 36, retaining saveable-state decorator) and ADR-43
  (Hilt 2.60.1, required manifest change, scoped receiver annotations) amended.
- Orchestrator: opened **ISSUE-028** (migration-fixture gap) in `ISSUES.md`.
- Orchestrator → Developer (Phase D, MVI + Turbine): 3 gates green; Today /
  Creation / Activities converted to the MVI contract, Habits effect wired,
  Turbine 1.2.0 adopted, 2 Reviewer corrections applied. Reported the "mojibake"
  in `TodayScreen.kt`.
- Orchestrator: **verified that mojibake report was a console artifact** (byte
  scan: strict UTF-8 decode clean, all non-ASCII codepoints correct) and passed
  the finding on as not-a-defect; also confirmed the extraction doc's string
  baseline was stale.
- Orchestrator → Developer (Phase E1, localization + typography): production code
  reached 0 literal `Text("`, 0 raw `sp` outside theme, 338 `stringResource`
  uses; 32 strings added. Escalated the ViewModel-copy boundary.
- Orchestrator: **ruled** the escalation in scope and authorized a tightly
  scoped **Phase E1b** (ViewModel string contracts + `recurrenceLabel` layering
  fix) — a `@StringRes`/`UiText` representation change only, no ADR-44 impact.
- Orchestrator → Developer (Phase E1b): 3 gates green; 254 tests (+1); new
  `presentation/UiText.kt`; `HabitsViewModel` no longer calls `recurrenceLabel`.
- Orchestrator → Developer (Phase E2, Compose quality): 3 gates green; 0 keyless
  lazy items, 9 `contentType`, 11 `@Immutable`, 12 `mergeDescendants`, 1
  `liveRegion`, 0 a11y/48dp/edge-to-edge violations, 1 recomposition fix.
- Orchestrator → Developer (Phase F, release/perf): 4 gates green including
  `:app:assembleRelease` with R8 (4.04 MB vs 22.77 MB debug); 2 `Routes` keep
  rules (verified necessary + sufficient), profileinstaller 1.4.1, no signing
  config invented.
- Orchestrator → Reviewer (final, whole modernization): **PASS WITH FINDINGS**,
  all 4 Gradle gates re-executed independently, **runtime VERIFIED** on a
  Pixel_7 AVD (debug + minified release, `connectedDebugAndroidTest` 7/7,
  NavKey restore across process death on both APKs). One new pre-existing
  defect (onboarding re-show) + 4 notes, **no Developer correction required**.
- Orchestrator: opened **ISSUE-029** (onboarding start-destination gate) and
  carried the 4 notes as the deferred-runtime register; task closed.

---

## 12. FINAL OUTCOME

<!-- ORCHESTRATOR ONLY -->

**MODERNIZATION COMPLETE AND VERIFIED.** All six planned phases (plus the
Orchestrator-authorized E1b) are implemented, all four Gradle gates execute
green, and the final Reviewer verified the whole modernization on a real device
emulator. Nothing committed or pushed.

### Final toolchain (§8.2, all re-derived by the Reviewer)

| Component | Final | Note |
|---|---|---|
| AGP | 9.0.0 | built-in Kotlin; `org.jetbrains.kotlin.android` + `kotlinOptions{}` removed |
| Gradle wrapper | 9.1.0 | AGP 9 minimum |
| Kotlin | 2.2.10 | K2; skill's 2.1.20 superseded by AGP 9's floor (ADR-41) |
| KSP | 2.2.10-2.0.2 | KSP2-only; both skill pins superseded (ADR-41) |
| Compose BOM | 2026.01.01 | ui 1.10.2, material3 1.4.0; forced by nav3 (ADR-42) |
| Navigation | nav3-runtime/ui 1.1.0 | `navigation-compose` removed |
| lifecycle-viewmodel-navigation3 | 2.10.0 | |
| Lifecycle | 2.10.0 | nav3 hard dependency |
| kotlinx-serialization-json | 1.8.1 | + serialization plugin 2.2.10 |
| Room | 2.7.1 | schemas byte-identical |
| Coroutines | 1.10.2 | |
| DataStore | 1.1.7 | |
| Hilt | 2.60.1 (+ androidx.hilt 1.2.0) | 2.57.2 fails under AGP 9; fallback pre-authorized (ADR-43) |
| Turbine | 1.2.0 | test-only |
| profileinstaller | 1.4.1 | |
| activity-compose | 1.12.0 | forcing path re-verified by the Developer: nav3-ui + lifecycle-viewmodel-navigation3 POM/module metadata (the §8.2 row's BOM/material3 attribution is superseded by that evidence; version itself unchanged) |
| SDK | compileSdk 36 / targetSdk 35 / minSdk 26 / jvmTarget 17 | compileSdk 36 forced by nav3 AAR `minCompileSdk` |

### Architecture

Navigation 3 (`NavDisplay` + `entryProvider`, 12 `@Serializable` NavKeys with
ID-only args, 0 string routes, tab/back/deep-link behavior preserved, restore
across process death proven on debug **and** minified release); Hilt DI
(`@HiltAndroidApp`, `@HiltViewModel` ×10, `di/` modules, `@HiltWorker`,
`AppContainer` deleted); MVI per ADR-44 for Today/Creation/Activities, MVVM
retained for the other seven; all user-visible copy resource-driven (new
`UiText` contract); R8 + resource shrink with two `Routes` keep rules.

### Verification (final Reviewer, all executed)

`assembleDebug` ✅ · `testDebugUnitTest` ✅ **41 files / 254 tests / 0 failures /
0 errors / 0 skipped** · `compileDebugAndroidTestKotlin` ✅ · `assembleRelease` ✅
(R8, 4.04 MB vs 22.77 MB debug, 0 R8 warnings) · `connectedDebugAndroidTest`
✅ **7/7 on Pixel_7 (API 35)** · runtime smoke of tabs, create flow, detail/edit,
deep-open, and release-APK startup ✅.

### Deferred (recorded, not fixed — out of modernization scope)

- **ISSUE-029** (High) — onboarding re-shows on cold start (pre-existing; not a
  regression; `HEAD` carries the identical construct).
- **ISSUE-028** (Low) — migration-test v1 DDL fixture gap, masked by a
  package-scoped Robolectric LEGACY override (test-source fix).
- material3 1.4.0 before/after visual watch (no pre-migration artifact exists);
  TalkBack audit of the 8 newly merged rows (structurally verified only);
  predictive-back gesture; `IconChoice` `onClickLabel` announcement.
- Release **signing** and a real baseline profile remain Human-in-the-Loop /
  out of scope.

---

## 13. TASK-012 - BASELINE PROFILE GENERATION (VERIFIED)

<!-- ORCHESTRATOR ONLY -->

**COMPLETE AND VERIFIED.** A **real** baseline profile now exists and is
packaged in the release APK. This supersedes the TASK-011 note above that a
"real baseline profile remains Human-in-the-Loop / out of scope".

### Starting state (verified, not assumed)
Before this task: **no** `app/src/main/baseline-prof.txt`, no `:baselineprofile`
module, no `androidx.baselineprofile` plugin, **0** `BaselineProfile` sources.
`profileinstaller 1.4.1` was present as a *runtime only*. The R8
`baseline-prof*.txt` files under `app/build/intermediates/**` are ART-profile
intermediates, **not** an AndroidX baseline profile.

### Delivered
- `gradle/libs.versions.toml` - androidx-baselineprofile/benchmark 1.5.0,
  uiautomator, test-ext-junit entries (ADR-45).
- `settings.gradle.kts` - `include(":baselineprofile")`.
- `baselineprofile/` module - `com.android.test` (applied version-less as
  `id("com.android.test")`, ADR-41 built-in Kotlin) + `androidx.baselineprofile`;
  `BaselineProfileGenerator.kt` covering onboarding skip -> Home -> Habits ->
  Tasks -> Insights -> Home, plus a cold-start pass.
- `app/build.gradle.kts` - `androidx.baselineprofile` plugin +
  `baselineProfile { saveInSrc = true; mergeIntoMain = true;
  automaticGenerationDuringBuild = false }` + `baselineProfile(project(...))`.
- **`app/src/main/generated/baselineProfiles/baseline-prof.txt`** - generated,
  2,486,193 B, **23,427 rules** (1,649 referencing `com/momentum/app`).

### Verification (final Reviewer, independent)
| Claim | Evidence |
|---|---|
| Generation ran | `:baselineprofile:collectNonMinifiedReleaseBaselineProfile` BUILD SUCCESSFUL (4m36s), 2 tests on Pixel_7, 0 failed |
| Profile is real | 23,427 rule lines, 0 blanks/comments; class + member rules; MomentumNavGraphKt 193, ActivitiesScreenKt 103, TodayScreenKt 52, HabitsScreenKt 37, InsightsScreenKt 22 - not a thin/onboarding-only profile |
| Release build | `:app:assembleRelease` BUILD SUCCESSFUL (52 tasks, 43 executed); `app-release-unsigned.apk` 4,237,684 B; **still unsigned** (no `signingConfigs`, no keystore) |
| Packaged in APK | `assets/dexopt/baseline.prof` 6,231 B (stored) + `assets/dexopt/baseline.profm` 805 B (stored) |
| Byte identity | packaged SHA-256 **byte-identical** to AGP `binary_art_profile/release/.../baseline.prof`; `MainActivity` + 228 `com.momentum.app` classes seeded at the R8 stage (obfuscated-but-seeded proof) |
| Scope | only the generated profile + the generator source changed; no commits; `app/schemas` clean; no `INTERNET` |

Two generator defects were found and fixed during collection (not worked
around): `startActivityAndWait` is a `MacrobenchmarkScope` member (not an
import), and the app's `POST_NOTIFICATIONS` prompt on every cold start blocked
`IsolationActivity` launch - resolved deterministically with `pm grant` via
`UiDevice.executeShellCommand`, not an unverifiable click.

### Open notes (Reviewer findings 1-3, non-blocking)
1. **No startup profile.** Neither `collect()` block sets
   `includeInStartupProfile = true`, so `baseline.profm` is present but not
   built from AOT startup rules. Matches the approved 8.7.5 sample verbatim;
   closing it needs a booted device + re-generation - **deferred by decision**.
2. `app/build.gradle.kts:80` comment says the profile lands at
   `app/src/main/baseline-prof.txt`; the plugin actually writes
   `app/src/main/generated/baselineProfiles/baseline-prof.txt`. Comment-only
   inaccuracy; packaging is proven correct.
3. `benchmark-macro-junit4:1.5.0` actually resolves `uiautomator` to 2.4.0,
   not the pinned 2.3.0 - the catalog comment overstates the guarantee (no
   behavioral impact; the classic DSL is used).

### Maintenance obligation
The profile is a **committed generated artifact**. If R8 names or the startup /
navigation journey change, the build stays green while the profile silently
stops matching - regenerate with `:baselineprofile:collectNonMinifiedReleaseBaselineProfile`
on a booted API 33+ device.

---

## 14. TASK-013 - RESOURCE AUDIT (VERIFIED: NOTHING DELETED)

<!-- ORCHESTRATOR ONLY -->

**COMPLETE AND VERIFIED. Bucket A (SAFE TO DELETE) is EMPTY. Zero resources
deleted.** The 17-file resource set is already minimal; every entry is used,
framework-reachable, or preview-only by rule.

### Correction to two Orchestrator premises (Developer + Reviewer confirmed)
1. `resources.txt` is **not** free of unused markers: it contains **211 rows
   marked `is not reachable`**, of which **19** are app-owned strings.
2. The decisive third evidence source is **`aapt2 dump resources` on the shipped
   release APK**, which agrees with the shrinker and resolves a real
   `resources.txt` **duplicate-row artifact** (`activities_title`,
   `cd_mark_as_done`, `cd_mark_as_not_done` each appear twice with contradictory
   reachability). **The `is not reachable` row is authoritative** - all three are
   absent from the release `resources.arsc`; the `reachable from R$string.X` row
   is the misleading side.

### Counts (reconciled by the Reviewer; 358 + 5 + 16 = 379)
| Bucket | Count | Content |
|---|---|---|
| **A - SAFE TO DELETE** | **0** | - |
| **B - USED, KEEP** | **358** | 349 strings (346 release-reachable + 3 compile-time default args) + `Theme.Momentum` + 7 drawables + `manrope` |
| **C - INDIRECT / FRAMEWORK, KEEP** | **5** | `ic_launcher_background`, `ic_launcher_foreground`, `ic_launcher_monochrome`, `ic_launcher`, `ic_launcher_round` |
| **D - PREVIEW ONLY, KEEP** | **16** | all `ds_*`, referenced only from `DesignSystemPreviews.kt` |
| **E - UNCERTAIN** | **0** | 0 `getIdentifier` / dynamic lookups; no `values-night`, `raw`, `xml`, `navigation`, `menu`, `assets` |

**Files: 17 before / 0 deleted / 17 retained. Resource names: 379 before /
0 deleted / 379 retained.**

### The 19 flagged strings - adjudication
- `cd_mark_as_done`, `cd_mark_as_not_done` (`CompletionButton.kt:60-64`) and
  `activities_title` (`ActivitiesScreen.kt:62`) are Kotlin **default parameter
  values** that every call site overrides; R8 strips them from release, but they
  remain compile-time references, so deleting them would break compilation.
  **Boundary nuance:** the `cd_mark_as_*` default is only ever *evaluated* by a
  `@Preview` (`CompletionButton.kt:138-144`) - a B/D boundary case; the conclusion
  (not deletable) is unaffected.
- 16 `ds_*`: every reference is inside `DesignSystemPreviews.kt`, which contains
  only `@Preview` functions. **Bucket D** per the previewability rule
  (AGENTS.md §5); stripped from release by the shrinker regardless.
- `locked_build_summary` / `locked_reduce_limit_summary` /
  `locked_reduce_zero_summary` are **production** (`HabitConfigScreen.kt:495-500`
  via `LockedIntentSummary`, called at `:190`) and present in the release arsc.
  Not dead, despite the name.

### Verification (Reviewer, independently)
`assembleDebug` UP-TO-DATE (42/42 - itself evidence nothing changed) ·
`testDebugUnitTest --rerun-tasks` **EXECUTED** - **41 files / 254 tests /
0 failures / 0 errors / 0 skipped** · `assembleRelease --rerun-tasks`
**EXECUTED** - R8 + `optimizeReleaseResources` both ran, shrinkResources green ·
`aapt2 dump resources` - 346/365 app strings present, the 19 absent are exactly
the 19 flagged · APK 4,237,684 B, byte-identical, still unsigned, 0
`META-INF/*.RSA|DSA|EC|SF`.

### APK size (no benefit claimed)
Release has been minified since TASK-011 Phase F (`isMinifyEnabled = true` +
`isShrinkResources = true`, ADR-45), which is also why the APK is dominated by
the untouched large assets rather than by dead code.

| | Bytes |
|---|---|---|
| Before | 4,237,684 |
| After | 4,237,684 |
| **Difference** | **0** |

SHA-256 identical. **No installed-app-size benefit is claimed** - nothing was
deleted, and the artifacts demonstrate no change. The APK is dominated by the 4
onboarding WebP images (1,519,994 B), 2 hero WebPs (372,250 B) and
`manrope.ttf` (164,700 B), all bucket B and genuinely used. Reducing those is a
product/design decision, not a resource cleanup.

### Routed forward (not actioned here)
- `activities_title` ("Activities") is **dead copy** after ADR-22 moved the Tasks
  tab onto the activities surface: reachable only as a never-invoked default
  parameter. Retiring it means changing a Kotlin default and touching ADR-22 tab
  naming - a **product copy decision**, not a bucket-A deletion.
- Root `RESOURCE_CLEANUP_REPORT.md` could not be written: no agent holds an edit
  grant for that root path (same class as `ISSUE-023`).
