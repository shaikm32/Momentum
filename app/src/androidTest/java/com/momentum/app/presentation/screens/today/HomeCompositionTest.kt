package com.momentum.app.presentation.screens.today

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.dp
import com.momentum.app.R
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.domain.usecase.TodayItem
import com.momentum.app.domain.usecase.TodaySections
import com.momentum.app.presentation.UiText
import com.momentum.app.presentation.aurora.AuroraState
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumTheme
import java.time.LocalDate
import kotlin.math.abs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * ADR-47 Amendment Notes 5 and 7 and ADR-48 Amendment Note 5, measured in real
 * layout bounds. These are the *retained* geometric outcomes of the approved
 * composition:
 *
 * - the atmosphere is the backdrop of the **whole content area** and the artwork
 *   is its **upper region**, at the window top with no band above it;
 * - the chrome is **fixed**, sits over that artwork, and keeps its elements as
 *   separate nodes so Settings stays reachable by screen reader;
 * - scrollable content **never overlaps** the chrome: the region's top edge *is*
 *   the chrome's bottom edge, at list top **and** after scrolling;
 * - BOUND-1: the artwork window always holds [ProgressCardRingSize] plus a
 *   standard gap either side of it of fully-opaque artwork below the shared 88 dp
 *   hand-off, and the window is **width-proportional** — derived, not measured;
 * - the progress card is the approved **horizontal** lockup: the fraction row
 *   first, the ring with the percentage **inside** it, the divider, then the
 *   breakdown — and it is one merged announcement;
 * - the empty card is centred in the scroll region, and the empty day collapses
 *   neither the atmosphere nor the chrome.
 *
 * Instrumented because the contract is a real composed layout: run with
 * `./gradlew :app:connectedDebugAndroidTest` (needs a connected device or
 * emulator; asserts bounds only, never pixels).
 */
class HomeCompositionTest {

    @get:Rule
    val compose = createComposeRule()

    /** The two fixed layers plus the progress card, composed as one host box. */
    private fun fixedLayers(
        darkMode: DarkModeSetting = DarkModeSetting.DARK,
        auroraState: AuroraState = AuroraState.PROGRESS,
        figureState: HeroFigureState = HeroFigureState.Fraction(done = 2, total = 3),
        encouragement: String? = Encouragement,
        width: Dp = 360.dp,
    ) {
        compose.setContent {
            MomentumTheme(darkMode = darkMode) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .testTag(HostTag),
                ) {
                    HomeAtmosphere(
                        state = auroraState,
                        reduceMotion = true,
                        windowWidth = width,
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag(HomeAtmosphereTag),
                    )
                    HomeTopChrome(
                        dateLabel = DateLabel,
                        greeting = Greeting,
                        greetingSupport = Support,
                        onOpenSettings = {},
                        onHeightChanged = {},
                        revealProgress = 1f,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .testTag(HomeChromeTag),
                    )
                    Box(Modifier.fillMaxSize()) {
                        HomeProgressCard(
                            figureState = figureState,
                            encouragement = encouragement,
                            completedCount = 2,
                            pendingCount = 1,
                            milestoneGlow = false,
                            reduceMotion = true,
                            ambientPulse = null,
                            revealProgress = 1f,
                        )
                    }
                }
            }
        }
        compose.waitForIdle()
    }

    /** The whole screen, so the scroll boundary can be measured at both extremes. */
    private fun screen(
        darkMode: DarkModeSetting = DarkModeSetting.DARK,
        remaining: Int = 2,
        completed: Int = 2,
    ) {
        compose.setContent {
            MomentumTheme(darkMode = darkMode) {
                TodayScreen(
                    state = success(remaining = remaining, completed = completed),
                    reduceMotion = true,
                    milestoneTick = 0,
                    onToggle = {},
                    onOpen = {},
                    onAdd = {},
                    onOpenSettings = {},
                )
            }
        }
        compose.waitForIdle()
    }

    private fun success(remaining: Int, completed: Int) = TodayUiState.Success(
        date = StartDate,
        dateLabel = DateLabel,
        greeting = UiText(R.string.today_greeting_evening),
        sections = TodaySections(
            remaining = (1..remaining).map { todayItem("Read $it", false) },
            completed = (1..completed).map { todayItem("Done $it", true) },
        ),
        encouragement = null,
        reduceMotionOverride = true,
    )

    private fun todayItem(title: String, completed: Boolean) = TodayItem(
        activity = Activity(title = title, startDate = StartDate),
        completed = completed,
        completionValue = 0.0,
        currentStreak = 0,
    )

    @Test
    fun theAtmosphereIsTheBackdropOfTheWholeContentArea() {
        fixedLayers()

        val host = compose.onNodeWithTag(HostTag).getBoundsInRoot()
        val atmosphere = compose.onNodeWithTag(HomeAtmosphereTag).getBoundsInRoot()

        assertTrue("the atmosphere must have real bounds", heightOf(atmosphere) > 0.dp)
        assertEquals(
            "the atmosphere must cover the whole content area, was $atmosphere vs $host",
            host,
            atmosphere,
        )
    }

    @Test
    fun theArtworkIsTheAtmospheresUpperRegionWithNoTopBand() {
        fixedLayers()

        val host = compose.onNodeWithTag(HostTag).getBoundsInRoot()
        val artwork = compose.onNodeWithTag(AtmosphereArtworkTag).getBoundsInRoot()

        assertTrue(
            "the artwork must start at the window top, was ${artwork.top} vs ${host.top}",
            (artwork.top - host.top).isWithin(1.dp),
        )
        assertTrue(
            "the artwork window must be artworkWindow(360.dp) tall, was $artwork",
            (heightOf(artwork) - artworkWindow(360.dp)).isWithin(1.dp),
        )
        // §4.8: the rendered window itself carries the artwork's intrinsic
        // ratio, so Fit shows the COMPLETE illustration with no crop.
        assertTrue(
            "the artwork window must preserve the intrinsic 1536/1024 ratio, " +
                "was ${widthOf(artwork)} × ${heightOf(artwork)}",
            abs(heightOf(artwork) / widthOf(artwork) - 1536f / 1024f) <= 0.01f,
        )
        assertTrue(
            "the artwork must be full-bleed horizontally, was $artwork vs $host",
            (artwork.left - host.left).isWithin(1.dp) &&
                (artwork.right - host.right).isWithin(1.dp),
        )
    }

    @Test
    fun theArtworkWindowIsDerivedNotMeasured() {
        // The window is a derived, width-proportional value, so BOUND-1 holds by
        // construction and in every state — there is no per-state measurement
        // left to get wrong. task.md §4.8: the derivation is the artwork's own
        // 1024 × 1536 intrinsic ratio, so the window carries the COMPLETE
        // artwork (1.5 × width), never the withdrawn 460/640 band.
        assertEquals(
            "the window must be the artwork's intrinsic ratio times the width",
            artworkWindow(360.dp),
            (ArtworkIntrinsicRatio * 360f).dp,
        )
        assertEquals(
            "the ratio is the artwork's own 1536/1024",
            1536f / 1024f,
            ArtworkIntrinsicRatio,
            0f,
        )
        assertEquals("the 360 dp window is 1.5 × width", 540f, artworkWindow(360.dp).value, 0.01f)
        assertEquals("the 320 dp window is 1.5 × width", 480f, artworkWindow(320.dp).value, 0.01f)
        assertEquals("the 412 dp window is 1.5 × width", 618f, artworkWindow(412.dp).value, 0.01f)
        assertTrue(
            "the window must grow with the width",
            artworkWindow(412.dp) > artworkWindow(360.dp) &&
                artworkWindow(360.dp) > artworkWindow(320.dp),
        )
    }

    @Test
    fun theOpaqueArtworkRegionClearsBoundOne() {
        // Measured as well as derived: whatever the density, the rendered window
        // must hold one ring plus a standard gap either side below the shared
        // hand-off.
        fixedLayers()

        val artwork = compose.onNodeWithTag(AtmosphereArtworkTag).getBoundsInRoot()
        val opaque = heightOf(artwork) - ArtworkHandOff
        assertTrue(
            "BOUND-1: the artwork must show at least one ring plus two standard " +
                "gaps of unobstructed, fully-opaque artwork, was $opaque",
            opaque >= ProgressCardRingSize + MomentumSpacing.standard * 2 - 1.dp,
        )
    }

    @Test
    fun theChromeIsFixedAndSitsOverTheArtwork() {
        fixedLayers()

        val artwork = compose.onNodeWithTag(AtmosphereArtworkTag).getBoundsInRoot()
        val chrome = compose.onNodeWithTag(HomeChromeTag).getBoundsInRoot()

        assertTrue(
            "the chrome must start at the window top, was ${chrome.top}",
            chrome.top <= 1.dp,
        )
        assertTrue("the chrome must have real height", heightOf(chrome) > 0.dp)
        // The chrome's copy sits over the artwork, in both themes and at both
        // font scales the sweep covers. At font scale 2.0 it may extend past the
        // window — that is accepted, so the assertion is overlap at all, never a
        // tuned ratio.
        assertTrue(
            "the chrome must overlap the artwork window, was " +
                "${chrome.top}..${chrome.bottom} vs ${artwork.top}..${artwork.bottom}",
            chrome.bottom > artwork.top && chrome.top < artwork.bottom,
        )
    }

    @Test
    fun theChromeKeepsItsElementsUnmergedSoSettingsStaysReachable() {
        fixedLayers()

        // A merged chrome would announce only its text and swallow the button,
        // making a persistent control unreachable by screen reader.
        compose.onNodeWithText(DateLabel, useUnmergedTree = true).assertExists()
        compose.onNodeWithText(Greeting, useUnmergedTree = true).assertExists()
        compose.onNodeWithText(Support, useUnmergedTree = true).assertExists()
        val settings = compose.onNodeWithContentDescription("Settings")
        settings.assertExists()
        assertTrue(
            "Settings must clear the 48 dp minimum target",
            heightOf(settings.getBoundsInRoot()) >= 48.dp,
        )
    }

    @Test
    fun scrollableContentNeverOverlapsTheChrome() {
        screen()

        val chrome = compose.onNodeWithTag(HomeChromeTag).getBoundsInRoot()
        val atTop = compose.onNodeWithTag(HomeScrollRegionTag).getBoundsInRoot()

        assertTrue(
            "the region's top edge must be the chrome's bottom edge, was " +
                "${atTop.top} vs ${chrome.bottom}",
            (atTop.top - chrome.bottom).isWithin(1.dp),
        )

        // And it must still hold after the list has been scrolled to its end:
        // nothing scrolls up underneath the chrome, because the region is
        // physically bounded rather than merely padded.
        compose.onNodeWithTag(HomeScrollRegionTag).performTouchInput { swipeUp() }
        compose.waitForIdle()

        val atEnd = compose.onNodeWithTag(HomeScrollRegionTag).getBoundsInRoot()
        assertEquals(
            "scrolling must not move the region at all, was $atEnd vs $atTop",
            atTop,
            atEnd,
        )
    }

    @Test
    fun scrollingDoesNotMoveTheArtworkOrTheChrome() {
        screen()

        val artworkBefore = compose.onNodeWithTag(AtmosphereArtworkTag).getBoundsInRoot()
        val chromeBefore = compose.onNodeWithTag(HomeChromeTag).getBoundsInRoot()

        compose.onNodeWithTag(HomeScrollRegionTag).performTouchInput { swipeUp() }
        compose.waitForIdle()

        assertEquals(
            "the artwork is the atmosphere's upper region and must not scroll, " +
                "was ${compose.onNodeWithTag(AtmosphereArtworkTag).getBoundsInRoot()}",
            artworkBefore,
            compose.onNodeWithTag(AtmosphereArtworkTag).getBoundsInRoot(),
        )
        assertEquals(
            "the chrome is persistent and must not scroll, was " +
                "${compose.onNodeWithTag(HomeChromeTag).getBoundsInRoot()}",
            chromeBefore,
            compose.onNodeWithTag(HomeChromeTag).getBoundsInRoot(),
        )
    }

    @Test
    fun theProgressCardIsTheApprovedHorizontalLockup() {
        fixedLayers()

        val fraction = compose.onNodeWithText(Fraction, useUnmergedTree = true).getBoundsInRoot()
        val suffix = compose.onNodeWithText("items done", useUnmergedTree = true).getBoundsInRoot()
        val percent = compose.onNodeWithText("67%", useUnmergedTree = true).getBoundsInRoot()
        val unit = compose.onNodeWithText("done", useUnmergedTree = true).getBoundsInRoot()
        val completed = compose.onNodeWithText("2 Completed", useUnmergedTree = true).getBoundsInRoot()
        val pending = compose.onNodeWithText("1 Pending", useUnmergedTree = true).getBoundsInRoot()

        // Text block LEFT, ring RIGHT: the fraction row sits above the ring, not
        // inside it — the vertical TASK-017 arrangement is gone.
        assertTrue(
            "the fraction must sit left of the ring, was ${fraction.right} vs ${percent.left}",
            fraction.right < percent.left,
        )
        assertTrue(
            "the suffix shares the fraction's baseline row, was ${suffix.top} vs ${fraction.top}",
            (suffix.top - fraction.top).isWithin(6.dp),
        )
        // The percentage is INSIDE the ring, and the ring is invariant.
        assertTrue(
            "the percentage must fit inside the ring, was ${widthOf(percent)}",
            widthOf(percent) < ProgressCardRingSize,
        )
        assertTrue(
            "the percentage must fit inside the ring, was ${heightOf(percent)}",
            heightOf(percent) < ProgressCardRingSize,
        )
        assertTrue(
            "the ring's unit must read under the percentage, was ${unit.top} vs ${percent.bottom}",
            unit.top >= percent.bottom - 1.dp,
        )
        // The breakdown reads BENEATH the ring, on one row, split to the edges.
        assertTrue(
            "the breakdown must read below the ring, was ${completed.top} vs ${percent.bottom}",
            completed.top > percent.bottom,
        )
        assertTrue(
            "the two breakdown counts must sit on one row, was ${completed.top} vs ${pending.top}",
            (completed.top - pending.top).isWithin(2.dp),
        )
        assertTrue(
            "the two breakdown counts must spread to the edges, was ${completed.left} vs ${pending.right}",
            completed.left < pending.right,
        )
        // One merged announcement for the whole group; the ring is decorative.
        compose.onNodeWithContentDescription(Description).assertExists()
        compose.onNodeWithText(Fraction).assertDoesNotExist()
        compose.onNodeWithText("items done").assertDoesNotExist()
        compose.onNodeWithText("67%").assertDoesNotExist()
    }

    @Test
    fun theEmptyDayCollapsesNeitherTheAtmosphereNorTheChrome() {
        screen(remaining = 0, completed = 0)

        val atmosphere = compose.onNodeWithTag(HomeAtmosphereTag).getBoundsInRoot()
        val chrome = compose.onNodeWithTag(HomeChromeTag).getBoundsInRoot()
        val artwork = compose.onNodeWithTag(AtmosphereArtworkTag).getBoundsInRoot()

        assertTrue("the atmosphere must survive an empty day", heightOf(atmosphere) > 0.dp)
        assertTrue("the chrome must survive an empty day", heightOf(chrome) > 0.dp)
        // Derived from the screen's own width (the same `maxWidth` TodayScreen
        // passes as `windowWidth`), so the invariant holds at any device width —
        // the window depends on width and state, never on the day's contents.
        val expected = artworkWindow(widthOf(atmosphere))
        assertTrue(
            "the artwork window must be identical on an empty day, was $artwork " +
                "vs $expected",
            (heightOf(artwork) - expected).isWithin(1.dp),
        )
        compose.onNodeWithText(DateLabel).assertExists()
        compose.onNodeWithText(Greeting).assertExists()
        compose.onNodeWithContentDescription("Settings").assertExists()
        // §8.7: `Hidden` is not composed at all, so it reserves nothing.
        compose.onNodeWithText("items done").assertDoesNotExist()
    }

    @Test
    fun theEmptyCardIsCentredInTheScrollRegion() {
        screen(remaining = 0, completed = 0)

        val region = compose.onNodeWithTag(HomeScrollRegionTag).getBoundsInRoot()
        val card = compose.onNodeWithText(EmptyTitle).getBoundsInRoot()

        val above = card.top - region.top
        val below = region.bottom - card.bottom
        // The card is optically centred in the region the chrome leaves over: the
        // list's own 96 dp FAB reserve is the only asymmetry, so that is the
        // tolerance rather than a hand-tuned spacer value.
        assertTrue(
            "the empty card must be optically centred in the scroll region, " +
                "was $above above and $below below",
            (above - below).isWithin(EmptyDayReserve),
        )
        assertTrue(
            "the empty card must clear the chrome's bottom edge, was ${card.top}",
            card.top >= chromeBottom() - 1.dp,
        )
        // §4.1.1 / ADR-48 Amendment Note 2: no CTA on the card, and the FAB stays
        // the single creation affordance.
        compose.onNodeWithText(EmptyAction).assertDoesNotExist()
        compose.onNodeWithContentDescription("Quick Add").assertIsDisplayed()
    }

    private fun chromeBottom(): Dp =
        compose.onNodeWithTag(HomeChromeTag).getBoundsInRoot().bottom

    private companion object {
        val StartDate = LocalDate.of(2026, 9, 30)
        const val HostTag = "homeHost"
        const val DateLabel = "FRI, SEP 25"
        const val Greeting = "Good evening"
        const val Support = "Ready to conquer your day?"
        const val Description = "2 of 3 completed today"
        /** §4.2 C + §10.8 Ruling B: the visible fraction carries the denominator. */
        const val Fraction = "2 of 3"
        const val Encouragement = "Next up: Read"
        const val EmptyTitle = "Nothing scheduled today"
        const val EmptyAction = "Add an activity"

        /**
         * The list's tail reserve, restated as the tolerance for "optically
         * centred": the region ends one [EmptyDayBottomReserve] of FAB clearance
         * above the content area's bottom, so that reserve — not a spacer — is
         * the only asymmetry between the space above and below the card.
         */
        val EmptyDayReserve = 96.dp
    }
}

/** Distance-independent comparison, tolerant of sub-pixel layout rounding. */
private fun Dp.isWithin(other: Dp, tolerance: Dp = 1.dp): Boolean =
    abs((value - other.value)) <= tolerance.value

private fun heightOf(rect: DpRect): Dp = rect.bottom - rect.top

private fun widthOf(rect: DpRect): Dp = rect.right - rect.left
