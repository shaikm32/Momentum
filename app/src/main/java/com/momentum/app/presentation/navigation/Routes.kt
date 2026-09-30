package com.momentum.app.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Navigation 3 routes (ADR-42): every destination is an `@Serializable` [NavKey].
 *
 * String routes (`"detail/{activityId}"`) are gone — arguments travel as typed,
 * **ID-only** fields on the key itself, so a typo is a compile error instead of
 * a runtime crash. The destination screens still fetch the row from the
 * repository (via `DetailTypeGate`/`EditRoute`/their ViewModels); the keys only
 * carry identity.
 *
 * The old constant names are preserved as type names so call sites read the
 * same: `Routes.DETAIL`/`Routes.detail(id)` → `Routes.Detail(id)`.
 */
object Routes {
    /** Bottom-nav start destination (labelled "Home"). */
    @Serializable
    data object Today : NavKey

    @Serializable
    data object Habits : NavKey

    @Serializable
    data object Tasks : NavKey

    @Serializable
    data object Insights : NavKey

    /** Not bottom-nav destinations: Calendar lives inside Insights, Settings is a pushed screen. */
    @Serializable
    data object Calendar : NavKey

    @Serializable
    data object Settings : NavKey

    @Serializable
    data object Onboarding : NavKey

    @Serializable
    data object Themes : NavKey

    /**
     * Guided creation flow (ADR-20). `+` opens the Create Item chooser
     * (Habit / Task sheet); Habit continues to the intent chooser, then to
     * the intent-specific configuration; Task goes straight to its own
     * configuration. Editing reuses the same configuration screens via [Edit].
     */
    @Serializable
    data object CreateIntent : NavKey

    @Serializable
    data object CreateTask : NavKey

    /** Edit screen for an existing row — id only; the row is loaded by the destination. */
    @Serializable
    data class Edit(val activityId: Long) : NavKey

    /** Detail screen for an existing row — id only; [DetailTypeGate] resolves the body. */
    @Serializable
    data class Detail(val activityId: Long) : NavKey

    fun edit(activityId: Long): Edit = Edit(activityId)
    fun detail(activityId: Long): Detail = Detail(activityId)
}
