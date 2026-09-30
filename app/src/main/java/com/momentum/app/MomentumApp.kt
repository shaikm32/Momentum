package com.momentum.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.momentum.app.data.repository.DefaultCategories
import com.momentum.app.domain.repository.CategoryRepository
import com.momentum.app.notification.ensureReminderChannel
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Application class. Dependency wiring lives in the Hilt graph (ADR-43);
 * the manual service locator (ADR-09) was retired with it.
 * WorkManager workers are created through [HiltWorkerFactory], supplied via
 * [Configuration.Provider] — the default androidx.startup initializer is
 * removed from the manifest so this configuration wins.
 */
@HiltAndroidApp
class MomentumApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var categoryRepository: CategoryRepository

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()
        ensureReminderChannel(this)
        // Seed default categories once (idempotent: only when the table is
        // empty) — the one-shot launch the retired service locator's init used to own.
        appScope.launch {
            runCatching {
                if (categoryRepository.all().isEmpty()) {
                    DefaultCategories.forEach { categoryRepository.upsert(it) }
                }
            }
        }
    }
}
