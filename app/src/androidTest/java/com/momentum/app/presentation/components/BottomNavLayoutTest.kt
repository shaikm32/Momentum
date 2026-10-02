package com.momentum.app.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.dp
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.presentation.theme.MomentumHeights
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumTheme
import kotlin.math.abs
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * ADR-49 Amendment Note 1's bounds-level obligations (§8.18.6 item 2). The
 * navigation bar's **visual** is the approved reference's and its **behaviour**
 * is Momentum's, so these assertions are geometry only: the bar's own margins and
 * every tab's touch target on **both** axes.
 *
 * No pixel or screenshot claim is made — `adb` is unavailable in the authoring
 * environment, so no instrumented suite has been executed here and the device
 * matrix is the human's and the Reviewer's.
 */
class BottomNavLayoutTest {

    @get:Rule
    val compose = createComposeRule()

    /** The bar exactly as `MomentumNavGraph` composes it: one slot, four tabs. */
    @Composable
    private fun bar(longLabels: Boolean = false) {
        MomentumTheme(darkMode = DarkModeSetting.LIGHT) {
            Box(Modifier.fillMaxWidth().testTag(SlotTag)) {
                LiquidGlassBottomBar(Modifier.fillMaxWidth().testTag(BarTag)) {
                    val labels = if (longLabels) {
                        listOf("Home — long", "Habits — long", "Tasks — long", "Insights — long")
                    } else {
                        listOf("Home", "Habits", "Tasks", "Insights")
                    }
                    labels.forEachIndexed { index, label ->
                        LiquidGlassTab(
                            icon = if (index == 0) Icons.Rounded.Home else Icons.Outlined.Home,
                            label = label,
                            selected = index == 0,
                            onClick = {},
                        )
                    }
                }
            }
        }
    }

    private fun render(longLabels: Boolean = false) {
        compose.setContent { bar(longLabels) }
        compose.waitForIdle()
    }

    private fun tabTargets(): List<Rect> = compose
        .onAllNodes(
            hasClickAction() and SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab),
        )
        .fetchSemanticsNodes()
        .map { node -> node.boundsInRoot }

    @Test
    fun thereIsExactlyOneTabTargetPerDestination() {
        render()

        compose
            .onAllNodes(
                hasClickAction() and SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab),
            )
            .assertCountEquals(4)
    }

    @Test
    fun everyTabClearsThe48DpMinimumOnBothAxes() {
        render()

        val targets = tabTargets()
        assertTrue("the four tabs must have real bounds", targets.size == 4)
        targets.forEach { rect ->
            assertTrue(
                "a tab must clear the 48 dp width floor, was ${rect.right - rect.left}",
                rect.width >= MomentumHeights.controlMin.value,
            )
            assertTrue(
                "a tab must clear the 48 dp height floor, was ${rect.height}",
                rect.height >= MomentumHeights.controlMin.value,
            )
        }
    }

    @Test
    fun theBarGrowsRatherThanClipsWhenItsLabelsDo() {
        render()
        val atScaleOne = heightOf(compose.onNodeWithTag(BarTag).getBoundsInRoot())

        // Longer labels are the observable proxy for a larger font scale: the
        // approved bar is `heightIn(min = 64.dp)`, never `height(64.dp)`.
        render(longLabels = true)
        val grown = heightOf(compose.onNodeWithTag(BarTag).getBoundsInRoot())

        assertTrue(
            "the bar must grow with its content, never clip: $atScaleOne -> $grown",
            grown >= atScaleOne,
        )
        assertTrue(
            "the grown bar must still clear the 64 dp reference height, was $grown",
            grown >= NavBarHeightForTest,
        )
    }

    @Test
    fun theBarClearsItsOwnReferenceSideMargins() {
        render()

        val slot = compose.onNodeWithTag(SlotTag).getBoundsInRoot()
        val bar = compose.onNodeWithTag(BarTag).getBoundsInRoot()

        assertTrue(
            "the bar's own 16 dp side margin must be honoured, was $bar vs $slot",
            abs((bar.left - slot.left).value - MomentumSpacing.standard.value) <= 1f &&
                abs((slot.right - bar.right).value - MomentumSpacing.standard.value) <= 1f,
        )
    }

    private fun heightOf(rect: DpRect) = rect.bottom - rect.top

    private companion object {
        const val SlotTag = "bottomBarSlot"
        const val BarTag = "bottomBar"

        /** The reference's `h-16`, restated as the floor the bar must reach. */
        val NavBarHeightForTest = 64.dp
    }
}
