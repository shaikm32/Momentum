package com.momentum.app.presentation.navigation

import com.momentum.app.domain.model.HabitIntent
import org.junit.Assert.assertEquals
import org.junit.Test

class RoutesTest {

    @Test
    fun `createHabit route carries the selected intent`() {
        assertEquals("create/habit/config?intent=BUILD", Routes.createHabit(HabitIntent.BUILD))
        assertEquals("create/habit/config?intent=REDUCE", Routes.createHabit(HabitIntent.REDUCE))
    }

    @Test
    fun `parseHabitIntent selects the correct intent`() {
        assertEquals(HabitIntent.BUILD, Routes.parseHabitIntent("BUILD"))
        assertEquals(HabitIntent.REDUCE, Routes.parseHabitIntent("REDUCE"))
    }

    @Test
    fun `parseHabitIntent maps legacy AVOID to REDUCE`() {
        assertEquals(HabitIntent.REDUCE, Routes.parseHabitIntent("AVOID"))
    }

    @Test
    fun `parseHabitIntent degrades to BUILD instead of crashing`() {
        assertEquals(HabitIntent.BUILD, Routes.parseHabitIntent(null))
        assertEquals(HabitIntent.BUILD, Routes.parseHabitIntent(""))
        assertEquals(HabitIntent.BUILD, Routes.parseHabitIntent("SOME_FUTURE_INTENT"))
    }

    @Test
    fun `edit and detail routes carry the activity id`() {
        assertEquals("edit?activityId=7", Routes.edit(7))
        assertEquals("detail/9", Routes.detail(9))
    }
}
