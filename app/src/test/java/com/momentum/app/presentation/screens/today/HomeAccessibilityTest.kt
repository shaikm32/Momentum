package com.momentum.app.presentation.screens.today

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * §8.13 + ADR-47 Amendment Note 5 and ADR-48 Amendment Notes 5 and 6: screen
 * readers, accessible labels, minimum touch targets, sufficient contrast and
 * reduced motion must all hold on Home — with completion readable without
 * animation or colour alone, **one** merged progress description, a **fixed** top
 * chrome whose elements stay separate semantics nodes so Settings remains
 * reachable, three heading stops, and a chrome that is *measured* (not a fixed
 * band) so large fonts make the scroll region smaller rather than clipping
 * anything.
 */
class HomeAccessibilityTest {

    private fun read(path: String): String {
        val file = File(path)
        assertTrue("missing source: ${file.absolutePath}", file.isFile)
        return file.readText()
    }

    private val screen = read(
        "src/main/java/com/momentum/app/presentation/screens/today/TodayScreen.kt",
    )
    private val chrome = read(
        "src/main/java/com/momentum/app/presentation/screens/today/HomeChrome.kt",
    )
    private val atmosphere = read(
        "src/main/java/com/momentum/app/presentation/screens/today/HomeAtmosphere.kt",
    )
    private val progress = read(
        "src/main/java/com/momentum/app/presentation/screens/today/HomeProgress.kt",
    )
    private val cards = read(
        "src/main/java/com/momentum/app/presentation/screens/today/HomeCards.kt",
    )
    private val aurora = read(
        "src/main/java/com/momentum/app/presentation/aurora/MomentumAurora.kt",
    )
    private val button = read(
        "src/main/java/com/momentum/app/presentation/components/CompletionButton.kt",
    )
    private val fab = read(
        "src/main/java/com/momentum/app/presentation/components/MomentumFab.kt",
    )
    private val glass = read(
        "src/main/java/com/momentum/app/presentation/components/LiquidGlass.kt",
    )

    private fun count(text: String, needle: String): Int = text.split(needle).size - 1

    /** Code only: comments must never satisfy (or fail) an assertion. */
    private fun codeOf(text: String): String = text
        .lines()
        .filterNot { it.trimStart().startsWith("//") || it.trimStart().startsWith("*") }
        .joinToString("\n")

    @Test
    fun `both section titles are announced as screen-reader headings`() {
        assertEquals(
            "Pending and Completed must both be headings",
            1,
            count(cards, ".semantics { heading() }"),
        )
    }

    @Test
    fun `the greeting is the first heading, giving three heading stops`() {
        // The structural answer to a fixed chrome: heading navigation yields
        // greeting -> Pending -> Completed, so the user gets a map rather than
        // only a permanently visible band.
        assertEquals(
            "the chrome must declare exactly one heading",
            1,
            count(chrome, ".semantics { heading() }"),
        )
        assertTrue("the heading must be the greeting", chrome.contains("Modifier.semantics { heading() }"))
    }

    @Test
    fun `the fixed chrome keeps its elements as separate unmerged semantics nodes`() {
        // A merged chrome would swallow the Settings button and make a persistent
        // control unreachable by screen reader — a hard requirement.
        val code = codeOf(chrome)
        listOf("clearAndSetSemantics", "mergeDescendants").forEach { token ->
            assertFalse("the chrome must never merge its semantics ($token)", code.contains(token))
        }
        assertTrue(
            "the chrome's copy and its Settings control must all be present",
            count(code, "Text(") >= 3 && code.contains("LiquidGlassIconButton("),
        )
        assertTrue(
            "Settings must keep its accessible label",
            chrome.contains("R.string.cd_settings"),
        )
        // The date capsule is a pill, not a node: a date is a date.
        assertFalse(
            "the date capsule must not gain a semantics modifier of its own",
            code.substringAfter("RoundedCornerShape(MomentumRadii.pill)").substring(0, 400)
                .contains("semantics"),
        )
    }

    @Test
    fun `every interactive control on Home clears the 48dp minimum target`() {
        assertTrue(
            "completion control height",
            button.contains(".size(MomentumHeights.controlMin)"),
        )
        assertTrue(
            "the idle ring variant keeps that same 48 dp target",
            button.contains("showIdleGlyph: Boolean = true"),
        )
        assertTrue(
            "header action buttons",
            glass.contains(".size(MomentumHeights.iconTarget)"),
        )
        assertTrue(
            "the shared FAB is 56 dp",
            fab.contains("MomentumFabSize = 56.dp"),
        )
        // The bottom bar's tab targets are 48 dp on both axes (ADR-49 Amendment
        // Note 1, deviation 5).
        assertTrue(
            "every tab clears the 48 dp minimum height",
            glass.contains("minHeight = MomentumHeights.controlMin"),
        )
        assertTrue(
            "the tab floor is a floor, not a clip",
            glass.contains("defaultMinSize("),
        )
    }

    @Test
    fun `reduced motion reaches the atmosphere, the card and the list`() {
        // ADR-48 Amendment Note 6 M1: the drift is ON, and AuroraBackground still
        // nulls its own phase — so the gate stays at exactly one branch.
        assertTrue("the approved Aurora drift is on", atmosphere.contains("animated = true"))
        assertEquals(
            "the Aurora snap keeps its one reduce-motion branch",
            1,
            count(atmosphere, "if (reduceMotion) {"),
        )
        assertTrue("the Aurora change is still a one-shot tween", atmosphere.contains("MomentumMotion.emphasis"))
        assertTrue("reduced motion snaps instead of tweening", atmosphere.contains("snap()"))
        assertTrue(
            "AuroraBackground nulls its own phase under reduced motion",
            aurora.contains("val animate = animated && !reduceMotion"),
        )
        assertTrue("the card must receive the flag", screen.contains("reduceMotion = reduceMotion"))
        assertTrue(
            "the card's own motion must be nulled by the flag",
            cards.contains("animate = !reduceMotion") &&
                cards.contains("glowEnabled = milestoneGlow && !reduceMotion"),
        )
        // Three list specs plus the single ambient clock: four branches, and the
        // clock branch composes no transition at all under reduced motion.
        assertEquals(
            "list motion must switch off, not just slow down",
            4,
            count(screen, "if (reduceMotion) null else"),
        )
        assertTrue(
            "the ambient transition is never composed when motion is reduced",
            screen.contains("if (reduceMotion) null else rememberInfiniteTransition(label = \"homeAmbient\")"),
        )
        assertTrue("branch transitions skip under reduced motion", screen.contains("EnterTransition.None"))
        assertTrue("visibility transitions skip under reduced motion", screen.contains("ExitTransition.None"))
        assertTrue(
            "the completion state must still be visible without motion",
            button.contains("if (completed) 1f else 0.94f"),
        )
        assertTrue(
            "the FAB's two opt-ins are both reduced-motion gated",
            count(fab, "&& !reduceMotion") == 2,
        )
        assertTrue(
            "the entrance clock resolves to fully revealed, not shortened",
            screen.contains("targetValue = if (reduceMotion) 1f else 0f"),
        )
    }

    @Test
    fun `the progress card exposes one merged description and a decorative ring`() {
        assertTrue(
            "one merged description so the lockup is not double-read",
            cards.contains("today_hero_progress_desc"),
        )
        assertTrue(cards.contains("clearAndSetSemantics { contentDescription = description }"))
        assertTrue("the ring is decorative (no percent semantics)", cards.contains("showPercent = false"))
        // The completion control keeps its action label even with no idle glyph.
        assertTrue(
            "the idle ring still announces its action",
            button.contains("this.contentDescription = label") &&
                cards.contains("completionActionLabel(activity, completed)"),
        )
        assertTrue(
            "reversal is preserved: the control keeps Role.Checkbox",
            button.contains("role = Role.Checkbox"),
        )
    }

    @Test
    fun `no secondary ink sits over the atmosphere anywhere on Home`() {
        // ISSUE-020 remedy: the text token, never a re-inflated veil. The glass
        // cards are exempt — they sit on their own fill — so only the two files
        // whose copy is actually bare over the atmosphere are checked.
        listOf("HomeChrome.kt", "HomeProgress.kt").forEach { file ->
            assertFalse(
                "no textSecondary over the atmosphere in $file",
                codeOf(read("src/main/java/com/momentum/app/presentation/screens/today/$file"))
                    .contains("textSecondary"),
            )
        }
        assertTrue(
            "the chrome's supporting line is textPrimary, not the reference's #475569",
            chrome.contains("color = c.textPrimary"),
        )
    }

    @Test
    fun `the atmosphere contributes no semantics`() {
        // MomentumHeroArtwork keeps contentDescription = null and AuroraBackground
        // stays outside the semantics tree — nothing here may wrap the atmosphere
        // in a semantics modifier of its own.
        val code = codeOf(atmosphere)
        assertFalse(
            "the atmosphere must not add a semantics node",
            code.contains("clearAndSetSemantics") || code.contains("mergeDescendants"),
        )
        assertFalse(
            "the new aura blobs must not add a semantics node either",
            codeOf(cards).contains("contentDescription = \"\"") ||
                codeOf(cards).contains("mergeDescendants"),
        )
        assertTrue(
            "the blobs are decorative: the verified glyph carries no label",
            cards.contains("contentDescription = null"),
        )
    }

    @Test
    fun `rows are grouped by completion state rather than tinted in place`() {
        // The call carries `contentType`, so the guard pins the key expression.
        assertTrue(
            "pending rows come from state.remaining",
            screen.contains("items(remaining, key = { it.activity.id }"),
        )
        assertTrue(
            "completed rows come from state.completed",
            screen.contains("items(finished, key = { it.activity.id }"),
        )
        assertTrue(count(screen, "completed = false,") >= 1)
        assertTrue(count(screen, "completed = true,") >= 1)
        assertTrue(
            "completion stays readable without colour alone: strikethrough plus a " +
                "subdued token, never a faded card",
            cards.contains("textDecoration = if (completed) TextDecoration.LineThrough else null") &&
                cards.contains("color = if (completed) c.textSecondary else c.textPrimary"),
        )
    }

    @Test
    fun `large fonts make the region smaller rather than clipping the chrome`() {
        // The chrome is measured, not a fixed band: the scroll region takes the
        // remainder and still scrolls, so no foreground element is height-
        // constrained by an atmosphere at any font scale.
        assertTrue(
            "the chrome must report its own measured height",
            chrome.contains(".onSizeChanged { size -> onHeightChanged(size.height) }"),
        )
        assertTrue(
            "the region must be inset by exactly that measurement",
            screen.contains(".padding(top = with(density) { chromeHeightPx.toDp() })"),
        )
        assertTrue(
            "the region must remain a scroll surface, so the card can grow",
            screen.contains("clipToBounds()"),
        )
        assertFalse(
            "the artwork window must not grow with the chrome or the font scale",
            codeOf(atmosphere).contains("chromeHeight"),
        )
        assertFalse(
            "no screen-percentage floor may inflate anything",
            screen.contains("screenHeightDp"),
        )
        assertTrue(
            "the entrance reveal is a layer transform, so it never constrains height",
            cards.contains("internal fun Modifier.homeReveal(reveal: Float)"),
        )
    }

    @Test
    fun `the loading skeleton is decorative with exactly one accessible label`() {
        assertTrue("the skeleton is testable", screen.contains("internal fun TodayLoadingSkeleton("))
        assertEquals(
            "one label for the whole skeleton",
            1,
            count(screen, "clearAndSetSemantics { contentDescription = label }"),
        )
        assertFalse("no spinner is composed", screen.contains("CircularProgressIndicator"))
        // ADR-48 Amendment Note 6 (B): Home legitimately owns exactly one
        // infinite transition now, so this assertion is re-anchored to the
        // composable it was actually about — the skeleton must not shimmer.
        val skeletonBody = screen
            .substringAfter("internal fun TodayLoadingSkeleton(")
            .substringBefore("@Composable")
        assertTrue("the extracted body must not be empty", skeletonBody.isNotBlank())
        assertFalse(
            "the skeleton must not shimmer",
            skeletonBody.contains("rememberInfiniteTransition") || skeletonBody.contains("animate"),
        )
    }
}
