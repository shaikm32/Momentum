package com.momentum.app.presentation.navigation

import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.serialization.NavKeySerializer
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class RoutesTest {

    @Test
    fun `edit and detail routes carry the activity id`() {
        assertEquals(Routes.Edit(activityId = 7L), Routes.edit(7L))
        assertEquals(Routes.Detail(activityId = 9L), Routes.detail(9L))
        assertNotEquals(Routes.Today, Routes.Habits)

        // Typed keys must be @Serializable (ADR-42) — `serializer()` only exists
        // when the plugin processed the class.
        val detail = Routes.detail(9L)
        val encoded = Json.encodeToString(Routes.Detail.serializer(), detail)
        assertEquals("""{"activityId":9}""", encoded)
        assertEquals(detail, Json.decodeFromString(Routes.Detail.serializer(), encoded))

        // The Android restore path (process death / config change) round-trips
        // through NavKeySerializer: FQCN + concrete serializer.
        val stackKey = Json.encodeToString(NavKeySerializer<NavKey>(), detail)
        assertEquals(detail, Json.decodeFromString(NavKeySerializer<NavKey>(), stackKey))
        assertEquals(
            Routes.Today,
            Json.decodeFromString(
                NavKeySerializer<NavKey>(),
                Json.encodeToString(NavKeySerializer<NavKey>(), Routes.Today),
            ),
        )
    }
}
