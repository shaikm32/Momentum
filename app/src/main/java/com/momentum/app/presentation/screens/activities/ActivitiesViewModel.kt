package com.momentum.app.presentation.screens.activities

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.R
import com.momentum.app.domain.engine.RecurrenceEngine
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.ActivityType
import com.momentum.app.domain.model.Category
import com.momentum.app.domain.repository.ActivityRepository
import com.momentum.app.domain.repository.CategoryRepository
import com.momentum.app.domain.repository.ReminderRepository
import com.momentum.app.domain.usecase.ToggleCompletionUseCase
import com.momentum.app.notification.ReminderScheduler
import com.momentum.app.presentation.UiText
import com.momentum.app.presentation.ViewModelArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ActivityTab { ACTIVE, PAUSED, ARCHIVED }
enum class ActivitySort { PRIORITY }

@Immutable
data class ActivitiesUiState(
    val query: String = "",
    val tab: ActivityTab = ActivityTab.ACTIVE,
    val categoryId: Long? = null,
    val sort: ActivitySort = ActivitySort.PRIORITY,
    val items: List<Activity> = emptyList(),
    val categories: List<Category> = emptyList(),
    val categoryNames: Map<Long, String> = emptyMap(),
)

/** The four user-mutable filter fields — one holder, mutated atomically (ADR-44). */
private data class ActivityFilters(
    val query: String = "",
    val tab: ActivityTab = ActivityTab.ACTIVE,
    val categoryId: Long? = null,
    val sort: ActivitySort = ActivitySort.PRIORITY,
)

/** MVI events (ADR-44): the screen's nine-action surface, dispatched via `onEvent`. */
sealed interface ActivitiesEvent {
    data class QueryChanged(val query: String) : ActivitiesEvent
    data class TabChanged(val tab: ActivityTab) : ActivitiesEvent
    data class CategoryChanged(val categoryId: Long?) : ActivitiesEvent
    data class Archive(val id: Long) : ActivitiesEvent
    data class Pause(val id: Long) : ActivitiesEvent
    data class Resume(val id: Long) : ActivitiesEvent
    data class Restore(val id: Long) : ActivitiesEvent
    data class ToggleToday(val id: Long) : ActivitiesEvent
    data class DeleteHard(val id: Long) : ActivitiesEvent
}

/** One-shot side effects (ADR-44) — replaces the former `Channel<String>`. */
sealed interface ActivitiesUiEffect {
    data class ShowMessage(val message: UiText) : ActivitiesUiEffect
}

/**
 * Shared list logic for the Activities screen. The bottom-nav Habits and
 * Tasks destinations are the same screen pinned to an [ActivityType] via
 * [type]; a null [type] shows every type (unfiltered).
 */
@HiltViewModel
class ActivitiesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val activityRepository: ActivityRepository,
    private val categoryRepository: CategoryRepository,
    private val reminderRepository: ReminderRepository,
    private val reminderScheduler: ReminderScheduler,
    private val toggleCompletion: ToggleCompletionUseCase,
) : ViewModel() {

    /** Bottom-nav pin: null = every type (unfiltered), passed via [ViewModelArgs.ACTIVITY_TYPE]. */
    private val type: ActivityType? = savedStateHandle.get<String>(ViewModelArgs.ACTIVITY_TYPE)
        ?.let { ActivityType.valueOf(it) }

    private val _filters = MutableStateFlow(ActivityFilters())

    private val effects = Channel<ActivitiesUiEffect>(Channel.BUFFERED)
    val effect = effects.receiveAsFlow()

    val state: StateFlow<ActivitiesUiState> = combine(
        activityRepository.observeAll(),
        categoryRepository.observeAll(),
        _filters,
    ) { activities, categories, f ->
        val names = categories.associate { it.id to it.name }
        ActivitiesUiState(
            query = f.query, tab = f.tab, categoryId = f.categoryId, sort = f.sort,
            categories = categories, categoryNames = names,
            items = activities
                .filter { type == null || it.activityType == type }
                .filter { it.status == f.tab.toStatus() }
                .filter { f.categoryId == null || it.categoryId == f.categoryId }
                .filter { f.query.isBlank() || it.title.contains(f.query, ignoreCase = true) }
                .let { list ->
                    when (f.sort) {
                        ActivitySort.PRIORITY -> list.sortedWith(
                            compareByDescending<Activity> { it.priority }.thenBy { it.title },
                        )
                    }
                },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ActivitiesUiState())

    /** Single entry point for every user action on this screen (ADR-44). */
    fun onEvent(event: ActivitiesEvent) {
        when (event) {
            is ActivitiesEvent.QueryChanged -> _filters.update { it.copy(query = event.query) }
            is ActivitiesEvent.TabChanged -> _filters.update { it.copy(tab = event.tab) }
            is ActivitiesEvent.CategoryChanged -> _filters.update { it.copy(categoryId = event.categoryId) }
            is ActivitiesEvent.Archive -> update(event.id, ActivityStatus.ARCHIVED)
            is ActivitiesEvent.Pause -> update(event.id, ActivityStatus.PAUSED)
            is ActivitiesEvent.Resume -> update(event.id, ActivityStatus.ACTIVE)
            is ActivitiesEvent.Restore -> update(event.id, ActivityStatus.ACTIVE)
            is ActivitiesEvent.ToggleToday -> toggleToday(event.id)
            is ActivitiesEvent.DeleteHard -> deleteHard(event.id)
        }
    }

    /** Quick-complete for today; no-ops with an explanatory message when unscheduled. */
    private fun toggleToday(id: Long) {
        viewModelScope.launch {
            val activity = activityRepository.getById(id) ?: return@launch
            val today = LocalDate.now()
            if (!RecurrenceEngine.isScheduled(activity, today)) {
                effects.send(ActivitiesUiEffect.ShowMessage(UiText(R.string.activities_not_scheduled_today)))
                return@launch
            }
            // P2#2: a "done" tap records the full target (8/8), not 1.0.
            runCatching { toggleCompletion(id, today, activity.targetPerOccurrence) }
                .onFailure { effects.send(ActivitiesUiEffect.ShowMessage(UiText(R.string.error_couldnt_save))) }
        }
    }

    private fun deleteHard(id: Long) {
        viewModelScope.launch {
            runCatching {
                reminderScheduler.cancelForActivity(id)
                reminderRepository.deleteForActivity(id)
                activityRepository.deleteHard(id)
            }
        }
    }

    private fun update(id: Long, status: ActivityStatus) {
        viewModelScope.launch {
            runCatching {
                activityRepository.setStatus(id, status)
                if (status != ActivityStatus.ACTIVE) reminderScheduler.cancelForActivity(id)
                else reminderScheduler.scheduleForActivity(id)
            }
        }
    }

    private fun ActivityTab.toStatus() = when (this) {
        ActivityTab.ACTIVE -> ActivityStatus.ACTIVE
        ActivityTab.PAUSED -> ActivityStatus.PAUSED
        ActivityTab.ARCHIVED -> ActivityStatus.ARCHIVED
    }
}
