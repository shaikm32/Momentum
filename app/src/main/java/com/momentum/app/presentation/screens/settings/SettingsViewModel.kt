package com.momentum.app.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.R
import com.momentum.app.data.repository.UserDataReset
import com.momentum.app.domain.model.AppSettings
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.domain.model.WeekStart
import com.momentum.app.domain.repository.ReminderRepository
import com.momentum.app.domain.repository.SettingsRepository
import com.momentum.app.notification.ReminderScheduler
import com.momentum.app.presentation.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface SettingsEffect {
    data class ShowMessage(val message: UiText) : SettingsEffect
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val reminderRepository: ReminderRepository,
    private val reminderScheduler: ReminderScheduler,
    private val userDataReset: UserDataReset,
) : ViewModel() {

    private val effects = Channel<SettingsEffect>(Channel.BUFFERED)
    val effect = effects.receiveAsFlow()

    val settings = settingsRepository.observe()
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
                reminderRepository.enabledAll()
                    .groupBy { it.activityId }.keys
                    .forEach { reminderScheduler.cancelForActivity(it) }
                // Single transaction: never partially cleared (see UserDataReset).
                userDataReset.clearAllUserData()
                effects.send(SettingsEffect.ShowMessage(UiText(R.string.settings_all_data_cleared)))
            }.onFailure {
                effects.send(SettingsEffect.ShowMessage(UiText(R.string.error_couldnt_clear_data)))
            }
        }
    }

    private fun update(transform: (AppSettings) -> AppSettings) {
        viewModelScope.launch {
            runCatching { settingsRepository.update(transform) }
                .onFailure {
                    effects.send(SettingsEffect.ShowMessage(UiText(R.string.error_couldnt_save_setting)))
                }
        }
    }
}
