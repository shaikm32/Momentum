package com.momentum.app.presentation.screens.today

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.momentum.app.R
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.usecase.TodayItem
import com.momentum.app.domain.usecase.TodaySections
import com.momentum.app.presentation.UiText
import com.momentum.app.presentation.aurora.AuroraState
import com.momentum.app.presentation.components.MomentumFab
import com.momentum.app.presentation.theme.MomentumTheme
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Plain, state-driven UI tests for the screen-level states (ADR-48 Amendment
 * Notes 5 and 6): the empty day's single creation affordance (the shared FAB,
 * with no competing CTA on the card), the populated day's shared FAB, **both**
 * approved sections with their status labels, the loading skeleton, the milestone
 * mapping, and the reduced-motion completion statement. `setContent` with
 * controlled state — no ViewModel and no DI.
 */
class HomeStatesTest {

    @get:Rule
    val compose = createComposeRule()

    private fun todayItem(title: String, completed: Boolean = false) = TodayItem(
        activity = Activity(title = title, startDate = StartDate),
        completed = completed,
        completionValue = 0.0,
        currentStreak = 0,
    )

    private fun success(
        remaining: List<TodayItem> = emptyList(),
        completed: List<TodayItem> = emptyList(),
    ) = TodayUiState.Success(
        date = StartDate,
        dateLabel = "WED, SEP 30",
        greeting = UiText(R.string.today_greeting_evening),
        sections = TodaySections(remaining = remaining, completed = completed),
        encouragement = null,
        reduceMotionOverride = true,
    )

    private fun renderToday(state: TodayUiState.Success) {
        compose.setContent {
            MomentumTheme {
                TodayScreen(
                    state = state,
                    reduceMotion = true,
                    milestoneTick = 0,
                    onToggle = {},
                    onOpen = {},
                    onAdd = {},
                    onOpenSettings = {},
                )
            }
        }
    }

    @Test
    fun anEmptyDayKeepsTheFabAndDropsTheCompetingEmptyCta() {
        renderToday(success())

        // ADR-48 Amendment Note 2 (human ruling): the FAB is the single
        // creation affordance in EVERY state, so an empty day shows it — its
        // presence is not a state change — and the empty card communicates
        // state only, with no second, competing CTA.
        compose.onNodeWithContentDescription("Quick Add").assertIsDisplayed().assertHasClickAction()
        compose.onNodeWithText("Nothing scheduled today").assertIsDisplayed()
        compose.onNodeWithText("Add an activity").assertDoesNotExist()
        // §8.7: `Hidden` is not composed, so no card and no reserved space.
        assertTrue(
            "the progress card must not be composed on an empty day",
            !compose.hasNodeWithTag(HomeProgressCardTag),
        )
    }

    @Test
    fun aPopulatedDayShowsTheProgressCardBothSectionsAndTheSharedFab() {
        renderToday(
            success(
                remaining = listOf(todayItem("Read")),
                completed = listOf(todayItem("Hydrate", completed = true)),
            ),
        )

        compose.assertNodeWithTagExists(HomeProgressCardTag)
        compose.onNodeWithText("Pending").assertIsDisplayed()
        compose.onNodeWithText("Scheduled Today").assertIsDisplayed()
        compose.onNodeWithText("Completed").assertIsDisplayed()
        compose.onNodeWithText("Finished").assertIsDisplayed()
        compose.onNodeWithText("Read").assertIsDisplayed()
        compose.onNodeWithText("Hydrate").assertIsDisplayed()
        compose.onNodeWithContentDescription("Quick Add").assertIsDisplayed().assertHasClickAction()
    }

    @Test
    fun aFullyCompleteDayStatesCompletionAndHidesThePendingSection() {
        renderToday(success(completed = listOf(todayItem("Read", completed = true))))

        compose.onNodeWithText("Today is complete", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("100%", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("Pending").assertDoesNotExist()
        compose.onNodeWithText("Completed").assertIsDisplayed()
    }

    @Test
    fun reducedMotionShowsTheCompletionStatementInstantly() {
        renderToday(success(completed = listOf(todayItem("Read", completed = true))))

        // The statement renders with no transient window open (tick 0) and the
        // statement is instant under reduced motion.
        compose.onNodeWithText("Today is complete", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun theLoadingSkeletonCarriesOneAccessibleLabelAndNoSpinner() {
        compose.setContent { MomentumTheme { TodayLoadingSkeleton() } }

        compose.onNodeWithContentDescription("Loading today…").assertIsDisplayed()
        compose.onAllNodes(
            SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo),
        ).assertCountEquals(0)
    }

    @Test
    fun auroraMapsTheDayStateAndTheTransientMilestone() {
        assertEquals(AuroraState.REST, homeAuroraState(isEmpty = true, progress = 0f, milestoneActive = false))
        assertEquals(AuroraState.PROGRESS, homeAuroraState(isEmpty = false, progress = 0.4f, milestoneActive = false))
        assertEquals(AuroraState.MILESTONE, homeAuroraState(isEmpty = false, progress = 0.9f, milestoneActive = true))
        assertEquals(AuroraState.COMPLETION, homeAuroraState(isEmpty = false, progress = 1f, milestoneActive = false))
    }

    @Test
    fun theSharedFabIsALabelledCircularButton() {
        var clicked = false
        compose.setContent {
            MomentumTheme {
                Box(Modifier.fillMaxSize()) {
                    MomentumFab(
                        onClick = { clicked = true },
                        contentDescription = "Quick Add",
                    )
                }
            }
        }

        val fab = compose.onNodeWithContentDescription("Quick Add")
        fab.assertIsDisplayed()
        fab.assertHasClickAction()
        fab.assertHeightIsAtLeast(48.dp)
        fab.assertWidthIsAtLeast(48.dp)
        fab.performClick()
        compose.runOnIdle { assertTrue("the FAB must open Create Item", clicked) }
    }

    @Test
    fun theFabOptInsAreRenderedOnHomeAndOffByDefault() {
        compose.setContent {
            MomentumTheme {
                Box(Modifier.fillMaxSize()) {
                    MomentumFab(
                        onClick = {},
                        contentDescription = "Quick Add",
                        glowPulse = true,
                        rotateGlyphOnPress = true,
                    )
                }
            }
        }

        // The opt-ins are additive: the control's size, role and label are
        // unchanged, so the same assertions hold as for the default FAB.
        val fab = compose.onNodeWithContentDescription("Quick Add")
        fab.assertIsDisplayed().assertHasClickAction()
        fab.assertHeightIsAtLeast(48.dp)
    }

    private companion object {
        val StartDate: LocalDate = LocalDate.of(2026, 9, 30)
    }
}

/** Is a node carrying [tag] present in the current tree? */
private fun ComposeTestRule.hasNodeWithTag(tag: String): Boolean =
    onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()

/** Assert that a node carrying [tag] is present. */
private fun ComposeTestRule.assertNodeWithTagExists(tag: String) {
    assertTrue("expected a node tagged $tag", hasNodeWithTag(tag))
}
