package com.momentum.app.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.momentum.app.domain.model.AppSettings
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.domain.model.WeekStart
import com.momentum.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.settingsStore by preferencesDataStore(name = "momentum_settings")

private object Keys {
    val ThemeId = stringPreferencesKey("theme_id")
    val DarkMode = stringPreferencesKey("dark_mode")
    val WeekStart = stringPreferencesKey("week_start")
    val OnboardingDone = booleanPreferencesKey("onboarding_done")
    val ReduceMotion = stringPreferencesKey("reduce_motion") // "null" | "true" | "false"
    val Motivational = booleanPreferencesKey("motivational")
}

/** DataStore (Preferences) implementation — small async key-value settings, no blocking reads. */
class DataStoreSettingsRepository(private val context: Context) : SettingsRepository {

    override fun observe(): Flow<AppSettings> =
        context.settingsStore.data.map { it.toSettings() }

    override suspend fun current(): AppSettings =
        context.settingsStore.data.map { it.toSettings() }.first()

    override suspend fun update(transform: (AppSettings) -> AppSettings) {
        val next = transform(current())
        context.settingsStore.edit { prefs ->
            prefs[Keys.ThemeId] = next.themeId
            prefs[Keys.DarkMode] = next.darkMode.name
            prefs[Keys.WeekStart] = next.weekStart.name
            prefs[Keys.OnboardingDone] = next.onboardingDone
            prefs[Keys.ReduceMotion] = next.reduceMotionOverride?.toString() ?: "null"
            prefs[Keys.Motivational] = next.motivationalMessagesEnabled
        }
    }

    private fun androidx.datastore.preferences.core.Preferences.toSettings() = AppSettings(
        themeId = get(Keys.ThemeId) ?: "twilight",
        darkMode = runCatching { DarkModeSetting.valueOf(get(Keys.DarkMode) ?: "SYSTEM") }
            .getOrDefault(DarkModeSetting.SYSTEM),
        weekStart = runCatching { WeekStart.valueOf(get(Keys.WeekStart) ?: "MONDAY") }
            .getOrDefault(WeekStart.MONDAY),
        onboardingDone = get(Keys.OnboardingDone) ?: false,
        reduceMotionOverride = when (get(Keys.ReduceMotion)) {
            "true" -> true
            "false" -> false
            else -> null
        },
        motivationalMessagesEnabled = get(Keys.Motivational) ?: true,
    )
}
