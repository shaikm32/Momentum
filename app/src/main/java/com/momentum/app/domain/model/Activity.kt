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
 * Semantic kind of an activity. Stored explicitly — never inferred from
 * [Recurrence] (a HABIT may be one-time and a TASK may repeat).
 *
 * - HABIT: something built or reduced consistently (streaks/analytics).
 * - TASK: something to get done (completion-focused).
 */
enum class ActivityType { HABIT, TASK }

/**
 * What the user is trying to change with a HABIT.
 *
 * - BUILD: do something consistently. Measured as Binary (just done →
 *   target 1, no unit) or Target (numeric value + unit).
 * - REDUCE: cut back on something. Measured per [ReduceMode]: either a
 *   numeric limit ([ReduceMode.LIMIT]) or zero ([ReduceMode.COMPLETE]).
 *
 * Persisted explicitly on every activity row. Meaningless for TASKs (stored
 * but ignored — a Task stays a Task and never gains intent UI).
 *
 * Legacy note: v3 exposed a third intent (AVOID, "quit entirely"). It was
 * folded into REDUCE + [ReduceMode.COMPLETE] — same zero-target mechanism,
 * no separate intent. Persisted "AVOID" strings are mapped to REDUCE on
 * read (see `toDomain`), so old databases load without crashing.
 */
enum class HabitIntent { BUILD, REDUCE }

/**
 * How far a REDUCE habit goes. Derived from the stored measure, not stored
 * separately (see [Activity.reduceMode]): a REDUCE row with the default
 * target (1.0) and no unit is COMPLETE (zero is the target); any real limit
 * amount/unit is LIMIT. LIMIT always carries a validated amount + unit, so
 * the derivation is unambiguous in both directions.
 */
enum class ReduceMode { LIMIT, COMPLETE }

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
    /** Semantic kind. Defaults to HABIT so pre-type rows keep their meaning. */
    val activityType: ActivityType = ActivityType.HABIT,
    /**
     * Habit intent. Defaults to BUILD so pre-intent rows keep working;
     * ignored for [ActivityType.TASK]. Binary-vs-Target within BUILD is
     * derived from [targetPerOccurrence]/[unit], not stored separately.
     */
    val habitIntent: HabitIntent = HabitIntent.BUILD,
    /**
     * Optional task due time (`HH:mm`). Display-only: reminders stay
     * separate (see [Reminder]). Null = no specific time. Habits never set it.
     */
    val dueTime: String? = null,
    val description: String = "",
    val iconKey: String = "spark",
    /**
     * Persisted predefined template id (see `HabitTemplates`); null = custom
     * or pre-template rows. Required so a predefined habit keeps its icon
     * lock after rename (title no longer matches `matchTemplate`).
     * Custom template ids are also stored; only non-custom ids lock the icon.
     */
    val habitTemplateId: String? = null,
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
        if (dueTime != null) {
            require(dueTime.matches(Regex("\\d{2}:\\d{2}"))) { "dueTime must be HH:mm" }
            val (h, m) = dueTime.split(":").map { it.toInt() }
            require(h in 0..23 && m in 0..59) { "dueTime must be HH:mm" }
        }
    }

    /**
     * Derives the [ReduceMode] for REDUCE habits from the stored measure.
     * Meaningful only when [habitIntent] is [HabitIntent.REDUCE]; for BUILD
     * rows the result is ignored. COMPLETE rows (including legacy AVOID rows
     * mapped to REDUCE) carry the default target with no unit.
     */
    fun reduceMode(): ReduceMode =
        if (targetPerOccurrence == 1.0 && unit.isBlank()) ReduceMode.COMPLETE else ReduceMode.LIMIT
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
