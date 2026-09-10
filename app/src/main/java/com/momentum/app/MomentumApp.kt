package com.momentum.app

import android.app.Application
import com.momentum.app.notification.ensureReminderChannel

/**
 * Application class. Manual dependency wiring lives here (AppContainer).
 * Do NOT introduce Hilt/Koin without an ADR entry in docs/DECISIONS.md.
 */
class MomentumApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(applicationContext)
        ensureReminderChannel(this)
    }
}
