package com.momentum.app.presentation.screens.today

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.presentation.theme.MomentumTheme
import org.junit.Rule
import org.junit.Test

/**
 * Plain, state-driven UI tests for the horizontal progress card's branches
 * (ADR-48 Amendment Note 5): Fraction / Complete / Hidden × encouragement
 * ON/OFF, × reduced motion. Controlled state with fixed strings, no ViewModel
 * and no DI.
 *
 * The text is asserted in the **unmerged** tree where the card owns a
 * `clearAndSetSemantics` description; the merged tree deliberately exposes
 * exactly ONE progress announcement, and the encouragement keeps its own.
 */
class HomeProgressStateTest {

    @get:Rule
    val compose = createComposeRule()

    private fun render(
        figureState: HeroFigureState,
        encouragement: String?,
        reduceMotion: Boolean = true,
        milestoneGlow: Boolean = false,
        completedCount: Int = 2,
        pendingCount: Int = 1,
    ) {
        compose.setContent {
            MomentumTheme(darkMode = DarkModeSetting.LIGHT) {
                Box(Modifier.fillMaxSize()) {
                    HomeProgressCard(
                        figureState = figureState,
                        encouragement = encouragement,
                        completedCount = completedCount,
                        pendingCount = pendingCount,
                        milestoneGlow = milestoneGlow,
                        reduceMotion = reduceMotion,
                        ambientPulse = null,
                        revealProgress = 1f,
                    )
                }
            }
        }
    }

    @Test
    fun incompleteShowsTheFractionRowTheRingAndTheStateSpecificLine() {
        render(HeroFigureState.Fraction(done = 2, total = 3), Encouragement)

        compose.onNodeWithText(Done, useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText(Suffix, useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("67%", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText(Unit, useUnmergedTree = true).assertIsDisplayed()
        // The line sits outside the merged group, so it keeps its own
        // announcement.
        compose.onNodeWithText(Encouragement).assertIsDisplayed()
        compose.onNodeWithContentDescription(Description).assertIsDisplayed()
    }

    @Test
    fun theCardIsOneMergedAnnouncement() {
        render(HeroFigureState.Fraction(done = 2, total = 3), encouragement = null)

        // One merged description for the whole group…
        compose.onNodeWithContentDescription(Description).assertIsDisplayed()
        // …and neither the fraction, the suffix, the percentage nor the unit is a
        // second announcement in the merged tree.
        compose.onNodeWithText(Done).assertDoesNotExist()
        compose.onNodeWithText(Suffix).assertDoesNotExist()
        compose.onNodeWithText("67%").assertDoesNotExist()
        compose.onNodeWithText(Unit).assertDoesNotExist()
    }

    @Test
    fun theBreakdownReadsBothCounts() {
        render(HeroFigureState.Fraction(done = 2, total = 3), encouragement = null)

        compose.onNodeWithText("2 Completed", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("1 Pending", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun completeReplacesTheFractionRowWithTheStatementAndFillsTheRing() {
        render(
            HeroFigureState.Complete(done = 3, total = 3),
            encouragement = null,
            completedCount = 3,
            pendingCount = 0,
        )
        compose.onNodeWithText(Statement, useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("100%", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText(Done, useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithText(Suffix, useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithContentDescription(CompleteDescription).assertIsDisplayed()
    }

    @Test
    fun hiddenRendersNoFractionNoRingAndNoStatement() {
        render(HeroFigureState.Hidden, encouragement = null)

        compose.onNodeWithText(Statement, useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithText(Done, useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithText("0%", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun encouragementOffShowsNoLine() {
        render(HeroFigureState.Fraction(done = 2, total = 3), encouragement = null)

        compose.onNodeWithText(Encouragement).assertDoesNotExist()
        compose.onNodeWithContentDescription(Description).assertIsDisplayed()
    }

    @Test
    fun reducedMotionRendersTheCompletionStatementWithoutTheTransient() {
        render(
            figureState = HeroFigureState.Complete(done = 3, total = 3),
            encouragement = null,
            reduceMotion = true,
            milestoneGlow = true,
        )

        // The statement is instant and still correct with the glow skipped.
        compose.onNodeWithText(Statement, useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithContentDescription(CompleteDescription).assertIsDisplayed()
    }

    private companion object {
        // §4.2 C + §10.8 Ruling B: the visible metric is `X of Y`, not `X`.
        const val Done = "2 of 3"
        const val Suffix = "items done"
        const val Unit = "done"
        const val Description = "2 of 3 completed today"
        const val CompleteDescription = "3 of 3 completed today"
        const val Statement = "Today is complete"
        const val Encouragement = "Next up: Read"
    }
}
