package com.momentum.app.presentation.components

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * ADR-49 Amendment Note 1 (human ruling 2026-10-01 §4.7 r1): the bottom
 * navigation is re-rendered to the approved Stitch visual **in place**, for all
 * four destinations — **VISUAL = the reference, BEHAVIOUR = Momentum**.
 *
 * There is exactly one implementation and exactly one call site, so the four
 * destinations cannot desynchronise and no Home-only fork is possible. These are
 * source-level guards in the established `HomeAtmosphereAssetsTest` /
 * `QuickAddTest` style: they pin the approved reference's numbers and the
 * retained Momentum behaviour, and they pin the **deviations** the plan records
 * so none of them is silently "corrected" later.
 */
class BottomNavStitchVisualTest {

    private fun read(path: String): String {
        val file = File(path)
        assertTrue("missing source: ${file.absolutePath}", file.isFile)
        return file.readText()
    }

    /** Code only: comments must never satisfy an assertion about rendered UI. */
    private fun codeOf(text: String): String = text
        .lines()
        .filterNot { it.trimStart().startsWith("//") || it.trimStart().startsWith("*") }
        .joinToString("\n")

    private val glass = read("src/main/java/com/momentum/app/presentation/components/LiquidGlass.kt")
    private val glassCode = codeOf(glass)
    private val nav = read("src/main/java/com/momentum/app/presentation/navigation/MomentumNavGraph.kt")

    @Test
    fun `the bar is the references floating pill at its exact geometry`() {
        assertTrue(
            "rounded-full with the nav-scoped fill opt-in (ADR-49 AN6)",
            glassCode.contains(".liquidGlass(CircleShape, GlassLevel.HERO, fillAlpha = NavBarFillAlpha)"),
        )
        assertFalse(
            "the retired 26 dp radius must stay gone",
            glassCode.contains("RoundedCornerShape(26.dp)"),
        )
        assertTrue("the bar's own height is the reference's h-16", glassCode.contains("private val NavBarHeight = 64.dp"))
        assertTrue(
            "a floor, never a clip: it grows with font scale",
            glassCode.contains(".heightIn(min = NavBarHeight)"),
        )
        assertTrue("the horizontal padding is px-space-md", glassCode.contains(".padding(horizontal = MomentumSpacing.standard)"))
        // The bar's own block: nav-bar padding to the end of the composable.
        val bar = glassCode.substringAfter(".heightIn(min = NavBarHeight)").take(220)
        assertTrue("the bar's padding block is present", bar.contains(".padding(horizontal = MomentumSpacing.standard)"))
        assertFalse(
            "the reference's bar row has no vertical padding",
            bar.substringAfter(".padding(horizontal = MomentumSpacing.standard)").contains("vertical ="),
        )
        assertTrue("justify-around", glassCode.contains("horizontalArrangement = Arrangement.SpaceAround"))
        assertTrue("items-center", glassCode.contains("verticalAlignment = Alignment.CenterVertically"))
    }

    @Test
    fun `the tab keeps Momentum's behaviour and the references sizes`() {
        assertTrue("the reference's active min width", glassCode.contains("ActiveTabMinWidth = 62.dp"))
        assertTrue("the reference's inactive min width", glassCode.contains("InactiveTabMinWidth = 56.dp"))
        assertTrue(
            "both states keep the 48 dp floor (deviation 5)",
            glassCode.contains("minHeight = MomentumHeights.controlMin"),
        )
        assertFalse("the reference's 44 px inactive height is below the floor", glassCode.contains("44.dp"))
        assertTrue("py-1", glassCode.contains(".padding(vertical = MomentumSpacing.micro)"))
        assertTrue("mt-0.5", glassCode.contains("Arrangement.spacedBy(TabIconGap)"))
        assertTrue("mt-0.5 is 2 px", glassCode.contains("private val TabIconGap = 2.dp"))
        assertTrue("the reference's active icon is 22 dp", glassCode.contains("ActiveTabIconSize = 22.dp"))
        assertTrue("the reference's inactive icon is 23 dp (deviation 14)", glassCode.contains("InactiveTabIconSize = 23.dp"))
        assertTrue("the inactive icon wrapper is a 36 dp circle", glassCode.contains("InactiveIconSlotSize = 36.dp"))
        assertTrue("Role.Tab is retained", glassCode.contains("role = Role.Tab"))
        assertTrue("the icon's label is retained", glassCode.contains("contentDescription = label"))
        assertTrue("press feedback is retained", glassCode.contains("glassPress(interaction)"))
        assertTrue(
            "Momentum's own 18 dp press/clip shape stays (not a reference surface)",
            glassCode.contains("private val TabPressRadius = 18.dp"),
        )
    }

    @Test
    fun `the active capsule is the reference two-stop gradient with its own alphas`() {
        assertTrue("px-4 py-1.5 on a full circle", glassCode.contains("horizontal = MomentumSpacing.standard") && glassCode.contains("vertical = CapsulePadding"))
        assertTrue("py-1.5 is 6 px", glassCode.contains("private val CapsulePadding = 6.dp"))
        assertTrue("the first stop's mix", glassCode.contains("lerp(c.auroraBlue, c.surface, CapsuleStopOneMix)"))
        assertTrue("the second stop's mix", glassCode.contains("lerp(c.auroraLavender, c.surface, CapsuleStopTwoMix)"))
        assertTrue("the reference's exact alphas", glassCode.contains("CapsuleStopOneAlpha = 0.75f") && glassCode.contains("CapsuleStopTwoAlpha = 0.85f"))
        assertTrue("135 degrees", glassCode.contains("start = Offset.Zero") && glassCode.contains("end = Offset.Infinite"))
        // The hairline is a literal ALPHA (deviation 10), never a literal colour
        // or a new token — the same construction this file already uses for its
        // light-mode top streak. ADR-49 Amendment Note 6 (human ruling
        // 2026-10-02 §8.20): Light resolves it against textPrimary (white
        // measured 1.11:1 on the near-white Light capsule); Dark keeps white.
        assertTrue(
            "Light capsule border resolves against the ink token",
            glassCode.contains("c.textPrimary.copy(alpha = CapsuleBorderAlpha * capsuleProgress)"),
        )
        assertTrue(
            "Dark capsule border keeps the reference white",
            glassCode.contains("Color.White.copy(alpha = CapsuleBorderAlpha * capsuleProgress)"),
        )
        assertTrue("the border is 1 px", glassCode.contains("private val CapsuleBorder = 1.dp"))
        assertTrue("the border's alpha", glassCode.contains("CapsuleBorderAlpha = 0.9f"))
        // The glow spot: the same substitution MomentumFab and Modifier.liquidGlass
        // already document (#6366F1 is not a Momentum token).
        assertTrue("the glow spot", glassCode.contains("c.primary.copy(alpha = CapsuleSpotAlpha * capsuleProgress)"))
        assertTrue("0 2px 10px", glassCode.contains("CapsuleElevation = 2.dp") && glassCode.contains("CapsuleSpotAlpha = 0.2f"))
        assertTrue(
            "the capsule's alphas are SCALED by the progress, never lerped toward transparent",
            glassCode.contains("it.copy(alpha = it.alpha * progress)"),
        )
    }

    @Test
    fun `the retired tab treatment stays gone`() {
        assertFalse("the selected 1.06 scale is removed", glassCode.contains("1.06f"))
        assertFalse("the whole-tab background plate is superseded", glassCode.contains("tabSelectedBg"))
        assertFalse("the whole-tab border plate is superseded", glassCode.contains("tabSelectedBorder"))
        assertFalse("weight(1f) is removed so SpaceAround has room", glassCode.contains(".weight(1f)"))
        assertFalse("the old label token is replaced", glassCode.contains("MaterialTheme.typography.labelMedium"))
        // Deviation 15: hover does not exist on touch.
        assertFalse("no hover treatment", glassCode.contains("hover"))
    }

    @Test
    fun `no blur layer is introduced anywhere in the glass file`() {
        listOf("backdrop-filter", "RenderEffect", "Modifier.blur", "renderEffect").forEach { token ->
            assertFalse("a blur layer is not adopted: $token", glassCode.contains(token))
        }
        assertTrue("the bar keeps the existing floating-layer material", glassCode.contains("GlassLevel.HERO"))
    }

    @Test
    fun `the labels use the approved micro token with the approved weights`() {
        assertTrue("the nav labels reuse MomentumLabelMicro", glassCode.contains("style = MomentumLabelMicro"))
        assertTrue("Bold when active", glassCode.contains("FontWeight.Bold else FontWeight.SemiBold"))
        assertTrue(
            "the retained ink animation targets the text tokens (deviation 7)",
            glassCode.contains("if (selected) c.textPrimary else c.textSecondary"),
        )
        assertEquals(
            "exactly one added float: the capsule transition",
            1,
            glassCode.split("animateFloatAsState(").size - 1 - 1,
        )
        assertTrue(glassCode.contains("label = \"tabCapsule\""))
        assertTrue(
            "the colour animation is retained for both the icon and the label",
            glassCode.contains("label = \"tabIconTint\"") && glassCode.contains("label = \"tabLabelColor\""),
        )
    }

    @Test
    fun `the nav slot keeps its safe area and its behaviour`() {
        // Only the three margin values changed: left-4 / right-4 / bottom-5.
        assertTrue(
            "the reference's 16 dp side margins",
            nav.contains("start = MomentumSpacing.standard") && nav.contains("end = MomentumSpacing.standard"),
        )
        assertTrue("the reference's 20 dp bottom margin", nav.contains("bottom = MomentumSpacing.screen"))
        assertFalse(
            "the retired margins must stay gone",
            nav.contains("bottom = MomentumSpacing.compact"),
        )
        // The safe area is unchanged: WindowInsets.navigationBars.only(Bottom) is
        // Momentum's platform equivalent of the reference's safe-area inset.
        assertTrue(
            "the navigation-bar inset is retained",
            nav.contains("WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)"),
        )
        // Behaviour: Nav3 routing, the four destinations, selectTab and the
        // showBar gate are untouched.
        assertTrue("the four destinations are unchanged", nav.contains("TopLevelDestination(Routes.Today, R.string.nav_home, Icons.Rounded.Home, Icons.Outlined.Home)"))
        assertEquals("exactly four top-level destinations", 4, nav.split("TopLevelDestination(Routes.").size - 1)
        assertTrue("tab selection is unchanged", nav.contains("onClick = { backStack.selectTab(dest.key) }"))
        assertTrue("the bar gate is unchanged", nav.contains("val showBar = Destinations.any { it.key == currentKey }"))
        assertTrue("Scaffold is unchanged", nav.contains("Scaffold(") && nav.contains("contentWindowInsets = contentWindowInsets"))
        assertTrue(
            "one call site each, so no fork is possible",
            nav.split("LiquidGlassBottomBar {").size - 1 == 1 && nav.split("LiquidGlassTab(").size - 1 == 1,
        )
        assertTrue(
            "every create-item entry point still routes through the FAB",
            nav.split("showCreateItem = true").size - 1 >= 4,
        )
    }
}
