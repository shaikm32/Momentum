package com.momentum.app

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.momentum.app.domain.model.AppSettings
import com.momentum.app.domain.repository.ReminderRepository
import com.momentum.app.domain.repository.SettingsRepository
import com.momentum.app.notification.ReminderScheduler
import com.momentum.app.presentation.navigation.MomentumNavGraph
import com.momentum.app.presentation.theme.MomentumTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* sticky: user can grant in Settings */ }
    // MainScope (coroutines-android, no extra dependency) instead of lifecycleScope,
    // which would need lifecycle-runtime-ktx.
    private val scope: CoroutineScope = MainScope()

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var reminderRepository: ReminderRepository

    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        enableEdgeToEdge()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermissionIfNeeded()
        val openActivityId = intent.getLongExtra(EXTRA_OPEN_ACTIVITY, -1).takeIf { it != -1L }
        setContent {
            val settings by settingsRepository.observe().collectAsStateWithLifecycle(AppSettings())
            MomentumTheme(themeId = settings.themeId, darkMode = settings.darkMode) {
                MomentumNavGraph(
                    onboardingDone = settings.onboardingDone,
                    openActivityId = openActivityId,
                )
            }
        }
        // Re-arm reminders after every launch (cheap, idempotent).
        scope.launch(Dispatchers.IO) {
            runCatching {
                reminderRepository.enabledAll()
                    .groupBy { it.activityId }.keys
                    .forEach { reminderScheduler.scheduleForActivity(it) }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        ) return
        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    companion object {
        const val EXTRA_OPEN_ACTIVITY = "open_activity_id"
    }
}
