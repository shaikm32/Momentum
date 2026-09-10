package com.momentum.app

import android.app.Application

/**
 * Phase 0 application class.
 *
 * Manual dependency wiring lives here for now (AppContainer).
 * Do NOT introduce Hilt/Koin without an ADR entry in docs/DECISIONS.md.
 */
class MomentumApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(applicationContext)
    }
}
