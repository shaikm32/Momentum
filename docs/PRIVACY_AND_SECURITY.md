# Momentum — Privacy & Security

## Privacy story (user-facing)

**Your data stays on your device.** No account, no login, no backend, no cloud
sync, no analytics SDKs, no ads, no tracking. Stated in onboarding, Settings →
About, and the README.

## Enforcement

- **No network**: `AndroidManifest.xml` must never gain `INTERNET`. No
  Retrofit/OkHttp/Firebase/Supabase/analytics SDKs (banned without ADR).
  Dependencies are justified per `AGENTS.md` §11.
- **Minimal permissions**: `POST_NOTIFICATIONS`, `RECEIVE_BOOT_COMPLETED`,
  `SCHEDULE_EXACT_ALARM` only — each tied to the implemented reminder feature.
  Never location, contacts, mic, camera, storage, or identifiers.
- **Local-only storage**: Room (`momentum.db`) + DataStore Preferences.
  `allowBackup=true` uses the platform backup mechanism; no app-side exfiltration.
- **No secrets**: nothing to hardcode — no API keys, tokens, or credentials exist
  in this project. Never commit keystores, `google-services.json`, or
  `local.properties` (see `.gitignore`).

## Data safety

- Completion history is append-style truth: edits never rewrite past records;
  delete flows are explicit and confirmed (archive = soft-delete default).
- `clearAllData()` cancels alarms before deleting rows.
- Room `exportSchema = true`; schema JSON in `app/schemas/` is committed;
  pre-1.0 destructive migration is a conscious per-change choice, post-1.0 all
  changes ship real `Migration`s.
- User-facing errors are human-readable; stack traces never reach the UI.
- Input validation at the domain boundary (`Activity` init requires title
  ≤120 chars, priority 0–3, target > 0, end ≥ start); storage codec degrades
  unknown/malformed rules instead of crashing.
