package com.momentum.app.presentation.components

import com.momentum.app.R
import com.momentum.app.domain.model.Activity
import java.io.File
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * DASHBOARD_SPECS §21 / DECISIONS §34: the completion control announces its
 * *action* with the activity's own title, and "Reach a Target" activities must
 * not promise an instant toggle they do not perform.
 *
 * The wording lives in `res/values/strings.xml`; the decision (which label)
 * stays a pure function, so these tests pin both halves — the mapping here and
 * the exact English sentence plus its `%1$s` slot in [strings].
 */
class CompletionLabelTest {

    private val start = LocalDate.of(2026, 9, 25)
    private val binary = Activity(title = "Workout", startDate = start)
    private val target = Activity(
        title = "Drink water",
        startDate = start,
        targetPerOccurrence = 8.0,
        unit = "glasses",
    )

    private val strings = File("src/main/res/values/strings.xml").readText()

    /** Pins the exact English wording + `%1$s` slot behind a label resource. */
    private fun assertString(name: String, expected: String) {
        assertTrue(
            "strings.xml must still carry <string name=\"$name\">$expected</string>",
            strings.contains("<string name=\"$name\">$expected</string>"),
        )
    }

    @Test
    fun `binary incomplete names the completion action`() {
        assertEquals(R.string.cd_complete_activity, completionActionLabelRes(binary, completed = false))
        assertString("cd_complete_activity", "Complete %1\$s")
    }

    @Test
    fun `binary completed names the reversal action`() {
        assertEquals(R.string.cd_mark_incomplete, completionActionLabelRes(binary, completed = true))
        assertString("cd_mark_incomplete", "Mark %1\$s incomplete")
    }

    @Test
    fun `target incomplete points at the value dialog`() {
        assertEquals(R.string.cd_enter_progress, completionActionLabelRes(target, completed = false))
        assertString("cd_enter_progress", "Enter today\\'s progress for %1\$s")
    }

    @Test
    fun `target completed points at editing, not toggling`() {
        assertEquals(R.string.cd_edit_progress, completionActionLabelRes(target, completed = true))
        assertString("cd_edit_progress", "Edit today\\'s progress for %1\$s")
    }

    @Test
    fun `metadata label is null for plain activities and shaped for targets`() {
        assertNull(targetProgressLabel(binary, 0.0))
        assertEquals("5/8 glasses", targetProgressLabel(target, 5.0))
    }

    @Test
    fun `above-target values are shown as logged, never clamped`() {
        assertEquals("10/8 glasses", targetProgressLabel(target, 10.0))
    }

    @Test
    fun `both label helpers read the engine instead of re-deciding target mode`() {
        val card = File("src/main/java/com/momentum/app/presentation/components/ActivityCard.kt")
            .readText()
        assertTrue(card.contains("CompletionEngine.isTargetBased(activity)"))
        assertFalse(card.contains("targetPerOccurrence == 1.0 && activity.unit.isBlank()"))
    }
}

/**
 * The Home row must stay readable: dimming the whole card with an alpha layer
 * dropped body-text contrast below 4.5:1 in the light theme, so "completed"
 * is expressed through the surface instead (DASHBOARD_SPECS §21, AGENTS §9).
 * Reduced motion skips the micro-interpolations rather than the state change.
 */
class ActivityCardSurfaceTest {

    private val card =
        File("src/main/java/com/momentum/app/presentation/components/ActivityCard.kt").readText()
    private val button =
        File("src/main/java/com/momentum/app/presentation/components/CompletionButton.kt").readText()

    @Test
    fun `completed rows keep their text at full opacity`() {
        assertFalse(card.contains("alpha(cardAlpha)"))
        assertFalse(card.contains(".alpha("))
        assertTrue(card.contains("GlassLevel.CONTROL"))
        assertTrue(card.contains("if (completed) GlassLevel.CONTROL else GlassLevel.CARD"))
    }

    @Test
    fun `completion pop respects reduced motion`() {
        assertTrue(button.contains("rememberReduceMotion(null)"))
        assertTrue(button.contains("if (reduceMotion) tween(0) else spring"))
    }

    @Test
    fun `the row hands its own activity title to the control`() {
        assertTrue(card.contains("contentDescription = completionActionLabel(activity, completed)"))
    }
}
