package com.momentum.app.domain.model

import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Activity lifecycle state.
 *
 * - ACTIVE: scheduled normally, shown on Today.
 * - PAUSED: hidden from Today, history kept, can resume.
 * - ARCHIVED: soft-delete; hidden everywhere except explicit archive views.
 */
enum class ActivityStatus { ACTIVE, PAUSED, ARCHIVED }

/**
 * Recurrence rule. Stored as a type-tagged rule (type + payload, see ADR-06),
 * never as materialized future rows. Occurrence computation lives in
 * [com.momentum.app.domain.engine.RecurrenceEngine] — pure Kotlin.
 */
sealed interface Recurrence {
    /** One-time task; [Activity.startDate] is the due date. */
    data object Once : Recurrence

    /** Every day from [Activity.startDate] on. */
    data object Daily : Recurrence

    /** Monday–Friday preset. */
    data object Weekdays : Recurrence

    /** Any subset of weekdays (Mon=1..Sun=7, [DayOfWeek] set, non-empty). */
    data class SelectedWeekdays(val days: Set<DayOfWeek>) : Recurrence {
        init { require(days.isNotEmpty()) { "days must be non-empty" } }
    }

    /** Every [n] days anchored at [Activity.startDate]. */
    data class EveryNDays(val n: Int) : Recurrence {
        init { require(n >= 1) { "n must be >= 1" } }
    }

    /** Weekly on the anchor's weekday (shorthand for EveryNWeeks(1, anchorDay)). */
    data object Weekly : Recurrence

    /** Every [n] weeks on [days] (non-empty), anchored at [Activity.startDate]. */
    data class EveryNWeeks(val n: Int, val days: Set<DayOfWeek>) : Recurrence {
        init {
            require(n >= 1) { "n must be >= 1" }
            require(days.isNotEmpty()) { "days must be non-empty" }
        }
    }

    /** Monthly on [dayOfMonth], clamped to month length (e.g. 31 → Feb 28/29). */
    data class Monthly(val dayOfMonth: Int) : Recurrence {
        init { require(dayOfMonth in 1..31) { "dayOfMonth must be 1..31" } }
    }
}

data class Activity(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val iconKey: String = "spark",
    val categoryId: Long? = null,
    /** Null = inherit category/theme accent. */
    val colorArgb: Int? = null,
    val startDate: LocalDate,
    /** Null = indefinite. Past end ⇒ nothing scheduled, history retained. */
    val endDate: LocalDate? = null,
    val recurrence: Recurrence = Recurrence.Daily,
    val targetPerOccurrence: Double = 1.0,
    val unit: String = "",
    /** 0–3, affects Today ordering. */
    val priority: Int = 0,
    val notes: String = "",
    val status: ActivityStatus = ActivityStatus.ACTIVE,
    val createdAtEpochMs: Long = 0,
    val updatedAtEpochMs: Long = 0,
) {
    init {
        require(title.isNotBlank()) { "title must not be blank" }
        require(title.length <= 120) { "title must be <= 120 chars" }
        require(priority in 0..3) { "priority must be 0..3" }
        require(targetPerOccurrence > 0) { "target must be > 0" }
        if (endDate != null) require(!endDate.isBefore(startDate)) { "endDate >= startDate" }
    }
}

data class CompletionRecord(
    val id: Long = 0,
    val activityId: Long,
    /** ISO-8601 local date (yyyy-MM-dd) of the scheduled occurrence. */
    val occurrenceDate: String,
    val completedAtEpochMs: Long,
    /** Partial credit: value vs [Activity.targetPerOccurrence]. */
    val value: Double = 1.0,
    val note: String = "",
)

data class Category(
    val id: Long = 0,
    val name: String,
    val iconKey: String = "folder",
    val colorArgb: Int? = null,
    val sortOrder: Int = 0,
    val isDefault: Boolean = false,
)

data class Reminder(
    val id: Long = 0,
    val activityId: Long,
    /** Local time HH:mm. */
    val timeLocal: String,
    /**
     * Weekday bitmask (Mon=bit0..Sun=bit6). 0 = follows the activity schedule,
     * i.e. fire on every scheduled occurrence.
     */
    val daysMask: Int = 0,
    val enabled: Boolean = true,
)

enum class DarkModeSetting { SYSTEM, LIGHT, DARK }

enum class WeekStart { MONDAY, SUNDAY }

data class AppSettings(
    val themeId: String = "twilight",
    val darkMode: DarkModeSetting = DarkModeSetting.SYSTEM,
    val weekStart: WeekStart = WeekStart.MONDAY,
    val onboardingDone: Boolean = false,
    /** Null = follow the system accessibility setting. */
    val reduceMotionOverride: Boolean? = null,
    val motivationalMessagesEnabled: Boolean = true,
)
