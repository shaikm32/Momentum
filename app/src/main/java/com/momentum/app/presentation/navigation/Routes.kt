package com.momentum.app.presentation.navigation

import com.momentum.app.domain.model.HabitIntent

object Routes {
    /** Bottom-nav start destination (labelled "Home"). */
    const val TODAY = "today"
    const val HABITS = "habits"
    const val TASKS = "tasks"
    const val INSIGHTS = "insights"

    /** Not bottom-nav destinations: Calendar lives inside Insights, Settings is a pushed screen. */
    const val CALENDAR = "calendar"
    const val SETTINGS = "settings"
    const val ONBOARDING = "onboarding"
    const val THEMES = "themes"

    /**
     * Guided creation flow (ADR-20). `+` opens the Create Item chooser
     * (Habit / Task sheet); Habit continues to the intent chooser, then to
     * the intent-specific configuration; Task goes straight to its own
     * configuration. Editing reuses the same configuration screens via [EDIT].
     */
    const val CREATE_INTENT = "create/habit"
    const val CREATE_HABIT = "create/habit/config?intent={intent}"
    const val CREATE_TASK = "create/task"
    const val EDIT = "edit?activityId={activityId}"
    const val DETAIL = "detail/{activityId}"

    fun createHabit(intent: HabitIntent) = "create/habit/config?intent=${intent.name}"

    /** Parses the habit-config argument. Unknown/missing values degrade to BUILD, never crash. Legacy "AVOID" deep links map to REDUCE. */
    fun parseHabitIntent(raw: String?): HabitIntent = when (raw) {
        "AVOID" -> HabitIntent.REDUCE
        else -> runCatching { HabitIntent.valueOf(raw.orEmpty()) }.getOrDefault(HabitIntent.BUILD)
    }

    fun edit(activityId: Long) = "edit?activityId=$activityId"
    fun detail(activityId: Long) = "detail/$activityId"
}
