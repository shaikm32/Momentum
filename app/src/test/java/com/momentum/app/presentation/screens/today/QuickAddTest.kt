package com.momentum.app.presentation.screens.today

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * ADR-47 §6 / refinement R9–R10 / ADR-48 Amendment Notes 2 and 4: one shared
 * `MomentumFab` replaces the two divergent private FABs — Home's glass
 * `QuickAddFab` and Habits' `AddHabitFab` — with one material and the
 * `prominent` glyph. Home opens Create Item from it and renders it in **every**
 * state, so the empty state's CTA is not a second, competing affordance. The
 * material is now glass-over-`primary` at the design system's own
 * floating-layer alpha, so the Aurora reads through the disc — an approved
 * shared change, which is why Habits' FAB is translucent too.
 */
class QuickAddTest {

    private fun read(path: String): String {
        val file = File(path)
        assertTrue("missing source: ${file.absolutePath}", file.isFile)
        return file.readText()
    }

    /** Comments must never satisfy an assertion about rendered UI. */
    private fun codeOf(text: String): String = text
        .lines()
        .filterNot { it.trimStart().startsWith("//") || it.trimStart().startsWith("*") }
        .joinToString("\n")

    private val today = read("src/main/java/com/momentum/app/presentation/screens/today/TodayScreen.kt")
    private val habits = read("src/main/java/com/momentum/app/presentation/screens/habits/HabitsScreen.kt")
    private val fab = read("src/main/java/com/momentum/app/presentation/components/MomentumFab.kt")
    private val nav = read("src/main/java/com/momentum/app/presentation/navigation/MomentumNavGraph.kt")

    private fun count(haystack: String, needle: String): Int =
        haystack.split(needle).size - 1

    @Test
    fun `Home and Habits share the one MomentumFab`() {
        assertTrue("Home must use the shared FAB", codeOf(today).contains("MomentumFab("))
        assertTrue("Habits must use the shared FAB", codeOf(habits).contains("MomentumFab("))
        assertFalse("the divergent Habits FAB must be gone", habits.contains("AddHabitFab"))
        assertFalse("the divergent glass Home FAB must be gone", today.contains("QuickAddFab"))
        assertTrue("the component owns the glyph", fab.contains("fun MomentumFab("))
    }

    @Test
    fun `the shared FAB is circular with one material and the prominent glyph`() {
        assertTrue("circular", fab.contains("CircleShape"))
        assertTrue("it owns the add glyph", fab.contains("MomentumIcons.add"))
        assertTrue("the glyph is the prominent size", fab.contains("MomentumIconSizes.prominent"))
        assertTrue("one Aurora material", fab.contains("Brush.verticalGradient"))
        // ADR-48 Amendment Note 3: the hairline is the design system's own
        // theme-resolved edge, not a hard-coded white alpha.
        assertTrue(
            "the dark hairline must be the design system's edge token",
            fab.contains("MomentumGlass.borderDark"),
        )
        assertTrue(
            "the light hairline must be the design system's ink edge token",
            fab.contains("MomentumGlass.borderLight"),
        )
        assertFalse(
            "the hard-coded white hairline is retired — it is invisible in Light",
            fab.contains("Color.White.copy(alpha = 0.28f)"),
        )
        assertTrue("soft shadow", fab.contains("shadowElevation = glowShadowElevation("))
        assertTrue("the elevation must be the floating-layer token", fab.contains("MomentumGlass.heroElevation"))
        assertTrue("press feedback", fab.contains("glassPress(interaction)"))
        assertTrue("announced as a button", fab.contains("role = Role.Button"))
    }

    @Test
    fun `the reference glow and press rotation are additive opt-ins, Home only`() {
        // ADR-48 Amendment Note 6 (M3/M5): the §8 pass's planned "the reference's
        // permanent glow pulse is absent" is INVERTED — the human ruled the
        // reference's FAB motion required. Both parameters default to false, so
        // Habits' FAB is byte-identical.
        assertTrue(fab.contains("glowPulse: Boolean = false"))
        assertTrue(fab.contains("rotateGlyphOnPress: Boolean = false"))
        assertTrue("Home opts into the reference glow", codeOf(today).contains("glowPulse = true"))
        assertTrue("Home opts into the reference press rotation", codeOf(today).contains("rotateGlyphOnPress = true"))
        assertFalse("Habits must not inherit Home's opt-in", habits.contains("glowPulse = true"))
        assertFalse("Habits must not inherit Home's opt-in", habits.contains("rotateGlyphOnPress = true"))
        assertTrue("both opt-ins are reduced-motion gated", fab.contains("&& !reduceMotion"))
        // The reference's own numbers: 12 -> 16 dp elevation, 0.40 -> 0.55 spot.
        assertTrue(fab.contains("GlowElevationRise = 4.dp"))
        assertTrue(fab.contains("const val GlowSpotRest = 0.40f"))
        assertTrue(fab.contains("const val GlowSpotPeak = 0.55f"))
        assertTrue(fab.contains("const val GlyphPressRotation = 90f"))
        assertTrue("the press rotation reuses the existing fast token", fab.contains("durationMillis = MomentumMotion.fast"))
        // The glow's phase must be read in the **layer/draw phase**, never in the
        // composable body: `Modifier.shadow` is itself a `BlockGraphicsLayerModifier`,
        // so the shadow is written out as that same layer with the read inside its
        // block — which is what stops the FAB recomposing on every frame
        // (ADR-48 Amendment Note 6 §3/§4, `task.md` §10.4 finding 1).
        assertTrue(
            "the glow's phase is read inside the layer block",
            fab.contains("val phase = glowPhase.value") &&
                fab.contains("spotShadowColor = c.primary.copy(alpha = glowSpotAlpha(phase))"),
        )
        assertFalse(
            "the phase must never be read in the composable body",
            fab.contains("glowPhase.value)"),
        )
        assertFalse(
            "Modifier.shadow must not be used — it cannot read the phase in the layer phase",
            fab.contains("Modifier.shadow("),
        )
        // No new material, size, glyph or easing was added for either loop.
        assertEquals(
            "the FAB gains no second shadow",
            1,
            count(fab, "shadowElevation = "),
        )
    }

    @Test
    fun `the FAB fill is glass-over primary at the design system's own alpha`() {
        // ADR-48 Amendment Note 4: the two hard-coded fill alphas become
        // MomentumGlass.heroFill on c.primary, so the Aurora stays perceptible
        // through the disc and the FAB joins one material family with the glass
        // bar and the Settings control. No new token, no new value.
        assertTrue(
            "the fill must use the floating-layer glass token",
            fab.contains("c.primary.copy(alpha = MomentumGlass.heroFill)"),
        )
        assertFalse(
            "the retired opaque fill alphas must stay gone",
            fab.contains("0.95f") || fab.contains("0.92f"),
        )
        assertTrue(
            "the glyph's ink must stay onPrimary, so it keeps contrast",
            fab.contains("tint = c.onPrimary"),
        )
        // Nothing else on the shared component changed.
        assertTrue("the disc is still 56 dp", fab.contains("MomentumFabSize = 56.dp"))
        assertTrue("the top-edge illumination streak survives", fab.contains("0f to Color.White.copy(alpha = 0.22f)"))
        assertTrue("the label is still a parameter", fab.contains("contentDescription: String"))
    }

    @Test
    fun `the FAB is labelled and clears the 48 dp minimum`() {
        assertTrue(
            "TalkBack must hear Home's feature name",
            codeOf(today).contains("R.string.cd_quick_add"),
        )
        assertTrue(
            "TalkBack must hear Habits' feature name",
            codeOf(habits).contains("R.string.cd_create_habit"),
        )
        assertTrue("the label is a parameter", fab.contains("contentDescription: String"))
        assertTrue(
            "diameter must clear the 48 dp minimum touch target",
            fab.contains("MomentumFabSize = 56.dp"),
        )
    }

    @Test
    fun `tapping Quick Add opens Create Item and nothing else`() {
        assertTrue(
            "Quick Add must open the Habit/Task chooser",
            count(nav, "showCreateItem = true") >= 4,
        )
        assertTrue(nav.contains("if (showCreateItem)"))
    }

    @Test
    fun `Home always shows the FAB and the empty state carries no CTA`() {
        val code = codeOf(today)
        // ADR-48 Amendment Note 2 (human ruling): the FAB is the single
        // creation affordance in EVERY state, so its presence is not a state
        // change and the empty-state card communicates state only.
        assertFalse(
            "the FAB must not be gated on the day's state",
            code.contains("visible = !state.isEmpty"),
        )
        assertFalse(
            "no visibility animation may wrap the FAB",
            code.contains("AnimatedVisibility"),
        )
        assertTrue("the FAB must be composed", code.contains("MomentumFab("))
        assertFalse(
            "the empty state must take no action",
            code.contains("HomeEmptyState(onAdd"),
        )
        assertTrue("the empty state is still the no-activity card", code.contains("HomeEmptyState()"))
        assertFalse("the retired Star glyph must stay gone", code.contains("Icons.Rounded.Star"))
        assertFalse("no Material glyph import may remain", code.contains("material.icons.rounded.Star"))
        assertTrue(
            "the list still reserves room for the floating button",
            code.contains("MomentumSpacing.card + MomentumFabSize + MomentumSpacing.section"),
        )
        assertTrue(
            "the reserve must be one named constant so the list and the empty " +
                "card's centring region cannot drift",
            code.contains("private val EmptyDayBottomReserve = MomentumSpacing.card + MomentumFabSize + MomentumSpacing.section"),
        )
        assertTrue(
            "the empty state renders without a glyph",
            code.contains("icon = null"),
        )
        assertTrue(
            "the empty card is centred by measured geometry, not spacers",
            code.contains("heightIn(min = emptyRegion)"),
        )
        assertTrue(
            "the centring region comes from the chrome's reported height",
            code.contains("onHeightChanged = { chromeHeightPx = it }"),
        )
        assertTrue(
            "the centring region is the viewport minus the chrome and the reserve",
            code.contains("viewportDp - with(density) { chromeHeightPx.toDp() } - EmptyDayBottomReserve"),
        )
        assertFalse(
            "the retired empty-state CTA copy must not linger as dead strings",
            read("src/main/res/values/strings.xml").contains("today_empty_action"),
        )
    }

    @Test
    fun `the FAB is placed one section gap above the content area`() {
        // §8.5 / §8.15.4: the FAB floats over the list and must not read as
        // part of the glass bottom bar, so it is bottom-end aligned, inset by
        // one screen margin horizontally and one *section* gap vertically —
        // the primary visual-integration fix for N3.
        val code = codeOf(today)
        val call = code.substringAfter("MomentumFab(")
        assertTrue(
            "the FAB must be bottom-end aligned",
            call.contains(".align(Alignment.BottomEnd)"),
        )
        assertTrue(
            "the FAB must clear the screen edge by the screen margin",
            call.contains("end = MomentumSpacing.screen"),
        )
        assertTrue(
            "the FAB must sit one section gap above the content area, not one " +
                "card padding",
            call.contains("bottom = MomentumSpacing.section"),
        )
        assertFalse(
            "the retired card-padding gap must not come back",
            call.contains("bottom = MomentumSpacing.compact"),
        )
    }
}
