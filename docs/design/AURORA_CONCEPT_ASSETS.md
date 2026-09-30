# Aurora Concept Assets — `design/aurora-concept/` (REMOVED)

> **Status: non-runtime, and the asset pack has since been REMOVED.**
> Scope: Phase-2 visual concept exploration input.
> Recorded during Momentum Premium Redesign **Phase 1 closure** (2026-09-28),
> resolving `ISSUE-024`.
>
> **Update (post-ADR-46):** the Phase-2 exploration was abandoned without a
> direction being selected, and `design/aurora-concept/` was **deleted from the
> repository**. The 8 files inventoried in §3 **no longer exist on disk and are
> not tracked in Git**. This document is retained as the historical record of
> what that pack was and why removing it was safe. The pack was **never
> consumed by the application at any point**, so its removal changes **no runtime
> behaviour and no APK content**. **Production Aurora is unaffected**:
> `MomentumAurora.kt`, `MomentumHeroArtwork.kt`, `HomeHero.kt`, `HabitsHero.kt`,
> `Color.kt` and the bundled `habits_hero_light/dark.webp` all remain in place and
> unchanged. Sections below describe the pack **as it was**; read them as
> historical, not present-tense inventory.

---

## 1. Purpose

`design/aurora-concept/` **used to hold** non-runtime visual exploration assets — web
font material used as *input to visual concept exploration*. It is not an
application asset directory, not a `res/` source set, and not part of the build.

## 2. Scope

These assets are in scope **only** for **Phase 2 — Visual Concept
Exploration**, as reference material when evaluating candidate visual
directions. They are out of scope for every implementation phase, and they
carry no authority over the current design system.

## 3. Contents — 8-file inventory (historical; the pack has since been removed)

> The table below records the pack **as it existed** when this document was
> written. **None of these files exists any more.**

| File | Bytes | What it was |
|---|---|---|
| `fonts.css` | 7,805 | **Web CSS artifact** — Google Fonts CSS API v2 output. Declares Manrope at 400/500/600/700 across six subsets, plus Material Symbols Rounded at `100 700`, and a `.material-symbols-rounded` class. |
| `fonts/f1.woff2` | 24,836 | Manrope variable — latin subset |
| `fonts/f2.woff2` | 8,520 | Manrope variable — vietnamese subset |
| `fonts/f3.woff2` | 15,120 | Manrope variable — latin-ext subset |
| `fonts/f4.woff2` | 14,500 | Manrope variable — cyrillic subset |
| `fonts/f5.woff2` | 2,552 | Manrope variable — cyrillic-ext subset |
| `fonts/f6.woff2` | 9,444 | Manrope variable — greek subset |
| `fonts/f7.woff2` | 5,399,668 | **Material Symbols Rounded** variable (98.5% of the pack) |

Pack total: 5,482,445 bytes across 8 files. All seven `.woff2` files are
complete `wOF2` / `00010000` containers whose header length equals their
on-disk length.

*Verification caveat:* the woff2 `name` tables are brotli-compressed and were
not parsed, so `f7`'s identity rests on the CSS declaration plus its
uncompressed size (15,224,444 B), not on an independent read of the font's own
name record.

## 4. Current Android runtime status: **not consumed**

- **Zero references** from the application, tests, documentation, artwork,
  Gradle configuration, version catalog, agent configuration or `.gitignore`.
- `presentation/theme/Type.kt` builds its `FontFamily` solely from
  `Font(R.font.manrope, …)`, resolving to the single bundled
  `app/src/main/res/font/manrope.ttf` (164,700 B, git-tracked).
- No `assets`, `sourceSets` or web-asset pipeline exists in any build file.
- **Not required by the current implementation**, for three independent
  reasons:
  - **Format** — `.woff2` is a web font format and is not a valid Android font
    resource; Android accepts `.ttf` / `.otf` only. These files cannot be
    consumed by Compose as they stand.
  - **Redundancy** — `f1`–`f6` are web-delivered Manrope subsets (19-table
    signature matching the bundled variable TTF) and duplicate font capability
    the app already bundles for offline use.
  - **Non-use** — `f7` is not the icon family the application uses.

## 5. Font status: concept/web assets, **not** Android resources

`fonts.css` is a **web** artifact whose only possible consumer is an HTML/CSS
page. The repository contains **no** HTML files, and this is its only `.css`
file, so no consumer exists today. `f1`–`f6` duplicate Manrope, which the app
already bundles as a variable TTF — that TTF is the **variable font**
(`wght` 200–800; named instances at 300/400/500/600/700/800) recorded by
ADR-21, and it is what the application uses.

## 6. Material Symbols Rounded status: **concept asset only** *(pack removed)*

The current application icon implementation is **unchanged**: Momentum uses
Material **Icons** via `material-icons-extended`
(`app/build.gradle.kts:66`). Material Symbols Rounded existed here only as a
concept reference, and that reference has now been **removed** with the pack.

The application's documentation and this concept pack both named Material
**Symbols** Rounded while the build uses Material **Icons**. That disagreement
was **intentionally left unresolved** as a **Phase-2 decision** — recorded as
open question Q6 in `MOMENTUM_PREMIUM_DESIGN_AUDIT.md` §23, and still unresolved.
Removing the pack **does not settle it**; it only removes the pack's evidence,
so Q6 now stands on the documentation record alone. Nothing in this document
resolves it.

## 7. ⚠️ Warning — runtime adoption requires a separate decision

**These assets must not be treated as application or runtime assets.**

Moving any part of `design/aurora-concept/` into the Android runtime —
bundling a font from it, switching the icon family, or wiring `fonts.css` into
any build — requires a **separate design and implementation decision**,
recorded in `docs/architecture/decisions/DECISIONS.md` by the Architect (ADRs
are Architect-owned; the Orchestrator may not author them).

Concretely, any such adoption must first resolve:

1. Which icon family Momentum uses, and the functional consequence — a static
   vector set has no weight axis, so the concept's variable-weight idea is
   unavailable to the app as it stands.
2. Whether a font change is wanted at all, given Manrope is already bundled
   and offline.
3. Offline-only compliance (`AGENTS.md` §2) and the version-catalog rule (§11)
   if any new dependency is implied.
4. The test and preview impact, since the current type scale, previews and
   presentation tests are built around the existing font registration.

No such decision has been made. This document records status only.

## 8. Provenance — and its limits

**What repository-only evidence establishes:** the files were **introduced as
local design exploration assets** outside the agent workflow. All eight were
written on **2026-09-28** within a 9-second window, after the last commit
(`bd1bafb`, 2026-09-26). No agent in this repository can write `design/**` —
all four agents hold `edit: *: deny` with narrow grants — so they were not
produced by the multi-agent system. The directory is untracked and
un-`gitignore`d.

**What is NOT established, and is recorded as unknown rather than inferred:**

- the original author's intent,
- the exact upstream font version,
- the source URL,
- the acquisition route.

**Corroboration, not proof:** the structure of `fonts.css` is *consistent with*
Google Fonts CSS API v2 delivery, and ADR-21 records the already-bundled
Manrope as "OFL-1.1 from google/fonts". That is consistent with the same
upstream family, but it is **not** proof of the origin of these particular
files.

**Retention is a forward-looking decision.** Keeping this directory is a
decision to retain it as Phase-2 concept input going forward. It is **not** a
claim about what the directory originally was.

## 9. Version-control caution

This directory is **untracked and not gitignored** — no `.gitignore` rule
covers `design/`, `woff2` or `ttf`.

**Do not run a blanket `git add -A` while these assets remain untracked.** It
would commit 5.23 MB of binaries with no documentation — the exact condition
`ISSUE-024` was opened for. Either:

- add the assets **together with** this document, or
- add a `.gitignore` rule for the directory.

Contrast with `artwork/onboarding/`, which is git-tracked and whose provenance
is recorded in the ADR chain — that is the convention this directory is being
brought into line with.

## 10. No ADR was created

This record documents the status of an existing, non-runtime asset directory.
It takes **no architectural decision** and changes no architecture, so the
repository's ADR criteria (`AGENTS.md` §14.15) are not met. No ADR number has
been invented.

## 11. Related records

| Record | Location |
|---|---|
| The audit that surfaced this directory (correction F-9) | `MOMENTUM_PREMIUM_DESIGN_AUDIT.md` — §4.2, §5.1, §23 Q6, Appendix E, Appendix F |
| Issue lifecycle and full investigation evidence | `ISSUES.md` — `ISSUE-024` (**RESOLVED**) |
| Phase-1 closure record | `task.md` — §15 |
| The font actually used by the application | `docs/architecture/decisions/DECISIONS.md` — ADR-21 |
