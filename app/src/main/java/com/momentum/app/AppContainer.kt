package com.momentum.app

import android.content.Context

/**
 * Manual service locator for Phase 0.
 *
 * Room / DataStore / repositories will be wired here in Phase 2.
 * Keep this boring and explicit — no reflection, no DI framework.
 */
class AppContainer(private val context: Context) {
    // Phase 2: val database: MomentumDatabase by lazy { ... }
    // Phase 2: val activityRepository: ActivityRepository by lazy { ... }
    // Phase 10: val themeRepository: ThemeRepository by lazy { ... }
}
