# CURRENT TASK

> Active task state only. TASK-015/016/017/018 records are archived in
> `task_archive/`; their approved decisions remain canonical, append-only, in
> `docs/architecture/decisions/DECISIONS.md` (ADR-47/48/49 + Amendment Notes).

---

## 1. TASK IDENTITY

<!-- ORCHESTRATOR ONLY -->

- Task ID: **TASK-019**
- Title: **Home top-chrome vertical position refinement** — bring the fixed Home
  top chrome up to a tight 4–6 dp clearance below the system status-bar region
- Status: **COMPLETE / ACCEPTED (2026-10-02).** Independent Reviewer verdict:
  **PASS**. The 4 dp semantic top clearance is **ACCEPTED AND FROZEN** — treat
  it as settled and **do not revisit it** unless a concrete future device or
  layout issue (e.g. a real status-bar/cutout overlap on other hardware) is
  demonstrated with evidence. It was reached deliberately in two steps, not by
  a single guess, and both steps are recorded below.
- Priority: Medium
- Created: 2026-10-02
- Last Updated: 2026-10-02
- Classification: **Spacing refinement** on the fixed Home top chrome. Not a
  redesign; no behaviour, architecture, contrast or background change.
- Routing: **Orchestrator → Developer → Reviewer**
- Supersedes: nothing. TASK-018 is **CLOSED** (archived to
  `task_archive/TASK-018.md`) and stands in full.

## 2. OBJECTIVE

<!-- ORCHESTRATOR ONLY -->

Move the entire Home top-chrome composition **upward as one unit** so it sits
immediately below the system status-bar region with a small, intentional
breathing space, instead of floating in the Aurora field.

Affected composition (must move together): **date capsule · Settings icon ·
greeting · greeting support line**.

## 3. ROOT CAUSE

<!-- ORCHESTRATOR ONLY -->

### Pass 1 — resolved, must NOT be undone

`HomeChrome.kt:110-111` stacked the status-bar inset **plus** a full
`MomentumSpacing.section` (24 dp) above the date capsule, so the chrome sat
`statusBarInset + 24 dp` from the top edge. `section` is documented in
`Tokens.kt:27` as *"24dp — gaps between major sections"* — the wrong semantic
below the system status region.

Pass 1 replaced that token with `MomentumSpacing.compact` (12 dp). That
correction was architecturally right and is **retained**; it removed the
double-counted section-scale gap. Measured result: a **32 px ≈ 12.2 dp** gap
(status-bar inset 136 px, capsule top 168 px), which the human judges still too
low.

### Pass 2 — the governing remaining value

The excess is now `MomentumSpacing.compact` = 12 dp, which exceeds the requested
**4–6 dp** clearance. `Tokens.kt` semantic review:

- `micro` = 4 dp — *"hairline-adjacent nudges inside compound controls"*
- `tight` = 8 dp — *"gaps inside compound controls (chips, rows)"*

`micro` carries the only in-range value, but its documented meaning is unrelated
to status-bar clearance, and inlining it would abuse an unrelated token. The
correct move is a **named semantic alias bound to the existing token**, exactly
mirroring the `ChromeBottomInset` precedent already established in the same file:

```kotlin
internal val ChromeTopInset = MomentumSpacing.micro   // 4 dp
```

This yields a **4 dp** clearance — inside the requested 4–6 dp band — introduces
**no new token and no dp literal**, and gives the value the semantic name the
composition actually means.

## 4. SCOPE

<!-- ORCHESTRATOR ONLY -->

**In scope:** the single governing spacer value in
`app/src/main/java/com/momentum/app/presentation/screens/today/HomeChrome.kt`
and its KDoc.

**Out of scope / must NOT change:** Aurora artwork or background; anything
touching `ArtworkIntrinsicRatio`, `ContentScale.Fit`, the artwork window, the
scrim, or background layering; the scroll architecture; the progress card; the
FAB; bottom navigation; Liquid Glass tokens; Habits / Tasks / Insights /
Settings; domain / data / repository / ViewModel; dependencies; build files.

## 5. REQUIREMENTS

<!-- ORCHESTRATOR ONLY -->

1. Reduce the top spacing substantially — target **≈4–6 dp** of visual
   clearance between the bottom of the status-bar/cutout region and the top of
   the date capsule, subject to the existing edge-to-edge inset. Not zero; the
   date must not overlap the status-bar icons or cutout.
2. Fix the **governing** layout/inset/padding value. Do **not** add compensating
   offsets elsewhere.
3. Forbidden: hard-coded negative offsets · `translationY`/offset hacks ·
   duplicated inset calculations · screen-specific hacks · scroll-position logic
   · artwork-position adjustments · new spacing tokens · dp literals in
   `HomeChrome.kt` (pinned by `HomeAtmosphereAssetsTest`).
4. Date capsule, greeting, support line and Settings move **together** as one
   composition.
5. Preserve the existing spacing **between** Date → Greeting, Greeting →
   Subtitle, and Date → Settings.
6. Preserve `ChromeBottomInset = MomentumSpacing.section` (the *bottom* gap,
   pinned by `HomeAtmosphereAssetsTest`).
7. Must **not** overlap the system status-bar icons or cutout.
8. Light **and** Dark behaviour preserved; accessibility and edge-to-edge
   behaviour preserved.
9. If an existing spacing token does not carry the intended semantics, introduce
   the **smallest appropriately named semantic alias** bound to that token rather
   than abusing an unrelated token inline.

## 6. RELEVANT FILES

<!-- ARCHITECT OWNER -->

- `app/src/main/java/com/momentum/app/presentation/screens/today/HomeChrome.kt` —
  **the only production file to change** (the leading spacer, now at :110-117,
  bound to the `ChromeTopInset` alias).
- `app/src/main/java/com/momentum/app/presentation/theme/Tokens.kt` —
  read-only (`compact` = 12 dp, `section` = 24 dp).
- `app/src/test/java/com/momentum/app/presentation/screens/today/HomeAtmosphereAssetsTest.kt`
  — read-only; pins `ChromeBottomInset` and "no `.dp` literal in the chrome".
- `app/src/androidTest/java/com/momentum/app/presentation/screens/today/HomeCompositionTest.kt`
  — read-only; bounds assertions for the fixed chrome.

## 7. ENVIRONMENT FACTS

<!-- ORCHESTRATOR ONLY -->

- `adb` **is** available at
  `C:\Users\shaik\AppData\Local\Android\Sdk\platform-tools\adb.exe`;
  emulator `emulator-5554` (sdk_gphone64_x86_64, 1080×2400 @420 dpi ≈ 412 dp).
- The app's theme mode is **System**, so Light/Dark are toggled with
  `adb shell cmd uimode night no|yes`.
- Build: `:app:assembleDebug` → `app/build/outputs/apk/debug/app-debug.apk`;
  install with `adb install -r`.
- **Screenshots must be captured on-device then pulled**
  (`adb shell screencap -p /sdcard/x.png` + `adb pull`). `adb exec-out … > file`
  corrupts the PNG under PowerShell.
- Validation must use the mandated `gradle-run` wrapper
  (`scripts/gradle_run.py`); never invoke `./gradlew` directly. Python 3.14
  available. Pass `gradlew.bat` as the command after `--`.

## 8. ARCHITECTURAL DECISIONS AND PLAN

<!-- ARCHITECT OWNER -->

No new ADR is required. ADR-47 Amendment Note 5 §3 governs this composition
(no plate, no divider, one continuous scrim, one axis/ink/type scale) and is
unaffected by a spacing value; AN5 §4's scroll-boundary contract is likewise
unaffected. The Developer records the reason in the existing `HomeChrome.kt`
KDoc. If the Reviewer finds this genuinely conflicts with a recorded clause,
it is escalated rather than absorbed here.

---

## 9. IMPLEMENTATION STATUS

<!-- DEVELOPER OWNER -->

**RESULT — implemented and validated; spacing-only.**

Single production file changed:
`app/src/main/java/com/momentum/app/presentation/screens/today/HomeChrome.kt`.
The spacer above the date capsule changed `MomentumSpacing.section` → a new
named semantic alias `ChromeTopInset = MomentumSpacing.micro` (4 dp), placed
beside the existing `ChromeBottomInset` precedent. No new token, no dp literal,
no offset/offset-hack, no other file touched.

**Validation (mandated `gradle-run` wrapper, Python 3.14.8):**
- `:app:testDebugUnitTest --rerun-tasks` → exit **0**, **293 tests / 42 suites /
  0 failures / 0 errors / 0 skipped**.
- `:app:assembleDebug` → exit **0**; installed on `emulator-5554`.
- Existing pins still hold: `ChromeBottomInset = MomentumSpacing.section` and
  "no `.dp` literal in the chrome".

**Device measurement** (live `statusBars` / `displayCutout` frame = 136 px =
51.8 dp @ 2.625 px/dp):

| Mode | Status-bar bottom | Capsule top | Gap |
|---|---|---|---|
| Light | 136 px | 147 px | **11 px ≈ 4.19 dp** |
| Dark | 136 px | 147 px | **11 px ≈ 4.19 dp** |

Inside the requested 4–6 dp band; **11 px of clearance — no overlap** with the
status-bar icons or cutout in either theme.

**Internal chrome spacing unchanged** (`uiautomator` bounds, identical in Light
and Dark and identical to pass 1): Date→Greeting **22 px**, Greeting→Subtitle
**11 px**, and capsule top y=147 = Settings top y=147 — the four elements moved
together as one composition.

**Not disturbed:** Aurora artwork and background (no change to
`HomeAtmosphere.kt`, `ArtworkIntrinsicRatio`, `ContentScale`, artwork window,
scrim or layering); scroll architecture; progress card; FAB; bottom navigation;
Liquid Glass tokens; Habits / Tasks / Insights / Settings. On swipe, every chrome
row (0–444 px) was **bit-identical** (0 changed pixels) while content below
scrolled. Only pre-existing warnings appeared; nothing committed.

**Note (not a defect):** the emulator carried no app data, so onboarding and a
notification-permission prompt blocked the first captures; the Developer granted
`POST_NOTIFICATIONS` and created five throwaway habits to make the list overflow
for the scroll-stability measurement. Emulator-local runtime data only — no
production code or resource involved.

## 10. REVIEW FINDINGS AND VALIDATION

<!-- REVIEWER OWNER -->

**VERDICT: PASS (independent Reviewer pass, 2026-10-02).** No files modified by
the Reviewer. All twelve review objectives PASS:

1. `ChromeTopInset` is `internal`, declared in `HomeChrome.kt`, used only there
   → Home-only, no leakage.
2. `MomentumSpacing.micro == 4.dp` (`Tokens.kt:13`) is the smallest 4 dp-grid
   step; bound via named alias (not inlined) → semantically defensible, no token
   abuse, `Tokens.kt` untouched.
3. No negative offsets, `translationY`/offset hacks, duplicated inset handling,
   or `.dp` literals; the single status-bar inset read is unchanged.
4. Inset spacer present; date/Settings clear the status bar/cutout by
   ≈11.5 px (≈4.4 dp), no overlap, both themes.
5. Date, Settings, Greeting, Subtitle sit in one composition (capsule top
   ≈147.5, greeting 220, support 331, Settings centred on the row) — top-aligned
   and moved together.
6. Internal spacing unchanged: Date→Greeting and Greeting→Subtitle = the 4 dp
   column gap; `ChromeBottomInset = MomentumSpacing.section` byte-identical.
7. Chrome fixed on scroll — exhaustive per-pixel row diff: rows 0–443 identical,
   first change at y=444 (chrome bottom edge).
8. Aurora artwork/background untouched (`HomeAtmosphere.kt`,
   `MomentumHeroArtwork.kt`, `Tokens.kt`, resources unchanged).
9. Light/Dark `uiautomator` bounds byte-identical.
10. Habits / Tasks / Insights / Settings smoke-checked, no regression.
11. Liquid Glass, progress card, FAB, bottom nav, artwork untouched.
12. Diff scope: one production file (one spacer value + one named alias); no
    test/build/resource/domain change; nothing committed.

**Validation (mandated `gradle-run` wrapper):**
- `:app:testDebugUnitTest --rerun-tasks` → exit **0**, **293 tests / 42 suites /
  0 failures / 0 errors / 0 skipped**.
- `:app:assembleDebug` → exit **0**.
- Only the four pre-existing warnings; none attributable to this change.

**Device verification:** live insets (`statusBars`/`displayCutout` = 136 px);
clearance ≈**11.5 px ≈ 4.4 dp**, no overlap, Light and Dark; chrome-fixed-on-scroll
proven by exhaustive per-pixel diff; cross-screen smoke of Habits/Tasks/Insights/
Settings. Full font-scale matrix and instrumentation runs skipped (a single
spacing constant; `HomeCompositionTest` asserts only relations, all covered by
the unit suite and the pixel diff).

**Non-blocking observations (not defects):** the chrome insets from
`WindowInsets.statusBars` only (not `displayCutout`) — a pre-existing property,
unchanged here; and `task.md` §6's line-reference to the leading spacer was
stale (:110-111 → :110-117) and **has been corrected** — an Orchestrator-owned
cosmetic nit, now closed.

---

## 11. FINAL OUTCOME

<!-- ORCHESTRATOR OWNER -->

**COMPLETE / ACCEPTED — the Home top chrome is anchored to the top edge, and
the 4 dp clearance is FROZEN.**

Two spacing-only passes, both independently verified:

1. Removed the double-counted `MomentumSpacing.section` (24 dp) gap that sat on
   top of the status-bar inset — the original cause of the excessive gap.
2. Tightened the remaining `compact` (12 dp) to `ChromeTopInset =
   MomentumSpacing.micro` (4 dp), a named semantic alias mirroring the existing
   `ChromeBottomInset` precedent.

**Final accepted state:** `HomeChrome.kt` is the only production file changed —
one spacer value bound to one named `internal val ChromeTopInset` (no new token,
no dp literal, no offset hack).

**Verification evidence (Developer + independent Reviewer, verdict PASS):**

| Check | Result |
|---|---|
| Light clearance (live inset 136 px, capsule top 147 px) | ≈11.5 px ≈ **4.4 dp** |
| Dark clearance | ≈**4.4 dp** (byte-identical bounds) |
| Status-bar / cutout overlap | **none** |
| Chrome fixed during scroll | **0** differing pixels above y=444 |
| Date / Settings / Greeting / Subtitle | move as **one** composition, top-aligned |
| Internal chrome spacing | **unchanged** (Date→Greeting, Greeting→Subtitle, `ChromeBottomInset`) |
| Aurora artwork / background | **unchanged** (`HomeAtmosphere.kt`, `MomentumHeroArtwork.kt`, scrim, layering) |
| Habits / Tasks / Insights / Settings | **unaffected** |
| Liquid Glass / progress card / FAB / bottom nav | **untouched** |
| Unit tests | **293 tests / 42 suites / 0 failures / 0 errors / 0 skipped** |
| Debug build | **success** |
| Production files changed | **only `HomeChrome.kt`** |

**Freeze:** the 4 dp semantic top clearance is **accepted and frozen**. Do not
reopen or re-tune it without a concrete, evidenced device/layout issue. The
established Aurora/background architecture is untouched and remains the accepted
baseline.

Nothing committed — commit/push remain Human-in-the-Loop.

## 12. OPEN ITEMS HANDED TO THE HUMAN

<!-- ORCHESTRATOR OWNER -->

_None._