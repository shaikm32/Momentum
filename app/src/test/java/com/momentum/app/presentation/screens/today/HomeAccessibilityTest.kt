package com.momentum.app.presentation.screens.today

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * DASHBOARD_SPECS §21: screen readers, accessible labels, minimum touch
 * targets, sufficient contrast and reduced motion must all hold on Home —
 * with the completion state readable without animation or colour alone.
 */
class HomeAccessibilityTest {

    private fun read(path: String): String {
        val file = File(path)
        assertTrue("missing source: ${file.absolutePath}", file.isFile)
        return file.readText()
    }

    private val screen = read(
        "src/main/java/com/momentum/app/presentation/screens/today/TodayScreen.kt",
    )
    private val button = read(
        "src/main/java/com/momentum/app/presentation/components/CompletionButton.kt",
    )

    private fun count(text: String, needle: String): Int = text.split(needle).size - 1

    @Test
    fun `both section titles are announced as screen-reader headings`() {
        assertTrue(
            "Remaining today must be a heading",
            count(screen, ".semantics { heading() }") == 2,
        )
    }

    @Test
    fun `reduced motion reaches the hero, the list and the aurora`() {
        assertTrue("aurora must not animate", screen.contains("animated = !reduceMotion"))
        assertTrue("hero must receive the flag", screen.contains("reduceMotion = reduceMotion"))
        assertTrue(
            "list motion must switch off, not just slow down",
            count(screen, "if (reduceMotion) null else") >= 2,
        )
        assertTrue(
            "the completion state must still be visible without motion",
            button.contains("if (completed) 1f else 0.94f"),
        )
    }

    @Test
    fun `every interactive control on Home clears the 48dp minimum target`() {
        assertTrue(
            "completion control height",
            button.contains(".size(MomentumHeights.controlMin)"),
        )
        assertTrue(
            "header action buttons",
            read(
                "src/main/java/com/momentum/app/presentation/components/LiquidGlass.kt",
            ).contains(".size(MomentumHeights.iconTarget)"),
        )
    }

    @Test
    fun `rows are grouped by completion state rather than tinted in place`() {
        // The call now also carries `contentType` (Phase E2), so the guard pins
        // the key expression, not the closing paren.
        assertTrue(
            "remaining rows come from state.remaining",
            screen.contains("items(remaining, key = { it.activity.id }"),
        )
        assertTrue(
            "completed rows come from state.completed",
            screen.contains("items(finished, key = { it.activity.id }"),
        )
        assertTrue(count(screen, "completed = false,") >= 1)
        assertTrue(count(screen, "completed = true,") >= 1)
    }
}
