package com.momentum.app.presentation.screens.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.AppContainer
import com.momentum.app.domain.engine.RecurrenceEngine
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.Category
import java.time.LocalDate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ActivityTab { ACTIVE, PAUSED, ARCHIVED }
enum class ActivitySort { PRIORITY, TITLE, RECENT }

data class ActivitiesUiState(
    val query: String = "",
    val tab: ActivityTab = ActivityTab.ACTIVE,
    val categoryId: Long? = null,
    val sort: ActivitySort = ActivitySort.PRIORITY,
    val items: List<Activity> = emptyList(),
    val categories: List<Category> = emptyList(),
    val categoryNames: Map<Long, String> = emptyMap(),
)

class ActivitiesViewModel(private val container: AppContainer) : ViewModel() {

    private val query = MutableStateFlow("")
    private val tab = MutableStateFlow(ActivityTab.ACTIVE)
    private val categoryId = MutableStateFlow<Long?>(null)
    private val sort = MutableStateFlow(ActivitySort.PRIORITY)

    private val messagesChannel = Channel<String>(Channel.BUFFERED)
    val messages = messagesChannel.receiveAsFlow()

    val state = combine(
        container.activityRepository.observeAll(),
        container.categoryRepository.observeAll(),
        combine(query, tab) { q, t -> q to t },
        categoryId,
        sort,
    ) { activities, categories, queryAndTab, cat, s ->
        val (q, t) = queryAndTab
        val names = categories.associate { it.id to it.name }
        ActivitiesUiState(
            query = q, tab = t, categoryId = cat, sort = s,
            categories = categories, categoryNames = names,
            items = activities
                .filter { it.status == t.toStatus() }
                .filter { cat == null || it.categoryId == cat }
                .filter { q.isBlank() || it.title.contains(q, ignoreCase = true) }
                .let { list ->
                    when (s) {
                        ActivitySort.PRIORITY -> list.sortedWith(
                            compareByDescending<Activity> { it.priority }.thenBy { it.title },
                        )
                        ActivitySort.TITLE -> list.sortedBy { it.title.lowercase() }
                        ActivitySort.RECENT -> list.sortedByDescending { it.createdAtEpochMs }
                    }
                },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ActivitiesUiState())

    fun setQuery(q: String) { query.value = q }
    fun setTab(t: ActivityTab) { tab.value = t }
    fun setCategory(id: Long?) { categoryId.value = id }
    fun setSort(s: ActivitySort) { sort.value = s }

    fun archive(id: Long) = update(id, ActivityStatus.ARCHIVED)
    fun pause(id: Long) = update(id, ActivityStatus.PAUSED)
    fun resume(id: Long) = update(id, ActivityStatus.ACTIVE)

    /** Quick-complete for today; no-ops with an explanatory message when unscheduled. */
    fun toggleToday(id: Long) {
        viewModelScope.launch {
            val activity = container.activityRepository.getById(id) ?: return@launch
            val today = LocalDate.now()
            if (!RecurrenceEngine.isScheduled(activity, today)) {
                messagesChannel.send("Not scheduled today.")
                return@launch
            }
            // P2#2: a "done" tap records the full target (8/8), not 1.0.
            runCatching { container.toggleCompletion(id, today, activity.targetPerOccurrence) }
                .onFailure { messagesChannel.send("Couldn't save — try again.") }
        }
    }

    fun deleteHard(id: Long) {
        viewModelScope.launch {
            runCatching {
                container.reminderScheduler.cancelForActivity(id)
                container.reminderRepository.deleteForActivity(id)
                container.activityRepository.deleteHard(id)
            }
        }
    }

    private fun update(id: Long, status: ActivityStatus) {
        viewModelScope.launch {
            runCatching {
                container.activityRepository.setStatus(id, status)
                if (status != ActivityStatus.ACTIVE) container.reminderScheduler.cancelForActivity(id)
                else container.reminderScheduler.scheduleForActivity(id)
            }
        }
    }

    private fun ActivityTab.toStatus() = when (this) {
        ActivityTab.ACTIVE -> ActivityStatus.ACTIVE
        ActivityTab.PAUSED -> ActivityStatus.PAUSED
        ActivityTab.ARCHIVED -> ActivityStatus.ARCHIVED
    }
}
