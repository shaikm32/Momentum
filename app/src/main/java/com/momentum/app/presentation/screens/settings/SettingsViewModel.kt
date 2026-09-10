package com.momentum.app.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.AppContainer
import com.momentum.app.domain.model.AppSettings
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.domain.model.WeekStart
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface SettingsEffect {
    data class ShowMessage(val message: String) : SettingsEffect
}

class SettingsViewModel(private val container: AppContainer) : ViewModel() {

    private val effects = Channel<SettingsEffect>(Channel.BUFFERED)
    val effect = effects.receiveAsFlow()

    val settings = container.settingsRepository.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    fun setTheme(id: String) = update { it.copy(themeId = id) }
    fun setDarkMode(mode: DarkModeSetting) = update { it.copy(darkMode = mode) }
    fun setWeekStart(weekStart: WeekStart) = update { it.copy(weekStart = weekStart) }
    fun setReduceMotion(value: Boolean?) = update { it.copy(reduceMotionOverride = value) }
    fun setMotivational(enabled: Boolean) = update { it.copy(motivationalMessagesEnabled = enabled) }

    fun clearAllData() {
        viewModelScope.launch {
            runCatching {
                // Cancel alarms first so nothing fires for deleted rows.
                container.reminderRepository.enabledAll()
                    .groupBy { it.activityId }.keys
                    .forEach { container.reminderScheduler.cancelForActivity(it) }
                container.reminderRepository.clearAll()
                container.completionRepository.clearAll()
                container.activityRepository.clearAll()
                effects.send(SettingsEffect.ShowMessage("All data cleared."))
            }.onFailure { effects.send(SettingsEffect.ShowMessage("Couldn't clear data — try again.")) }
        }
    }

    private fun update(transform: (AppSettings) -> AppSettings) {
        viewModelScope.launch {
            runCatching { container.settingsRepository.update(transform) }
                .onFailure { effects.send(SettingsEffect.ShowMessage("Couldn't save setting.")) }
        }
    }
}
