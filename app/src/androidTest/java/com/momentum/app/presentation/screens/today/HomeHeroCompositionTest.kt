package com.momentum.app.presentation.screens.today

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.dp
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.presentation.theme.MomentumTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * ADR-31 regression, measured in real layout bounds: the Home Hero is **one**
 * Aurora-backed surface.
 *
 * The defect these guard against was structural, not tonal — the header sat
 * outside the Hero and a blanket veil flattened the artwork, so the screen read
 * as "header → strip of Aurora → plain progress area". File checks cannot see
 * that, so these tests assert the actual geometry:
 *
 * - the artwork's bounds are exactly the Hero's bounds (a genuine background
 *   layer, full bleed, no strip);
 * - the date, greeting, Settings control, ring, count and message all sit inside
 *   that artwork;
 * - nothing sits in a gap: the artwork spans the whole Hero at every collapse
 *   value, and the Hero is one region rather than stacked pieces.
 *
 * Instrumented because the contract is a real composed layout: run with
 * `./gradlew :app:connectedDebugAndroidTest` (needs a connected device or
 * emulator; asserts bounds only, never pixels).
 */
class HomeHeroCompositionTest {

    @get:Rule
    val compose = createComposeRule()

    /** Status-bar reserve of the device the rule is running on, read in composition. */
    private var statusBarTop = 0.dp

    private fun hero(
        darkMode: DarkModeSetting = DarkModeSetting.DARK,
        collapseProgress: Float = 0f,
    ) {
        compose.setContent {
            MomentumTheme(darkMode = darkMode) {
                statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                Box(Modifier.fillMaxSize()) {
                    HomeHero(
                        dateLabel = DateLabel,
                        greeting = Greeting,
                        progress = 0.67f,
                        doneCount = 2,
                        totalCount = 3,
                        message = Message,
                        collapseProgress = collapseProgress,
                        reduceMotion = false,
                        onOpenSettings = {},
                        onCollapseRangePx = {},
                    )
                }
            }
        }
    }

    @Test
    fun artworkIsTheBackgroundOfTheEntireHero() {
        hero()

        val heroBounds = compose.onNodeWithTag(HomeHeroTag).getBoundsInRoot()
        val artworkBounds = compose.onNodeWithTag(HeroArtworkTag).getBoundsInRoot()

        assertTrue("the Hero must have real bounds", heightOf(heroBounds) > 0.dp)
        assertTrue(
            "the artwork must cover the Hero exactly, was $artworkBounds vs $heroBounds",
            artworkBounds == heroBounds,
        )
    }

    @Test
    fun everyPieceOfHeroCopySitsOnTheArtwork() {
        hero()

        val artwork = compose.onNodeWithTag(HeroArtworkTag).getBoundsInRoot()
        val header = compose.onNodeWithTag(HeroHeaderTag).getBoundsInRoot()
        val content = compose.onNodeWithTag(HeroContentTag).getBoundsInRoot()
        val settings = compose.onNodeWithContentDescription("Settings").getBoundsInRoot()
        val date = compose.onNodeWithText(DateLabel).getBoundsInRoot()
        val greeting = compose.onNodeWithText(Greeting).getBoundsInRoot()
        val percent = compose.onNodeWithText("67%").getBoundsInRoot()
        val count = compose.onNodeWithText("2 of 3 completed").getBoundsInRoot()
        val message = compose.onNodeWithText(Message).getBoundsInRoot()

        listOf(
            "header" to header,
            "content" to content,
            "settings" to settings,
            "date" to date,
            "greeting" to greeting,
            "ring" to percent,
            "count" to count,
            "message" to message,
        ).forEach { (name, bounds) ->
            assertTrue(
                "$name must be inside the artwork: $bounds not within $artwork",
                artwork.contains(bounds),
            )
        }
    }

    @Test
    fun headerAndRingFormOneContinuousRegionWithNoPlainGap() {
        hero()

        val artwork = compose.onNodeWithTag(HeroArtworkTag).getBoundsInRoot()
        val greeting = compose.onNodeWithText(Greeting).getBoundsInRoot()
        val percent = compose.onNodeWithText("67%").getBoundsInRoot()

        // The artwork runs from the top of the Hero to its bottom, so the band
        // between greeting and ring is artwork — never bare page background.
        assertTrue(
            "the artwork must start at the top of the Hero",
            artwork.top <= greeting.top,
        )
        assertTrue(
            "the artwork must continue below the ring",
            artwork.bottom >= percent.bottom,
        )
        assertTrue(
            "the Hero must be one region taller than its widest block",
            heightOf(artwork) > heightOf(compose.onNodeWithTag(HeroContentTag).getBoundsInRoot()),
        )
    }

    @Test
    fun dateSitsDirectlyOnTheArtworkWithNoBandAboveIt() {
        hero()

        val artwork = compose.onNodeWithTag(HeroArtworkTag).getBoundsInRoot()
        val date = compose.onNodeWithText(DateLabel).getBoundsInRoot()
        val greeting = compose.onNodeWithText(Greeting).getBoundsInRoot()
        val settings = compose.onNodeWithContentDescription("Settings").getBoundsInRoot()

        // The artwork owns the Hero's top edge (ADR-33: it starts at the
        // physical top of the window, behind the status bar). The only thing
        // allowed above the date is the status-bar reserve itself plus the
        // Hero's own breathing room — never a strip of page background.
        compose.waitForIdle()
        val statusBars = statusBarTop
        val dateGap = date.top - artwork.top

        assertTrue("the artwork must start at the physical top edge, was ${artwork.top}", artwork.top <= 0.dp)
        assertTrue(
            "the date must clear the status bar without drifting away from the top, was $dateGap " +
                "(status bar $statusBars)",
            dateGap in statusBars..(statusBars + 32.dp),
        )
        assertTrue("the greeting must be over the artwork too", artwork.contains(greeting))
        assertTrue("Settings must be over the artwork too", artwork.contains(settings))
    }

    @Test
    fun heroEndsShortlyAfterTheMessageWithNoDeadSpace() {
        hero()

        val artwork = compose.onNodeWithTag(HeroArtworkTag).getBoundsInRoot()
        val message = compose.onNodeWithText(Message).getBoundsInRoot()
        val ring = compose.onNodeWithText("67%").getBoundsInRoot()
        val greeting = compose.onNodeWithText(Greeting).getBoundsInRoot()

        assertTrue(
            "nothing large may follow the message, was ${artwork.bottom - message.bottom}",
            (artwork.bottom - message.bottom) <= 56.dp,
        )
        assertTrue(
            "the open Aurora band belongs between the greeting and the ring",
            (ring.top - greeting.bottom) > 0.dp,
        )
    }

    @Test
    fun artworkStillBacksTheWholeHeroWhenCollapsed() {
        hero(collapseProgress = 1f)

        val heroBounds = compose.onNodeWithTag(HomeHeroTag).getBoundsInRoot()
        val artworkBounds = compose.onNodeWithTag(HeroArtworkTag).getBoundsInRoot()
        val header = compose.onNodeWithTag(HeroHeaderTag).getBoundsInRoot()
        val content = compose.onNodeWithTag(HeroContentTag).getBoundsInRoot()

        assertTrue(
            "collapse must not expose a bare band, was $artworkBounds vs $heroBounds",
            artworkBounds == heroBounds,
        )
        assertTrue("the collapsed header must stay on the artwork", artworkBounds.contains(header))
        assertTrue("the collapsed content must stay on the artwork", artworkBounds.contains(content))
    }

    @Test
    fun lightThemeComposesTheSameSingleSurface() {
        hero(darkMode = DarkModeSetting.LIGHT)

        val heroBounds = compose.onNodeWithTag(HomeHeroTag).getBoundsInRoot()
        val artworkBounds = compose.onNodeWithTag(HeroArtworkTag).getBoundsInRoot()
        val greeting = compose.onNodeWithText(Greeting).getBoundsInRoot()
        val message = compose.onNodeWithText(Message).getBoundsInRoot()

        assertTrue(
            "the light artwork must cover the Hero too",
            artworkBounds == heroBounds,
        )
        assertTrue(artworkBounds.contains(greeting))
        assertTrue(artworkBounds.contains(message))
        compose.onNodeWithTag(HeroArtworkTag).assertIsDisplayed()
    }

    private companion object {
        const val DateLabel = "FRI, SEP 25"
        const val Greeting = "Good evening"
        const val Message = "One more - finish strong."
    }
}

/** Bounds containment with a small tolerance for sub-pixel layout rounding. */
private fun DpRect.contains(other: DpRect): Boolean =
    other.left >= left - 1.dp &&
        other.right <= right + 1.dp &&
        other.top >= top - 1.dp &&
        other.bottom <= bottom + 1.dp

private fun heightOf(rect: DpRect): Dp = rect.bottom - rect.top
