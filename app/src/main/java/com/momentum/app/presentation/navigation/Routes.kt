package com.momentum.app.presentation.navigation

object Routes {
    const val TODAY = "today"
    const val ACTIVITIES = "activities"
    const val INSIGHTS = "insights"
    const val CALENDAR = "calendar"
    const val SETTINGS = "settings"
    const val ONBOARDING = "onboarding"
    const val THEMES = "themes"
    const val ADD = "add"
    const val EDIT = "edit?activityId={activityId}"
    const val DETAIL = "detail/{activityId}"

    fun edit(activityId: Long) = "edit?activityId=$activityId"
    fun detail(activityId: Long) = "detail/$activityId"
}
