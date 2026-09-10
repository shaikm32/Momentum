package com.momentum.app.domain.model

import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Phase 0 domain stubs. Full domain models land in Phase 2;
 * recurrence rules land in Phase 3. Kept minimal on purpose.
 */

enum class ActivityStatus { ACTIVE, PAUSED, ARCHIVED }

sealed interface Recurrence {
    data object Once : Recurrence
    data object Daily : Recurrence
    data class SelectedWeekdays(val days: Set<DayOfWeek>) : Recurrence
    data class EveryNDays(val n: Int) : Recurrence
    data class EveryNWeeks(val n: Int, val days: Set<DayOfWeek>) : Recurrence
    data class Monthly(val dayOfMonth: Int) : Recurrence
}

data class Activity(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val colorArgb: Int = 0,
    val iconKey: String = "spark",
    val categoryId: Long? = null,
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val recurrence: Recurrence = Recurrence.Daily,
    val targetPerOccurrence: Double = 1.0,
    val unit: String = "",
    val priority: Int = 0,
    val notes: String = "",
    val status: ActivityStatus = ActivityStatus.ACTIVE,
    val createdAtEpochMs: Long = 0,
    val updatedAtEpochMs: Long = 0,
)

data class CompletionRecord(
    val id: Long = 0,
    val activityId: Long,
    /** ISO-8601 local date (yyyy-MM-dd) of the scheduled occurrence. */
    val occurrenceDate: String,
    val completedAtEpochMs: Long,
    val value: Double = 1.0,
    val note: String = "",
)
