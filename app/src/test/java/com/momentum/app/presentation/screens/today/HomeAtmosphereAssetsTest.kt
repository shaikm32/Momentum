package com.momentum.app.presentation.screens.today

import androidx.compose.ui.unit.dp
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Guards the approved Home composition contract (ADR-47 + Amendment Notes 1-7;
 * ADR-48 Amendment Notes 5 and 6): the Aurora is **one atmosphere** — the
 * ambient field, the shared theme-selected artwork in a **width-proportional**
 * window measured from the window top, and one four-stop scrim spanning the
 * whole content area; the artwork is that atmosphere's upper region, blending
 * into the field with the shared 88dp dissolve and no hard boundary;
 * readability is that scrim plus the dissolve; and none of the removed
 * machinery (veil anchors, S-curve, ring scale, parallax, collapse, the retired
 * vertical lockup) comes back. Pure file checks — no device or resource loading
 * required.
 */
class HomeAtmosphereAssetsTest {

    private fun source(path: String): String {
        val file = File(path)
        assertTrue("missing source: ${file.absolutePath}", file.isFile)
        return file.readText()
    }

    /** Comments must never satisfy an assertion about rendered UI. */
    private fun codeOf(text: String): String = text
        .lines()
        .filterNot { it.trimStart().startsWith("//") || it.trimStart().startsWith("*") }
        .joinToString("\n")

    private val atmosphere = source(
        "src/main/java/com/momentum/app/presentation/screens/today/HomeAtmosphere.kt",
    )
    private val atmosphereCode = codeOf(atmosphere)

    private val chrome = source(
        "src/main/java/com/momentum/app/presentation/screens/today/HomeChrome.kt",
    )
    private val chromeCode = codeOf(chrome)

    private val progress = source(
        "src/main/java/com/momentum/app/presentation/screens/today/HomeProgress.kt",
    )
    private val progressCode = codeOf(progress)

    private val cards = source(
        "src/main/java/com/momentum/app/presentation/screens/today/HomeCards.kt",
    )
    private val cardsCode = codeOf(cards)

    private val screen = source(
        "src/main/java/com/momentum/app/presentation/screens/today/TodayScreen.kt",
    )
    private val screenCode = codeOf(screen)

    private val shared = source(
        "src/main/java/com/momentum/app/presentation/components/MomentumHeroArtwork.kt",
    )
    private val button = source(
        "src/main/java/com/momentum/app/presentation/components/CompletionButton.kt",
    )
    private val buttonCode = codeOf(button)
    private val viewModel = source(
        "src/main/java/com/momentum/app/presentation/screens/today/TodayViewModel.kt",
    )
    private val strings = source("src/main/res/values/strings.xml")

    @Test
    fun `the atmosphere draws the field, then the artwork window, then the one scrim`() {
        val fieldAt = atmosphereCode.indexOf("HomeAurora(")
        val artworkAt = atmosphereCode.indexOf("MomentumHeroArtwork(")
        val scrimAt = atmosphereCode.indexOf("HeroScrim(")
        assertTrue("the atmosphere must draw the ambient field", fieldAt >= 0)
        assertTrue("the atmosphere must draw the shared artwork", artworkAt >= 0)
        assertTrue("the atmosphere must draw its one constant scrim", scrimAt >= 0)
        assertTrue("the field is the first region", fieldAt < artworkAt)
        assertTrue("the artwork is over the field", artworkAt < scrimAt)
    }

    @Test
    fun `the artwork window is the intrinsic aspect ratio times the window width`() {
        // task.md §4.8 (Device Finding #1): the withdrawn 460/640 band is
        // replaced by the artwork's own 1024 × 1536 intrinsic ratio, so the
        // window carries the COMPLETE artwork — never a dp literal, never a
        // Hero height, never a tuned band.
        assertEquals(
            "the ratio is the artwork's own 1536/1024 intrinsic height ratio",
            1536f / 1024f,
            ArtworkIntrinsicRatio,
            0f,
        )
        assertTrue(
            "the window must be derived from the intrinsic ratio and the width",
            atmosphereCode.contains(
                "internal fun artworkWindow(windowWidth: Dp): Dp = " +
                    "(ArtworkIntrinsicRatio * windowWidth.value).dp",
            ),
        )
        assertFalse(
            "the withdrawn 460/640 band must be gone from production code",
            atmosphereCode.contains("460f / 640f") || atmosphereCode.contains("ArtworkBandFraction"),
        )
        assertTrue(
            "the hand-off must be the shared artwork's 88 dp dissolve",
            atmosphereCode.contains("ArtworkHandOff: Dp = 88.dp"),
        )
        // Width-proportional, and equal at equal width in every state.
        assertEquals("360 dp window", 540f, artworkWindow(360.dp).value, 0.01f)
        assertEquals("412 dp window", 618f, artworkWindow(412.dp).value, 0.01f)
        assertEquals("320 dp window", 480f, artworkWindow(320.dp).value, 0.01f)
        assertEquals(
            "the window must be proportional to the width",
            artworkWindow(360.dp) / artworkWindow(180.dp),
            2f,
            0.001f,
        )
    }

    @Test
    fun `the artwork window stays independent of content, state and scroll`() {
        // Every clause of ADR-47 Amendment Note 4 §3 survives verbatim: width is
        // the ONLY newly permitted input.
        val derivation = atmosphereCode.substringAfter("internal fun artworkWindow(").take(160)
        listOf(
            "state.isEmpty",
            "totalCount",
            "doneCount",
            "chromeHeight",
            "collapsePx",
            "maxHeight",
            "viewportDp",
            "minWidth",
        ).forEach { token ->
            assertFalse(
                "the artwork window must not read $token, was $derivation",
                derivation.contains(token),
            )
        }
        assertTrue(
            "the window must be passed the root's own maxWidth, never a new observer",
            screenCode.contains("windowWidth = windowWidth") && screenCode.contains("val windowWidth = maxWidth"),
        )
        assertFalse(
            "no new window-size observer may be introduced",
            screenCode.contains("WindowSizeClass") || screenCode.contains("LocalWindowInfo"),
        )
    }

    @Test
    fun `BOUND-1 is re-derived against the progress card ring`() {
        // task.md §4.8: the window is the intrinsic-ratio height, so the opaque
        // region below the hand-off is 1.5 × width − 88 dp — 392 @320 ·
        // 452 @360 · 530 @412. It grows against the withdrawn band, so the
        // inequality holds with far more margin than before.
        assertEquals(
            "the ring is the reference's w-24 h-24",
            96f,
            ProgressCardRingSize.value,
            0f,
        )
        listOf(320f to 392f, 360f to 452f, 412f to 530f).forEach { (width, expected) ->
            val opaque = artworkWindow(width.dp) - ArtworkHandOff
            assertEquals("the opaque band at ${width}dp", expected, opaque.value, 0.01f)
            assertTrue(
                "BOUND-1 at ${width}dp: $opaque must clear the ring plus two standard gaps",
                opaque.value >= ProgressCardRingSize.value + 2 * 16f,
            )
        }
    }

    @Test
    fun `the artwork is the atmospheres upper region and never covered by a plate`() {
        val call = atmosphereCode.substringAfter("MomentumHeroArtwork(").take(240)
        assertTrue(
            "Home must fit the COMPLETE artwork — the intrinsic ratio matches " +
                "its window, so Fit renders it uncropped",
            call.contains("contentScale = ContentScale.Fit"),
        )
        assertFalse(
            "no object-cover semantics may remain on Home's path",
            call.contains("ContentScale.Crop"),
        )
        assertTrue("the artwork must fill its window", call.contains("fillMaxSize()"))
        assertTrue("the artwork must be tagged as the atmosphere's region", call.contains("testTag(AtmosphereArtworkTag)"))
        assertFalse("no artwork zoom (removed)", call.contains("zoom"))
        assertFalse("no artwork parallax (removed)", call.contains("parallax"))
        assertFalse("the shared top dissolve is not overridden", call.contains("topDissolve"))
        assertFalse("the shared bottom dissolve is not overridden", call.contains("bottomDissolve"))
        // The window sits at the top of the atmosphere: its datum is the window
        // top (ADR-39's no-top-fade contract is load-bearing here), not the
        // chrome's bottom edge.
        assertTrue(
            "the artwork window must be top-aligned in the atmosphere",
            atmosphereCode.contains(".align(Alignment.TopStart)"),
        )
        assertTrue(
            "the window must be a derived height, not a fill of the atmosphere",
            atmosphereCode.contains(".height(artworkWindow(windowWidth))"),
        )
    }

    @Test
    fun `the scrim spans the whole content area with no stop at the chrome edge`() {
        assertTrue("the scrim must fill the atmosphere", atmosphereCode.contains("HeroScrim(Modifier.fillMaxSize())"))
        // One continuous gradient over both the chrome and the list — a stop at
        // the chrome's bottom edge would make the chrome read as a separate band.
        assertFalse(
            "no scrim may be positioned at the chrome's bottom edge",
            atmosphereCode.contains("chromeHeight"),
        )
        val peaks = Regex("""ScrimPeak(?:Light|Dark) = ([0-9.]+)f""")
            .findAll(atmosphereCode)
            .map { it.groupValues[1].toFloat() }
            .toList()
        assertEquals("one peak per theme", 2, peaks.size)
        assertTrue("peaks must stay inside 0.15..0.35, were $peaks", peaks.all { it in 0.15f..0.35f })
        assertTrue("the TASK-018 re-measurement moved no peak", peaks == listOf(0.26f, 0.22f))
        assertTrue(atmosphereCode.contains("ScrimPeakStart = 0.20f"))
        assertTrue(atmosphereCode.contains("ScrimPeakEnd = 0.80f"))
        assertTrue("the scrim must cache its brush", atmosphereCode.contains("drawWithCache"))
        assertTrue("the scrim is one vertical gradient", atmosphereCode.contains("Brush.verticalGradient"))
        // The scrim must reach alpha 0 at the content area's bottom edge.
        assertTrue(
            "the last stop must be the clear one, so the merge is continuous",
            atmosphereCode.contains("1f to clear"),
        )
    }

    @Test
    fun `the persistent chrome carries no plate, fill, divider or shadow`() {
        // ADR-47 Amendment Note 5 §3(a): a plate would make the chrome "an
        // unrelated opaque header" under §4.2 B.
        listOf(
            "Surface(",
            "Card(",
            "LiquidGlassCard",
            ".background(",
            "HorizontalDivider",
            "shadow(",
            "graphicsLayer",
        ).forEach { token ->
            assertFalse("the chrome must carry no $token", chromeCode.contains(token))
        }
        // The only sub-surfaces are the two the approved design itself carries
        // (ADR-47 Amendment Note 5 §3(a) amended by ADR-48 Amendment Note 5): the
        // date capsule and the Settings control.
        assertTrue(
            "the date capsule is the approved glass sub-surface",
            chromeCode.contains("Modifier.liquidGlass(") &&
                chromeCode.contains("RoundedCornerShape(MomentumRadii.pill)") &&
                chromeCode.contains("level = GlassLevel.CONTROL"),
        )
        assertTrue(
            "Settings is the other permitted glass sub-surface",
            chromeCode.contains("LiquidGlassIconButton("),
        )
        // No divider or edge at the chrome's bottom edge: separation is by
        // negative space alone, on the named token.
        assertTrue(
            "the chrome's trailing gap must be the section token alias",
            chromeCode.contains("internal val ChromeBottomInset = MomentumSpacing.section"),
        )
        // One axis, one ink, one type scale.
        assertTrue(
            "the chrome must share the screen axis",
            chromeCode.contains(".padding(horizontal = MomentumSpacing.screen)"),
        )
        assertFalse("no secondary ink over the artwork, either theme", chromeCode.contains("textSecondary"))
    }

    @Test
    fun `the chrome keeps the approved type ramp`() {
        assertTrue(
            "the greeting is the reference's headline-lg-mobile",
            chromeCode.contains("MaterialTheme.typography.headlineLarge"),
        )
        assertTrue(
            "the date capsule is the reference's label-sm",
            chromeCode.contains("style = MomentumLabelMicro"),
        )
        assertTrue(
            "the support line is the reference's body-sm",
            chromeCode.contains("MaterialTheme.typography.bodyMedium") &&
                chromeCode.contains("fontWeight = FontWeight.Medium"),
        )
        assertTrue(
            "the greeting's supporting line is a fixed string, never the " +
                "toggle-controlled encouragement",
            screenCode.contains("greetingSupport = stringResource(R.string.today_greeting_support)") &&
                strings.contains("Ready to conquer your day?"),
        )
        assertFalse(
            "the support line must not be the encouragement",
            chromeCode.contains("encouragement"),
        )
    }

    @Test
    fun `the chrome is fixed, not a scroll item, and reports one measured height`() {
        assertTrue("the chrome must report its own height", chromeCode.contains("onSizeChanged"))
        assertTrue("the chrome must forward the reported height", chromeCode.contains("onHeightChanged(size.height)"))
        // It must be composed as a sibling, outside the list, exactly once.
        assertEquals("the chrome must be composed exactly once", 1, screenCode.occurrences("HomeTopChrome("))
        assertFalse(
            "the chrome must not be a list item",
            screenCode.substringAfter("LazyColumn(").contains("HomeTopChrome("),
        )
    }

    @Test
    fun `the chrome is grouped visually but never merged semantically`() {
        // ADR-47 Amendment Note 5 §8: a merged chrome would swallow the Settings
        // button and make a persistent control unreachable by screen reader.
        listOf("clearAndSetSemantics", "mergeDescendants").forEach { token ->
            assertFalse("the chrome must not merge its semantics ($token)", chromeCode.contains(token))
        }
        assertTrue("the greeting is the screen's first heading", chromeCode.contains("semantics { heading() }"))
        // The date is a date, not a section: it stays plain text, so heading
        // navigation yields greeting -> Pending -> Completed and nothing else.
        assertEquals(
            "the chrome must declare exactly one heading",
            1,
            chromeCode.occurrences("semantics { heading() }"),
        )
        assertFalse(
            "the date capsule pill must carry no semantics of its own",
            chromeCode
                .substringAfter("RoundedCornerShape(MomentumRadii.pill)")
                .take(240)
                .contains("semantics"),
        )
    }

    @Test
    fun `the progress card is the approved horizontal lockup`() {
        // ADR-48 Amendment Note 5: text block left, ring right with the
        // percentage inside it, breakdown beneath a divider. The retired vertical
        // arrangement is gone.
        val card = cardsCode.substringAfter("internal fun HomeProgressCard(")
        val fractionAt = card.indexOf("R.string.today_hero_fraction")
        val ringAt = card.indexOf("ProgressOrb(")
        val percentAt = card.indexOf("roundToInt()")
        val dividerAt = card.indexOf("HorizontalDivider(")
        val breakdownAt = card.indexOf("R.string.cd_progress_completed")
        assertTrue("the fraction must be rendered", fractionAt >= 0)
        assertTrue("the ring must be rendered", ringAt >= 0)
        assertTrue("the percentage inside the ring must be rendered", percentAt >= 0)
        assertTrue("the breakdown must be rendered", breakdownAt >= 0)
        assertTrue("fraction before ring", fractionAt < ringAt)
        assertTrue("ring before percentage", ringAt < percentAt)
        assertTrue("divider before breakdown", dividerAt in 0 until breakdownAt)
        assertEquals(
            "the fraction and the percentage must share one baseline row",
            1,
            card.substring(0, ringAt).occurrences("verticalAlignment = Alignment.Bottom"),
        )
        // One merged description covers the whole group.
        assertTrue(
            cardsCode.contains("clearAndSetSemantics { contentDescription = description }"),
        )
        assertTrue(cardsCode.contains("stringResource(R.string.today_hero_progress_desc, done, total)"))
        // The encouragement stays OUTSIDE the merged group.
        assertTrue(
            "the encouragement must keep its own announcement",
            card.indexOf("clearAndSetSemantics") < card.indexOf("HomeEncouragementLine("),
        )
        // ADR-49 Amendment Note 3 (task.md §10.7): the emerald that failed the
        // 4.5:1 floor on this card's own glass is gone, and so is the
        // textSecondary that §10.1 put in its place — the wash makes the
        // *darkest* composited stop the governing case (3.03–3.91:1).
        val line = cardsCode.substringAfter("HomeEncouragementLine(").take(240)
        assertTrue(
            "the encouragement / statement line reads textPrimary",
            line.contains("color = c.textPrimary"),
        )
        assertFalse("the line must not be inked emerald", line.contains("c.success"))
        assertFalse(
            "the superseded textSecondary ink must not come back",
            line.contains("c.textSecondary"),
        )
        assertTrue("the ring stays decorative", cardsCode.contains("showPercent = false"))
        assertFalse("no second percentage is allowed", cardsCode.contains("showPercent = true"))
    }

    @Test
    fun `the progress card wears the approved values and no new colour`() {
        assertTrue("the ring is the reference's w-24", cardsCode.contains("size = ProgressCardRingSize"))
        assertTrue(
            "the ring gradient is three EXISTING theme tokens",
            cardsCode.contains("listOf(c.success, c.auroraBlue, c.auroraViolet)"),
        )
        assertTrue(
            "the card's glass is HERO with the one documented hue extension",
            cardsCode.contains("level = GlassLevel.HERO") && cardsCode.contains("hue = c.auroraLavender"),
        )
        assertTrue(
            "the row cards keep hue = null — the reference paints no wash on them",
            cardsCode.contains("hue = null"),
        )
        // ADR-49 Amendment Note 5 (task.md §4.11): the Home-scoped fill opts
        // reproduce the rejected global E3 raise at Home's surfaces only.
        assertTrue(
            "the progress card opts into the Home-scoped 0.85 fill",
            cardsCode.contains("fillAlpha = ProgressCardFillAlpha") &&
                cardsCode.contains("ProgressCardFillAlpha = 0.85f"),
        )
        assertTrue(
            "the Home rows opt into the Home-scoped 0.90 fill",
            cardsCode.contains("fillAlpha = HomeRowFillAlpha") &&
                cardsCode.contains("HomeRowFillAlpha = 0.90f"),
        )
        assertTrue(
            "the suffix is the reference's items done, never habits done",
            cardsCode.contains("R.string.today_progress_suffix") && strings.contains(">items done<"),
        )
        assertFalse("habits done is not a true statement about Momentum", strings.contains("habits done"))
        // §4.2 C + `task.md` §10.8 Ruling B: the **visible** fraction carries both
        // values — `screen.png` reads "3 of 5 habits done" and `code.html:92`
        // "3 of 5 items done". §8.7's printed hierarchy specified only the
        // numerator, which was a plan defect no ADR or ruling chose. Both values
        // already arrive in `HeroFigureState.Fraction`: no new data, no new token.
        assertTrue(
            "the fraction string carries the numerator and the denominator",
            strings.contains("<string name=\"today_hero_fraction\">%1\$d of %2\$d</string>"),
        )
        assertTrue(
            "both values are passed from the existing figure state",
            cardsCode.contains("stringResource(R.string.today_hero_fraction, done, total)"),
        )
        assertFalse(
            "the numerator-only form is the superseded §8.7 hierarchy",
            cardsCode.contains("R.string.today_hero_fraction, done)"),
        )
        assertTrue("the ring unit is the reference's done", strings.contains("<string name=\"today_progress_ring_unit\">done</string>"))
        assertTrue("the breakdown copy", strings.contains("%1\$d Completed") && strings.contains("%1\$d Pending"))
        assertTrue(
            "the completed control keeps its reversal (ADR-47 §21)",
            cardsCode.contains("completionActionLabel(activity, completed)") &&
                cardsCode.contains("CompletionAccent.SUCCESS"),
        )
        // ADR-49 Amendment Note 3 (task.md §10.8 Ruling A): `showIdleGlyph`
        // suppresses the glyph **while idle only**. The reference's pending
        // control is an empty ring and its completed control carries the check
        // inside the filled emerald disc, so the gate must be
        // `showIdleGlyph || completed` — never the bare parameter, which once
        // left every completed row as an unmarked emerald disc. Both states are
        // pinned here so it cannot regress silently again.
        assertTrue(
            "Home keeps the reference's empty idle ring",
            cardsCode.contains("showIdleGlyph = false"),
        )
        assertTrue(
            "the glyph must be suppressed only while idle, never in the done state",
            buttonCode.contains("val glyphShown = showIdleGlyph || completed"),
        )
        assertTrue(
            "the done state always draws the check glyph",
            buttonCode.contains("if (glyphShown) {"),
        )
        assertFalse(
            "the bare parameter must not gate the glyph again",
            buttonCode.contains("if (showIdleGlyph) {"),
        )
        // Exactly one of the control and the icon carries the label, in every
        // state: never a double announcement, never a silent control. Both
        // carriers are gated on the same `glyphShown`, so they are mutually
        // exclusive by construction.
        assertTrue(
            "the control carries the label when the glyph is absent",
            buttonCode.contains("Modifier.semantics { this.contentDescription = label }"),
        )
        assertTrue(
            "the icon carries the label when the glyph is drawn",
            buttonCode.contains("contentDescription = contentDescription"),
        )
        assertEquals(
            "exactly two carriers — the control's label and the icon — and both " +
                "gated on the glyph's presence",
            2,
            buttonCode.occurrences("if (glyphShown) {"),
        )
    }

    @Test
    fun `every band on the progress card is textPrimary and both Home status labels opt in`() {
        // Human ruling 2026-10-01 (`task.md` §13.1, option (a) adopted): this
        // card's wash paints `lerp(surface, auroraLavender, 0.55)` at
        // `heroFill` OVER the backdrop, so the *darkest* composited stop is the
        // governing case and `c.textSecondary` clears the ≥ 4.5:1 body-text
        // floor nowhere on it (3.91:1 at its lightest stop, 3.03:1 at its
        // darkest). The card's body text is therefore uniformly `textPrimary`:
        // the fraction, the all-done headline, the `items done` suffix, the
        // ring's `done` unit, the ring's percentage, the encouragement line and
        // BOTH halves of the breakdown row. This is an approved,
        // accessibility-driven deviation from the reference's `#475569`
        // hierarchy — pinned here so no future agent "restores" the slate
        // hierarchy and silently reintroduces the failure.
        val card = cardsCode
            .substringAfter("internal fun HomeProgressCard(")
            .substringBefore("internal fun HomeSectionHeader(")
        listOf(
            "R.string.today_hero_fraction" to "the fraction",
            "R.string.today_hero_complete" to "the all-done headline",
            "R.string.today_progress_suffix" to "the items-done suffix",
            "R.string.today_progress_ring_unit" to "the ring's done unit",
            "R.string.cd_progress_completed" to "the n Completed band",
            "R.string.cd_progress_pending" to "the n Pending band",
        ).forEach { (token, band) ->
            val at = card.indexOf(token)
            assertTrue("$band must be rendered", at >= 0)
            val inkAt = card.indexOf("color =", at)
            assertTrue("$band must carry an explicit ink", inkAt >= 0)
            assertEquals(
                "$band must read c.textPrimary — the card has no slate hierarchy",
                "color = c.textPrimary,",
                card.substring(inkAt, card.indexOf("\n", inkAt)).trim(),
            )
        }
        // The percentage inside the ring is body text too, and shares the one ink.
        val percentAt = card.indexOf("roundToInt()")
        assertTrue("the ring's percentage must be rendered", percentAt >= 0)
        val percentInkAt = card.indexOf("color =", percentAt)
        assertEquals(
            "the ring's percentage must read c.textPrimary",
            "color = c.textPrimary,",
            card.substring(percentInkAt, card.indexOf("\n", percentInkAt)).trim(),
        )
        // Nothing may survive inside the card at all — comments are already
        // stripped by `codeOf`, so this is a code-level statement.
        assertFalse(
            "no c.textSecondary may remain anywhere inside HomeProgressCard",
            card.contains("c.textSecondary"),
        )
        // The remedy is text + two Home-scoped material constants: the arc is
        // classified DECORATIVE and exempt (ADR-49 Amendment Note 5, task.md
        // §4.11 CHANGE 5), while the two breakdown dots are 3:1 non-text
        // boundaries and both take c.textPrimary.
        assertTrue(
            "the ring gradient keeps the approved three tokens",
            card.contains("listOf(c.success, c.auroraBlue, c.auroraViolet)"),
        )
        assertEquals(
            "both breakdown dots take c.textPrimary",
            2,
            card.occurrences(".background(c.textPrimary, CircleShape)"),
        )
        assertFalse(
            "the retired success / textMuted dots must stay gone",
            card.contains(".background(c.success, CircleShape)") ||
                card.contains(".background(c.textMuted, CircleShape)"),
        )
        // ADR-49 Amendment Note 5 (task.md §4.11 CHANGE 4): both Home status
        // bands now opt into textPrimary; the `null` default still resolves to
        // textSecondary for any future caller.
        val pendingHeader = screenCode
            .substringAfter("R.string.today_section_remaining")
            .substringBefore("items(remaining")
        assertTrue(
            "Pending's Scheduled Today opts into textPrimary",
            pendingHeader.contains("labelColor = c.textPrimary"),
        )
        assertTrue(
            "the default ink stays textSecondary for future callers",
            cardsCode.contains("labelColor: Color? = null") &&
                cardsCode.contains("val statusInk = labelColor ?: c.textSecondary"),
        )
        assertEquals(
            "both Home section headers pass textPrimary",
            2,
            screenCode.occurrences("labelColor = c.textPrimary"),
        )
    }

    @Test
    fun `the approved aura blobs breathe on the shared clock in the draw phase`() {
        // ADR-48 Amendment Note 6 (M2): positive assertions, replacing the
        // retired "the reference's permanent glow pulse is absent" clause.
        assertTrue("blob A's reference alpha", cardsCode.contains("AuraBlobAlphaA = 0.30f"))
        assertTrue("blob B's reference alpha", cardsCode.contains("AuraBlobAlphaB = 0.25f"))
        assertTrue("the cached blur radius is the reference's 32px", cardsCode.contains("AuraBlobRadius = 32.dp"))
        assertTrue("the radius breath is the reference's 32 -> 40px", cardsCode.contains("AuraRadiusBreath = 0.25f"))
        assertTrue("the negative offsets are cropped", cardsCode.contains("clipToBounds()"))
        assertTrue("the blobs are cached brushes", cardsCode.contains("Brush.radialGradient"))
        assertTrue("one brush per blob, no per-frame allocation", cardsCode.contains("drawWithCache"))
        assertFalse(
            "no blur filter may be introduced",
            cardsCode.contains("RenderEffect") || cardsCode.contains("Modifier.blur"),
        )
        // No animation instance may live inside a list item.
        assertEquals("no transition in HomeCards.kt", 0, cards.occurrences("rememberInfiniteTransition"))
        assertEquals("no animate state in HomeCards.kt", 0, cards.occurrences("animateFloatAsState"))
        assertEquals("no mutable state in HomeCards.kt", 0, cards.occurrences("mutableStateOf"))
        assertEquals("no effect in HomeCards.kt", 0, cards.occurrences("LaunchedEffect"))
        assertTrue(
            "the clock arrives as a State read only in the draw / layer phase",
            cardsCode.contains("ambientPulse: State<Float>?") &&
                cardsCode.contains("boltPulse: State<Float>?"),
        )
    }

    @Test
    fun `the section headings are the screen-local approved pair`() {
        // MomentumSectionHeader has 29 call sites, so Home's headings are
        // screen-local. R7: accepted and documented.
        val shared = File("src/main/java/com/momentum/app/presentation/components/MomentumComponents.kt")
        assertTrue("the shared header still exists for everyone else", shared.isFile)
        assertTrue(
            "the shared header is not modified by this task",
            shared.readText().contains("style = MaterialTheme.typography.titleMedium"),
        )
        assertFalse("Home no longer composes the shared header", screenCode.contains("MomentumSectionHeader("))
        assertTrue(cardsCode.contains("style = MomentumHeadlineSection"))
        assertTrue(cardsCode.contains("style = MomentumLabelMicro"))
        assertTrue(cardsCode.contains("semantics { heading() }"))
        // ADR-49 Amendment Note 3 (task.md §10.7): the emerald ink failed the
        // 4.5:1 floor on the status band, and `textSecondary` was rejected in
        // turn for the same method-inverted reason as the progress card. The two
        // labels now differ deliberately: `Scheduled Today` keeps the default
        // `textSecondary` (it sits on the bare atmosphere); `Finished` is
        // `textPrimary`. `accent` stays for the glyph alone.
        assertTrue(
            "the verified glyph becomes ink, never the accent",
            screenCode.contains("accent = c.textPrimary") &&
                cardsCode.contains("tint = accent"),
        )
        assertTrue(
            "both status labels are textPrimary, never the accent",
            screenCode.contains("labelColor = c.textPrimary") &&
                cardsCode.contains("color = statusInk"),
        )
        assertTrue(
            "the default ink stays textSecondary for future callers",
            cardsCode.contains("labelColor: Color? = null") &&
                cardsCode.contains("val statusInk = labelColor ?: c.textSecondary") &&
                !screenCode.contains("labelColor = c.textSecondary"),
        )
        assertFalse(
            "the label no longer derives its ink from the accent",
            cardsCode.contains("accent ?:"),
        )
        assertTrue(cardsCode.contains("accent: Color? = null"))
        assertTrue("the status glyph is the new verified badge", cardsCode.contains("MomentumIcons.verified"))
        assertTrue(
            "the pending status is a plain label",
            strings.contains("<string name=\"today_section_scheduled_today\">Scheduled Today</string>") &&
                strings.contains("<string name=\"today_section_finished\">Finished</string>"),
        )
    }

    @Test
    fun `the retired vertical lockup and the unbound keyframes stay gone`() {
        // Comments and imports are excluded: a KDoc mention is not code.
        val all = listOf(
            atmosphereCode,
            chromeCode,
            progressCode,
            cardsCode,
            screenCode,
        ).joinToString("\n")
        listOf(
            // The rejected TASK-017 vertical composition.
            "HeroProgressLockup",
            "HeroRingSize",
            "HeroOrbGap",
            "HeroProgressTag",
            // The pre-existing removed Hero machinery.
            "HeroReadabilityVeil",
            "VeilHeader",
            "VeilContent",
            "VeilOpen",
            "VeilSteps",
            "OpenBandWidth",
            "BottomBlend",
            "RingScale",
            "RingBounds",
            "ease01",
            "ArtworkScale",
            "ParallaxTravel",
            "ArtworkCrown",
            "HeaderProgressGap",
            "HomeHeroTag",
            "HeroArtworkTag",
            "HeroHeaderTag",
            "HeroContentTag",
            "collapsePx",
            "maxCollapsePx",
            "heroNestedScroll",
            "onCollapseRangePx",
            // Keyframes that are defined-but-unbound in code.html itself.
            "flame-pulse",
            "aurora-float",
        ).forEach { token ->
            assertFalse("retired machinery is back: $token", all.contains(token))
        }
        // The streak chip removed by the human ruling (§4.7 r3) leaves no trace.
        listOf("StreakBadge", "streak =", "Streak").forEach { token ->
            assertFalse("streak UI is back: $token", listOf(cardsCode, screenCode).any { it.contains(token) })
        }
        assertFalse(
            "no streak string may be added for Home's cards",
            strings.contains("today_card_streak"),
        )
    }

    @Test
    fun `no Home-specific dp literal survives outside the two named constants`() {
        // §8.2: only ArtworkHandOff (a restated measurement) and the
        // ChromeBottomInset alias are Home-specific spacing values; everything
        // else on the cards is a named token from the 4 dp grid.
        val literals = Regex("""\b(\d+(?:\.\d+)?)\.dp\b""")
            .findAll(cardsCode)
            .map { it.groupValues[1] }
            .toSet()
        assertEquals(
            "the cards' dp literals are exactly the approved reference values",
            setOf("144", "128", "48", "40", "32", "14", "8"),
            literals,
        )
        assertTrue("the chrome adds no dp literal at all", !chromeCode.contains(".dp"))
        assertTrue(
            "the atmosphere's only production dp value is the shared artwork's own hand-off",
            Regex("""(\d+(?:\.\d+)?)\.dp\b""")
                .findAll(atmosphereCode.substringBefore("@Preview"))
                .map { it.groupValues[1] }
                .toList() == listOf("88"),
        )
        assertTrue(atmosphereCode.contains("ArtworkHandOff: Dp = 88.dp"))
        assertTrue(
            "the four-stop scrim profile is untouched",
            atmosphereCode.contains("ScrimPeakStart = 0.20f") &&
                atmosphereCode.contains("ScrimPeakEnd = 0.80f"),
        )
    }

    @Test
    fun `every piece of Home copy over the atmosphere uses textPrimary`() {
        // ISSUE-020 remedy: the TEXT TOKEN, never re-inflating the veil. The
        // glass cards are exempt — they sit on their own fill, not on the
        // atmosphere — and HomeCards.kt legitimately uses textSecondary there.
        listOf("HomeChrome.kt", "HomeProgress.kt").forEach { file ->
            val code = codeOf(source("src/main/java/com/momentum/app/presentation/screens/today/$file"))
            assertFalse(
                "no textSecondary over the atmosphere in $file",
                code.contains("textSecondary"),
            )
        }
    }

    @Test
    fun `the Home atmosphere never selects artwork from the clock`() {
        listOf(
            "HeroAtmosphere",
            "home_hero_",
            "rememberHeroLocalTime",
            "LocalTime",
            "DAWN",
            "SUNSET",
        ).forEach { token ->
            assertFalse("time-of-day token still in the atmosphere: $token", atmosphere.contains(token))
        }
        assertFalse("Home owns no drawable of its own", atmosphere.contains("R.drawable"))
        assertTrue(
            "Home must draw the shared artwork system",
            atmosphere.contains("MomentumHeroArtwork("),
        )
        listOf("LocalTime", "java.time", "LocalDate").forEach { token ->
            assertFalse("the shared artwork must not read the clock: $token", shared.contains(token))
        }
    }

    @Test
    fun `shared artwork selection maps theme to the bundled aurora pair`() {
        assertTrue(shared.contains("R.drawable.habits_hero_light"))
        assertTrue(shared.contains("R.drawable.habits_hero_dark"))
        assertTrue(
            "selection must be a pure function of the theme",
            shared.contains("fun momentumHeroArtworkRes(dark: Boolean)"),
        )
        assertTrue(
            "the component must read the active theme",
            shared.contains("LocalMomentumColors.current.isDark"),
        )
        assertTrue(
            "the bundled pair must stay Android-resource-safe",
            File("src/main/res/drawable/habits_hero_light.webp").isFile &&
                File("src/main/res/drawable/habits_hero_dark.webp").isFile,
        )
    }

    @Test
    fun `the shared artwork contract is retained, not recreated`() {
        assertTrue(shared.contains("ContentScale.Crop"))
        assertTrue(shared.contains("contentDescription = null"))
        assertTrue(shared.contains("BlendMode.DstIn"))
        assertTrue("the declared top band must survive", shared.contains("topDissolve"))
        assertTrue("the bottom dissolve must survive", shared.contains("bottomDissolve"))
        assertTrue(
            "the artwork is drawn as supplied, never re-tinted by Home",
            !atmosphere.contains("ColorFilter") && !atmosphere.contains("tint ="),
        )
    }

    @Test
    fun `the atmosphere keeps the artwork procedural-free`() {
        listOf(
            "drawPath",
            "drawCircle",
            "quadraticTo",
            "cubicTo",
            "smoothstep",
            "farRidge",
            "nearRidge",
            "shorePath",
            "trailPath",
            "sunCenter",
        ).forEach { token ->
            assertTrue("procedural artwork token still present: $token", !atmosphere.contains(token))
        }
    }

    @Test
    fun `the greeting still follows the local clock`() {
        assertTrue("the time-aware greeting must survive", viewModel.contains("LocalTime.now()"))
        mapOf(
            "today_greeting_morning" to "Good morning",
            "today_greeting_afternoon" to "Good afternoon",
            "today_greeting_evening" to "Good evening",
            "today_greeting_night" to "Good night",
        ).forEach { (res, wording) ->
            assertTrue(
                "the ViewModel no longer names R.string.$res for a time band",
                viewModel.contains("R.string.$res"),
            )
            assertTrue("wording changed for $res", strings.contains(wording))
        }
    }

    @Test
    fun `the encouragement line is state-specific and the retired copy is gone`() {
        assertTrue(viewModel.contains("R.string.today_encouragement_streak"))
        assertTrue(viewModel.contains("R.string.today_encouragement_next"))
        assertTrue("the streak rule picks the greatest remaining streak", viewModel.contains("maxByOrNull"))
        assertTrue("the streak rule only fires from 2 days", viewModel.contains("currentStreak >= 2"))
        listOf(
            "today_message_",
            "today_celebrate_all_done",
            "hero_progress_count",
        ).forEach { retired ->
            assertFalse("retired string still named in the ViewModel: $retired", viewModel.contains(retired))
            assertFalse("retired string still in resources: $retired", strings.contains(retired))
        }
        assertTrue(strings.contains("today_hero_fraction"))
        assertTrue(strings.contains("today_hero_complete"))
        assertTrue(strings.contains("today_hero_progress_desc"))
        // The retired vertical lockup's copy leaves with the layout it served.
        listOf("today_hero_fraction_total", "today_hero_progress_label").forEach { retired ->
            assertFalse("the retired vertical lockup's copy must stay gone: $retired", strings.contains(retired))
        }
        // §4.7 r4: no domain change and no invented duration / entry-count copy.
        assertFalse(
            "no duration or entry-count copy may be invented for Home",
            strings.contains("15 mins") || strings.contains("3 entries"),
        )
    }

    private fun String.occurrences(token: String): Int =
        windowed(token.length).count { it == token }
}

/**
 * ADR-47 Amendment Note 5 §5 / ADR-47 Amendment Note 7 §2 and ADR-48 Amendment
 * Note 5: Home is **one** vertical scrolling surface with no nested scrolling
 * container, the persistent chrome is a fixed sibling above it rather than an
 * item of it, and the four layers are declared in the one order that satisfies
 * both the paint z-order and the TalkBack traversal order.
 */
class HomeScrollSurfaceTest {

    private fun screenSource(): String {
        val file = File("src/main/java/com/momentum/app/presentation/screens/today/TodayScreen.kt")
        assertTrue("missing Home screen source: ${file.absolutePath}", file.isFile)
        return file.readText()
    }

    /** Comments must never satisfy an assertion about rendered UI. */
    private fun codeOf(text: String): String = text
        .lines()
        .filterNot { it.trimStart().startsWith("//") || it.trimStart().startsWith("*") }
        .joinToString("\n")

    private fun String.occurrences(token: String): Int =
        windowed(token.length).count { it == token }

    private val screen = codeOf(screenSource())

    @Test
    fun `Home is one scroll surface with no nested scroll and no collapse`() {
        assertEquals(
            "Home must own exactly one LazyColumn — no nested list container",
            1,
            screen.occurrences("LazyColumn("),
        )
        assertFalse("a second scrollable would break the shared surface", screen.contains("verticalScroll("))
        // The scroll boundary is layout, not observation (Amendment Note 5 §5).
        assertFalse("no nested-scroll connection may survive", screen.contains("nestedScroll"))
        assertFalse("no scroll-driven collapse may survive", screen.contains("collapsePx"))
        assertFalse("no scroll observation may replace it", screen.contains("derivedStateOf"))
        assertFalse("no scroll position may be observed", screen.contains("onGloballyPositioned"))
        assertFalse("no parallax may be reintroduced", screen.contains("parallax"))
        assertFalse("the retired glass hero must not come back", screen.contains("TodayProgressHero"))
        assertFalse("the summary line moved into the hero", screen.contains("TodaySummary"))
        assertFalse("the deleted Hero must not come back", screen.contains("HomeHero("))
    }

    @Test
    fun `the four layers are declared in the contract order`() {
        val atmosphereAt = screen.indexOf("HomeAtmosphere(")
        val chromeAt = screen.indexOf("HomeTopChrome(")
        val regionAt = screen.indexOf("LazyColumn(")
        val fabAt = screen.indexOf("MomentumFab(")
        assertTrue("the atmosphere must be composed", atmosphereAt >= 0)
        assertTrue("the chrome must be composed", chromeAt >= 0)
        assertTrue("the scroll region must be composed", regionAt >= 0)
        assertTrue("the FAB must be composed", fabAt >= 0)
        // A Box composes and paints in declaration order, and the merged
        // semantics tree follows composition order — so this one order gives both
        // the paint z-order and date -> greeting -> Settings -> content -> FAB.
        assertTrue("the atmosphere is first", atmosphereAt < chromeAt)
        assertTrue("the chrome precedes the content", chromeAt < regionAt)
        assertTrue("the FAB is painted last, over the list's tail", regionAt < fabAt)
    }

    @Test
    fun `the scroll region begins exactly at the chrome's measured bottom edge`() {
        // Overlap is unrepresentable: the region is a SIBLING positioned by the
        // chrome's own reported height, and clipped as belt-and-braces.
        assertTrue(
            "the region must be inset by the measured chrome height",
            screen.contains(".padding(top = with(density) { chromeHeightPx.toDp() })"),
        )
        assertTrue(
            "the region must be clipped so no item can paint into the chrome",
            screen.contains(".clipToBounds()"),
        )
        assertTrue(
            "the chrome must report its measured height upward",
            screen.contains("onHeightChanged = { chromeHeightPx = it }"),
        )
        assertTrue(
            "the region must be tagged for bounds-level assertions",
            screen.contains("testTag(HomeScrollRegionTag)"),
        )
    }

    @Test
    fun `the header is the fixed chrome, not a Hero child and not a scroll item`() {
        val chrome = codeOf(
            File("src/main/java/com/momentum/app/presentation/screens/today/HomeChrome.kt").readText(),
        )
        assertFalse(
            "the retired Hero header must not come back",
            screen.contains("HomeHeader("),
        )
        assertEquals(
            "the chrome must be one sibling, not a list item",
            1,
            screen.occurrences("HomeTopChrome("),
        )
        // The chrome owns the copy the Hero used to own, and it is fixed.
        listOf(
            "dateLabel = state.dateLabel",
            "greeting = state.greeting.resolve()",
            "greetingSupport = stringResource(R.string.today_greeting_support)",
            "onOpenSettings = onOpenSettings",
            "revealProgress = chromeReveal",
        ).forEach { argument ->
            assertTrue("the chrome must receive $argument", screen.contains(argument))
        }
        listOf(
            "dateLabel: String",
            "greeting: String",
            "greetingSupport: String",
            "onOpenSettings: () -> Unit",
            "revealProgress: Float",
        ).forEach { parameter ->
            assertTrue("the chrome must declare $parameter", chrome.contains(parameter))
        }
    }

    @Test
    fun `Home renders both sections with the approved titles`() {
        assertTrue(
            "the Pending section heading is missing",
            screen.contains("stringResource(R.string.today_section_remaining)"),
        )
        assertTrue(
            "the Completed section heading is missing",
            screen.contains("stringResource(R.string.today_section_completed)"),
        )
        assertTrue(
            "the pending status label is missing",
            screen.contains("stringResource(R.string.today_section_scheduled_today)"),
        )
        assertTrue(
            "the completed status label is missing",
            screen.contains("stringResource(R.string.today_section_finished)"),
        )
        assertFalse("section counts are not part of the approved copy", screen.contains("Up next ·"))
        assertFalse("section counts are not part of the approved copy", screen.contains("Completed ·"))
        assertTrue("empty sections must be hidden", screen.contains("if (remaining.isNotEmpty())"))
        assertTrue("empty sections must be hidden", screen.contains("if (finished.isNotEmpty())"))
        assertFalse("no artificial item limit is allowed", screen.contains(".take("))
        assertFalse("no See All control is allowed", screen.contains("See All"))
        assertTrue(
            "the empty day hides both sections behind one empty state",
            screen.contains("if (state.isEmpty)"),
        )
    }

    @Test
    fun `the approved vertical rhythm is expressed from tokens`() {
        // ADR-48 Amendment Note 5: heading -> card and card -> card are the
        // reference's space-sm (8 dp); section -> section is its space-lg
        // (24 dp) = the list's 8 dp gap plus one standard spacer.
        assertTrue(
            "the list's arrangement must be the reference's space-sm",
            screen.contains("verticalArrangement = Arrangement.spacedBy(MomentumSpacing.tight)"),
        )
        assertEquals(
            "exactly one spacer separates the two sections",
            1,
            screen.occurrences("Spacer(Modifier.height(MomentumSpacing.standard))"),
        )
        assertFalse(
            "the retired 12 dp arrangement must stay gone",
            screen.contains("Arrangement.spacedBy(MomentumSpacing.item)"),
        )
    }
}
