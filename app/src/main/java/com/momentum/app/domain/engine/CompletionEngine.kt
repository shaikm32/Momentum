package com.momentum.app.domain.engine

import com.momentum.app.domain.model.Activity
import java.time.LocalDate

/**
 * Pure-Kotlin completion rule (zero Android imports).
 *
 * Momentum has exactly two completion methods (DASHBOARD_SPECS §10,
 * DECISIONS §13):
 *
 * - **Just Mark it Done** — [isTargetBased] is false (default target, no unit).
 *   Tapping the completion control records the occurrence immediately.
 * - **Reach a Target** — [isTargetBased] is true. A logged actual value is
 *   compared against the configured target:
 *   `actual >= target` → complete, `actual < target` → incomplete while the
 *   partial value is retained. Above-target values are valid and are never
 *   clamped to the target.
 *
 * The rule lives in one place so Home, detail screens, analytics and streaks
 * all read the same definition of "completed" — never a second copy in the
 * UI layer. Reversal is the same rule evaluated against the new value (or an
 * absent record), so both directions recalculate identically.
 */
object CompletionEngine {

    /**
     * True when the activity measures success as "Reach a Target" rather than
     * "Just Mark it Done". Derived from the stored measure only (the data model
     * intentionally does not persist the choice separately).
     */
    fun isTargetBased(activity: Activity): Boolean =
        activity.targetPerOccurrence != 1.0 || activity.unit.isNotBlank()

    /**
     * Completion rule for a single occurrence.
     *
     * @param actual the stored value for the occurrence, or `null` when
     *   nothing has been logged (nothing logged ⇒ incomplete).
     */
    fun isComplete(activity: Activity, actual: Double?): Boolean =
        actual != null && actual >= activity.targetPerOccurrence

    /**
     * The subset of [valuesByDate] that counts as a **successful scheduled
     * opportunity**. This is the set streaks and analytics must consume —
     * a partial below-target value is retained as progress but must not
     * continue a streak.
     */
    fun successfulDates(
        activity: Activity,
        valuesByDate: Map<LocalDate, Double>,
    ): Set<LocalDate> = valuesByDate.filterValues { isComplete(activity, it) }.keys
}
