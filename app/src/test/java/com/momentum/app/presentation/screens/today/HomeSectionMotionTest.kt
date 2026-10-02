package com.momentum.app.presentation.screens.today

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * DASHBOARD_SPECS §11 + refinement R6 + ADR-48 Amendment Notes 5 and 6:
 * completing an activity relocates its row from Pending to Completed and the row
 * must glide to its new place rather than vanish; every motion uses a named
 * `MomentumMotion` token; reduced motion switches decorative animation off
 * completely (null specs, absent loops — never shortened); the 100% milestone is
 * a real one-shot reward (no snackbar, no invented animation); and the screen's
 * only wall-clock motion is the **three approved ambient loops**, driven from one
 * shared clock, consumed in the draw/layer phase, and entirely absent under
 * reduced motion.
 */
class HomeSectionMotionTest {

    private fun read(path: String): String {
        val file = File(path)
        assertTrue("missing source: ${file.absolutePath}", file.isFile)
        // Comments and imports are stripped: neither a KDoc mention nor an
        // `import` line is a call site, and both would otherwise satisfy — or
        // inflate — a count-based assertion about rendered UI.
        return file.readText()
            .lines()
            .filterNot {
                val t = it.trimStart()
                t.startsWith("//") || t.startsWith("*") || t.startsWith("import ")
            }
            .joinToString("\n")
    }

    private val screen = read("src/main/java/com/momentum/app/presentation/screens/today/TodayScreen.kt")
    private val atmosphere = read("src/main/java/com/momentum/app/presentation/screens/today/HomeAtmosphere.kt")
    private val progress = read("src/main/java/com/momentum/app/presentation/screens/today/HomeProgress.kt")
    private val cards = read("src/main/java/com/momentum/app/presentation/screens/today/HomeCards.kt")
    private val viewModel = read("src/main/java/com/momentum/app/presentation/screens/today/TodayViewModel.kt")
    private val motion = read("src/main/java/com/momentum/app/presentation/theme/Motion.kt")
    private val aurora = read("src/main/java/com/momentum/app/presentation/aurora/MomentumAurora.kt")

    private fun count(text: String, needle: String): Int = text.split(needle).size - 1

    @Test
    fun `every home row and section header participates in list motion`() {
        assertTrue("headers must fade and place with their rows", count(screen, ".animateItem(") >= 4)
        assertTrue(count(screen, "placementSpec = placementSpec") >= 4)
        assertTrue(count(screen, "fadeInSpec = fadeInSpec") >= 4)
        assertTrue(count(screen, "fadeOutSpec = fadeOutSpec") >= 4)
    }

    @Test
    fun `reduced motion nulls every spec instead of shortening it`() {
        assertTrue(screen.contains("val fadeInSpec: FiniteAnimationSpec<Float>?"))
        assertTrue(screen.contains("val placementSpec: FiniteAnimationSpec<IntOffset>?"))
        assertTrue(screen.contains("val fadeOutSpec: FiniteAnimationSpec<Float>?"))
        // Three list specs plus the one ambient clock: four null-or-value branches.
        assertEquals(
            "every spec branch replaces, never shortens",
            4,
            count(screen, "if (reduceMotion) null else"),
        )
        assertTrue(
            "the standard token drives the fade in",
            screen.contains("tween(durationMillis = MomentumMotion.standard, easing = MomentumMotion.easeOut)"),
        )
        assertTrue(
            "the fast token drives the fade out",
            screen.contains("tween(durationMillis = MomentumMotion.fast)"),
        )
        assertTrue(screen.contains("spring(stiffness = Spring.StiffnessMediumLow)"))
        // Branch transitions and visibility skip their spec, not shorten it.
        assertTrue(screen.contains("EnterTransition.None"))
        assertTrue(screen.contains("ExitTransition.None"))
        // The branch crossfade is the only `if (reduceMotion) { … }` block left
        // on the screen itself. The Aurora intensity snap moved into the
        // atmosphere with the field it drives, and the Quick Add FAB's
        // `AnimatedVisibility` was removed by ADR-48 Amendment Note 2.
        assertEquals(
            "reduced-motion branches replace, never shorten",
            1,
            count(screen, "if (reduceMotion) {"),
        )
        assertTrue(
            "the Aurora snap moved with the field, not away from it",
            count(atmosphere, "if (reduceMotion) {") == 1,
        )
        assertTrue(
            "the encouragement's visibility transition is retained and nulled",
            progress.contains("EnterTransition.None") && progress.contains("ExitTransition.None"),
        )
    }

    @Test
    fun `the row forwards the incoming modifier so the animation reaches the card`() {
        val signature = screen.substringAfter("items(remaining").take(700)
        assertTrue(signature.contains("modifier = Modifier"))
        assertTrue(signature.contains(".padding(horizontal = MomentumSpacing.screen)"))
        assertTrue("the card itself is the screen-local surface", signature.contains("HomeActivityCard("))
    }

    @Test
    fun `section ordering is still fixed and empty sections stay hidden`() {
        assertTrue(count(screen, "title = stringResource(R.string.today_section_remaining)") == 1)
        assertTrue(count(screen, "title = stringResource(R.string.today_section_completed)") == 1)
        assertTrue(count(screen, "if (remaining.isNotEmpty())") == 1)
        assertTrue(count(screen, "if (finished.isNotEmpty())") == 1)
        assertTrue(
            "Pending must precede Completed",
            screen.indexOf("today_section_remaining") < screen.indexOf("today_section_completed"),
        )
    }

    @Test
    fun `the milestone is one shot, token-timed and snackbar-free`() {
        assertTrue("MomentumMotion.reward must exist", motion.contains("const val reward = 650"))
        assertTrue(screen.contains("MomentumMotion.reward"))
        assertTrue("one haptic acknowledgement", screen.contains("haptics.performHapticFeedback"))
        assertTrue(screen.contains("HapticFeedbackType.Confirm"))
        assertTrue(
            "one keyed effect owns the reward window",
            count(screen, "LaunchedEffect(milestoneTick") == 1,
        )
        assertTrue("the transient maps to MILESTONE", screen.contains("milestoneActive -> AuroraState.MILESTONE"))
        // Celebrate stays the typed one-shot signal, now payload-free, and is
        // never turned into a snackbar.
        assertTrue(viewModel.contains("TodayEffect.Celebrate"))
        assertTrue(viewModel.contains("effects.send(TodayEffect.Celebrate)"))
        assertFalse("the celebrate payload must stay removed", viewModel.contains("Celebrate(UiText"))
        assertFalse("the retire-day snackbar copy must stay gone", viewModel.contains("today_celebrate_all_done"))
        assertFalse(
            "the Celebrate branch must not surface a snackbar",
            screen.contains("TodayEffect.Celebrate -> snackbar"),
        )
        // The ring's sweep and the control's pop stay the existing springs.
        assertTrue(cards.contains("animate = !reduceMotion"))
    }

    @Test
    fun `Home's only infinite motion is the three approved ambient loops`() {
        // ADR-48 Amendment Note 6 (human ruling 2026-10-01 §4.7 r2) superseded
        // ADR-48 §6's blanket "no permanent infinite animation": the approved
        // Stitch composition puts a live Aurora in the progress card and a glowing
        // FAB over the atmosphere, so three *ambient* loops are required. The
        // replacement rule is narrower, not wider: slow (>= 2.2 s), tiny-amplitude,
        // one shared clock, draw-phase only, absent under reduced motion.
        assertEquals("exactly one ambient clock for the whole screen", 1, count(screen, "rememberInfiniteTransition"))
        assertEquals(
            "the clock is created only when motion is allowed",
            1,
            count(screen, "if (reduceMotion) null else rememberInfiniteTransition"),
        )
        // The three approved periods are pinned to code.html's own values.
        assertTrue("aurora-glow-pulse is 6 s", motion.contains("const val ambient = 6000"))
        assertTrue("fab-glow is 3 s", motion.contains("const val fabGlow = 3000"))
        assertTrue("bolt-pulse is 2.2 s", motion.contains("const val verifiedPulse = 2200"))
        // The atmosphere keeps its own drift (the "Aurora subtle movement"
        // ruling); it is still nulled inside AuroraBackground, so the ambient gate
        // stays at exactly one branch.
        assertTrue("aurora drift is enabled on Home", atmosphere.contains("animated = true"))
        assertEquals("the Aurora snap keeps its one reduce-motion branch", 1, count(atmosphere, "if (reduceMotion) {"))
        assertTrue("the Aurora change is still a one-shot tween", atmosphere.contains("MomentumMotion.emphasis"))
        assertTrue("reduced motion snaps instead of tweening", atmosphere.contains("snap()"))
        assertTrue(aurora.contains("val animate = animated && !reduceMotion"))
        // Retained rejections, now positively asserted.
        assertFalse("flame-pulse is unbound in code.html and belongs to the removed streak chip", progress.contains("flame-pulse"))
        assertFalse("aurora-float is unbound in code.html and stays unbound", screen.contains("aurora-float"))
        assertFalse(
            "per-row entrance stays rejected: no list item receives an entrance phase",
            cards.contains("entranceProgress"),
        )
    }

    @Test
    fun `the ambient loops stay out of the list items`() {
        // The intent of ADR-48 Amendment Note 6 §4: no per-item animation
        // instance, no per-frame recomposition, no scroll-driven work, and the
        // transition exists in exactly one place. The clock's values therefore
        // arrive as `State<Float>` parameters read inside the draw / layer phase,
        // never as a bare float the item's body would recompose on.
        assertEquals("no transition in a list item", 0, count(cards, "rememberInfiniteTransition"))
        assertEquals("no animated state in a list item", 0, count(cards, "animateFloatAsState"))
        assertEquals("no mutable state in a list item", 0, count(cards, "mutableStateOf"))
        assertEquals("no effect in a list item", 0, count(cards, "LaunchedEffect"))
        assertTrue(cards.contains("ambientPulse: State<Float>?"))
        assertTrue(cards.contains("boltPulse: State<Float>?"))
        assertTrue(
            "the blobs read the clock inside drawWithCache",
            cards.contains(".drawWithCache {") && cards.contains("phase?.value ?: 0f"),
        )
        assertTrue(
            "the verified glyph reads it inside graphicsLayer",
            cards.contains(".graphicsLayer {"),
        )
        // The FAB's loop is the third approved one, inside the shared component,
        // outside the list, and only Home opts into it.
        val fab = read("src/main/java/com/momentum/app/presentation/components/MomentumFab.kt")
        assertEquals("one glow loop in the FAB", 1, count(fab, "rememberInfiniteTransition"))
        assertTrue("the FAB's loop is gated", fab.contains("if (glowPulse && !reduceMotion)"))
        assertTrue("Home opts in", screen.contains("glowPulse = true"))
        // §8.19.3/§8.19.4: the phase is consumed in the draw/layer phase, never by
        // the composable body, so the loop invalidates the FAB's layer and nothing
        // else. `Modifier.shadow` cannot do that — it is itself a
        // `BlockGraphicsLayerModifier`, so the shadow is written out as that same
        // layer with the read inside its block.
        assertTrue(
            "the FAB's glow phase is read inside the layer block",
            fab.contains("val phase = glowPhase.value") &&
                fab.contains("shadowElevation = glowShadowElevation(phase).toPx()"),
        )
        assertFalse(
            "the FAB's glow phase must never be read in the composable body",
            fab.contains("glowPhase.value)"),
        )
        val habits = read("src/main/java/com/momentum/app/presentation/screens/habits/HabitsScreen.kt")
        assertFalse("Habits must not inherit Home's opt-in", habits.contains("glowPulse = true"))
    }

    @Test
    fun `the entrance is one finite clock, not a loop and not per row`() {
        // RISK-6: the clock lives above the AnimatedContent branch key, so a
        // payload refresh does not replay it.
        assertEquals("one entrance clock", 1, count(screen, "by animateFloatAsState("))
        assertTrue(screen.contains("tween(durationMillis = MomentumMotion.enter, easing = MomentumMotion.easeOut)"))
        assertTrue("the reference's total stagger span is the token", motion.contains("const val enter = 900"))
        listOf("EntranceChromeOffset = 0", "EntranceCardOffset = 100", "EntrancePendingOffset = 200", "EntranceCompletedOffset = 300")
            .forEach { offset ->
                assertTrue("the approved stagger offset $offset must survive", screen.contains(offset))
            }
        assertFalse(
            "the entrance must not be a per-row animation",
            cards.contains("animateItem") || cards.contains("AnimatedVisibility"),
        )
    }

    @Test
    fun `scrolling costs Home no recomposition and no observation`() {
        // ADR-47 Amendment Note 5 §5 / §12: the scroll boundary is layout, not
        // observation. Nothing in Home's scroll surface reads scroll state, so a
        // fling invalidates nothing — with the collapse deleted outright.
        val screenLines = screen.lines()
            .filterNot { it.trimStart().startsWith("//") || it.trimStart().startsWith("*") }
            .joinToString("\n")
        listOf(
            "NestedScrollConnection",
            "nestedScroll",
            "collapsePx",
            "maxCollapsePx",
            "heroNestedScroll",
            "onCollapseRangePx",
            "derivedStateOf",
            "onGloballyPositioned",
            "animateScrollPosition",
            "parallax",
        ).forEach { token ->
            assertFalse("obsolete scroll machinery is back: $token", screenLines.contains(token))
        }
        // The one measurement the chrome owns is a pure function of its content,
        // insets and width — it can never fire on scroll.
        assertTrue(
            "the only measurement is the chrome's own height",
            screen.contains("onHeightChanged = { chromeHeightPx = it }"),
        )
        assertEquals(
            "exactly one state object may drive the scroll boundary",
            1,
            count(screen, "var chromeHeightPx by remember"),
        )
        // The three ambient loops add no new scroll observer.
        assertEquals(
            "no per-frame state write may live inside a list item",
            0,
            count(cards, "mutableStateOf"),
        )
    }
}
