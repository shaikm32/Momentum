package com.momentum.app.presentation.screens.today

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * DASHBOARD_SPECS §11: completing an activity relocates its row from
 * "Remaining today" to "Completed today" — the row must glide to its new place
 * rather than vanish, and reduced motion must switch that off completely
 * (AGENTS §9: never force animation on users who disable it).
 */
class HomeSectionMotionTest {

    private val source =
        File("src/main/java/com/momentum/app/presentation/screens/today/TodayScreen.kt").readText()

    private fun count(needle: String): Int = source.split(needle).size - 1

    @Test
    fun `every home row and section header participates in list motion`() {
        assertTrue("rows must animate placement", count("Modifier.animateItem(") >= 2)
        assertTrue("headers must fade with their rows", count(".animateItem(") >= 4)
        assertTrue(count("placementSpec = motionPlace") >= 2)
        assertTrue(count("fadeInSpec = motionFade") >= 4)
    }

    @Test
    fun `reduced motion nulls every spec instead of shortening it`() {
        assertTrue(source.contains("val motionFade: FiniteAnimationSpec<Float>?"))
        assertTrue(source.contains("val motionPlace: FiniteAnimationSpec<IntOffset>?"))
        assertTrue(count("if (reduceMotion) null else") >= 2)
        assertTrue(source.contains("spring(stiffness = Spring.StiffnessMediumLow)"))
    }

    @Test
    fun `the row forwards the incoming modifier so the animation reaches the card`() {
        val signature = source.substringAfter("private fun TodayActivityRow(").take(500)
        assertTrue(signature.contains("modifier: Modifier = Modifier"))
        assertTrue(signature.contains("modifier.padding(horizontal = MomentumSpacing.screen)"))
    }

    @Test
    fun `section ordering is still fixed and empty sections stay hidden`() {
        assertTrue(count("title = stringResource(R.string.today_section_remaining)") == 1)
        assertTrue(count("title = stringResource(R.string.today_section_completed)") == 1)
        assertTrue(count("if (remaining.isNotEmpty())") == 1)
        assertTrue(count("if (finished.isNotEmpty())") == 1)
    }
}
