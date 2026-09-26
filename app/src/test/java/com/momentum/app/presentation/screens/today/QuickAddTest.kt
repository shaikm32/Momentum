package com.momentum.app.presentation.screens.today

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * DASHBOARD_SPECS §14 / DECISIONS §20: Quick Add is a compact circular `+`
 * floating button at the bottom-right, immediately above the bottom
 * navigation, opening Create Item (Habit / Task) — never a multi-purpose
 * action menu. It is the only add affordance on a populated Home list.
 */
class QuickAddTest {

    private val screen =
        File("src/main/java/com/momentum/app/presentation/screens/today/TodayScreen.kt").readText()
    private val nav =
        File("src/main/java/com/momentum/app/presentation/navigation/MomentumNavGraph.kt").readText()

    /** Comments must never satisfy an assertion about rendered UI. */
    private fun codeOf(text: String): String = text
        .lines()
        .filterNot { it.trimStart().startsWith("//") || it.trimStart().startsWith("*") }
        .joinToString("\n")

    private fun count(haystack: String, needle: String): Int =
        haystack.split(needle).size - 1

    @Test
    fun `Quick Add is a circular floating button above the navigation`() {
        val code = codeOf(screen)
        assertTrue("Quick Add must be its own composable", code.contains("private fun QuickAddFab("))
        assertTrue("it must be anchored bottom-right", code.contains("Alignment.BottomEnd"))
        assertTrue("it must be circular", code.contains("liquidGlass(CircleShape, GlassLevel.HERO)"))
        assertTrue("it must use the add glyph", code.contains("MomentumIcons.add"))
        assertFalse("a FAB does not belong in the Scaffold", nav.contains("floatingActionButton"))
    }

    @Test
    fun `the in-list add button is gone so Quick Add is the list's only add action`() {
        val code = codeOf(screen)
        assertFalse(
            "the retired in-list add button must not come back",
            code.contains("+ New activity"),
        )
        assertTrue(code.contains("QuickAddFab("))
        // Both section headers still present and in spec order.
        assertTrue(count(code, "title = \"Remaining today\"") == 1)
        assertTrue(count(code, "title = \"Completed today\"") == 1)
        assertTrue(
            code.indexOf("title = \"Remaining today\"") < code.indexOf("title = \"Completed today\""),
        )
    }

    @Test
    fun `Quick Add keeps the last activity scroll-clear of the button`() {
        val code = codeOf(screen)
        assertTrue(
            "the list must reserve bottom room under the floating button",
            code.contains("MomentumSpacing.card + QuickAddFabSize + MomentumSpacing.compact"),
        )
    }

    @Test
    fun `Quick Add is a labelled button with a comfortable target`() {
        val code = codeOf(screen)
        assertTrue("TalkBack must hear the feature name", code.contains("contentDescription = \"Quick Add\""))
        assertTrue("it must be announced as a button", code.contains("role = Role.Button"))
        assertTrue(
            "diameter must clear the 48 dp minimum touch target",
            code.contains("QuickAddFabSize = 56.dp"),
        )
        assertTrue("it must keep glass press feedback", code.contains(".glassPress(interaction)"))
    }

    @Test
    fun `tapping Quick Add opens Create Item and nothing else`() {
        assertTrue(
            "Quick Add must open the Habit/Task chooser",
            count(nav, "showCreateItem = true") >= 4,
        )
        assertTrue(nav.contains("if (showCreateItem)"))
    }
}
