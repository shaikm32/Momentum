package com.momentum.app.presentation.screens.today

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Guards the revised Home Hero artwork contract (DECISIONS ADR-30): the Hero
 * draws the **shared** Momentum Aurora artwork selected by the application
 * theme only, the retired time-of-day system (four `home_hero_*` WebPs plus
 * the `HeroAtmosphere` clock bands) is gone from both resources and code, and
 * the greeting remains the one element that follows the local clock. Pure file
 * checks — no device or resource loading required.
 */
class HomeHeroAssetsTest {

    private fun source(path: String): String {
        val file = File(path)
        assertTrue("missing source: ${file.absolutePath}", file.isFile)
        return file.readText()
    }

    private val hero = source(
        "src/main/java/com/momentum/app/presentation/screens/today/HomeHero.kt",
    )

    private val shared = source(
        "src/main/java/com/momentum/app/presentation/components/MomentumHeroArtwork.kt",
    )

    private val viewModel = source(
        "src/main/java/com/momentum/app/presentation/screens/today/TodayViewModel.kt",
    )

    /** Reads a `private val <name> = <n>.dp` constant, so tests assert the visual budget. */
    private fun dpConstant(text: String, name: String): Float? =
        Regex("""private val $name = ([0-9.]+)dp""").find(text)?.groupValues?.get(1)?.toFloatOrNull()

    @Test
    fun `the retired time-of-day home hero assets are gone from the project`() {
        val dir = File("src/main/res/drawable")
        assertTrue("missing res dir: ${dir.absolutePath}", dir.isDirectory)
        val leftovers = dir.listFiles { f -> f.name.startsWith("home_hero") }.orEmpty()
        assertTrue(
            "obsolete time-of-day assets still present: ${leftovers.map { it.name }}",
            leftovers.isEmpty(),
        )
        assertFalse(
            "the retired atmosphere engine must stay deleted",
            File("src/main/java/com/momentum/app/domain/engine/HeroAtmosphere.kt").exists(),
        )
    }

    @Test
    fun `the Home Hero never selects artwork from the clock`() {
        listOf(
            "HeroAtmosphere",
            "home_hero_",
            "rememberHeroLocalTime",
            "LocalTime",
            "DAWN",
            "SUNSET",
        ).forEach { token ->
            assertFalse("time-of-day token still in HomeHero: $token", hero.contains(token))
        }
        assertFalse("the Hero owns no drawable of its own", hero.contains("R.drawable"))
        assertTrue(
            "the Hero must draw the shared artwork system",
            hero.contains("MomentumHeroArtwork("),
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
    fun `the artwork is decorative, cover-cropped and dissolved at both edges`() {
        assertTrue(shared.contains("ContentScale.Crop"))
        assertTrue(shared.contains("contentDescription = null"))
        assertTrue(shared.contains("BlendMode.DstIn"))
        assertTrue("top dissolve missing from the shared artwork", shared.contains("topDissolve"))
        assertTrue("bottom dissolve missing from the shared artwork", shared.contains("bottomDissolve"))
        assertTrue(
            "HomeHero must own the dissolve bands so it can close the gap under the greeting",
            hero.contains("topDissolve = TopDissolve") && hero.contains("bottomDissolve = BottomDissolve"),
        )
        assertTrue(
            "the Hero keeps the illustration behind its copy",
            hero.contains("alignment = Alignment.BottomCenter"),
        )
    }

    @Test
    fun `the whole Home hero foreground sits inside the single artwork surface`() {
        // The artwork is the Hero's FIRST layer, and the complete copy — date,
        // greeting, Settings, ring, count, message — is rendered above it
        // (ADR-31). Habits is the reference composition: copy inside the
        // artwork's own Box, never beside it.
        val artworkAt = hero.indexOf("MomentumHeroArtwork(")
        val headerAt = hero.indexOf("HeroHeader(")
        val contentAt = hero.indexOf("HeroContent(")
        assertTrue("the Hero must draw the shared artwork", artworkAt >= 0)
        assertTrue("the Hero must render its own header", headerAt >= 0)
        assertTrue("the Hero must render its own content block", contentAt >= 0)
        assertTrue("the artwork must be the first layer", artworkAt < headerAt)
        assertTrue("the header must sit on the artwork", headerAt < contentAt)

        listOf(
            "HeroHeader(",
            "HeroContent(",
            "ProgressOrb(",
            "LiquidGlassIconButton(",
            "HeroReadabilityVeil(",
        ).forEach { layer -> assertTrue("missing Hero layer: $layer", hero.contains(layer)) }
        listOf("dateLabel", "greeting", "onOpenSettings", "doneCount", "message").forEach {
            assertTrue("the Hero must render $it", hero.contains(it))
        }

        // The artwork covers the Hero's whole box — never a child strip.
        assertTrue(
            "the artwork must fill the Hero's bounds",
            Regex("""MomentumHeroArtwork\([\s\S]{0,400}?Modifier\s*\.fillMaxSize\(\)""").containsMatchIn(hero),
        )
        assertFalse("no fixed-height artwork container is allowed", hero.contains("height(Artwork"))
        val artworkCall = hero.substring(hero.indexOf("MomentumHeroArtwork(")).take(400)
        assertFalse(
            "the artwork must not be inset from the Hero's own bounds",
            artworkCall.substringBefore("modifier = Modifier.fillMaxSize()").contains("padding("),
        )
    }

    @Test
    fun `the hero never flattens the artwork behind a blanket veil`() {
        assertTrue("the Hero must shape readability around its copy", hero.contains("HeroReadabilityVeil("))
        assertTrue("the veil must be one smooth gradient", hero.contains("Brush.verticalGradient("))

        val peaks = Regex("""Veil(?:Header|Content)(?:Dark|Light) = ([0-9.]+)f""")
            .findAll(hero)
            .map { it.groupValues[1].toFloat() }
            .toList()
        assertTrue("veil peaks not found", peaks.isNotEmpty())
        assertTrue(
            "a veil this strong erases the Aurora; peaks were $peaks",
            peaks.all { it <= 0.65f },
        )
        assertTrue("the open band between greeting and ring must stay near-untouched", hero.contains("VeilOpen"))
        // The blanket-veil failure mode must not come back.
        assertFalse("the old 0.86 blanket veil must stay gone", hero.contains("0.86f"))
    }

    @Test
    fun `no band of page background sits above the artwork or below the message`() {
        // Visual contract (ADR-32): the artwork owns the Hero's whole top edge,
        // so there is no top ramp and no inset that exposes the surface above
        // the date; and the Hero ends shortly after the message, so the bottom
        // ramp is a short blend rather than a void.
        assertTrue(
            "Home must not fade the artwork out at its top edge",
            hero.contains("private val TopDissolve = 0.dp"),
        )
        val topInset = dpConstant(hero, "HeroTopInset")
        val bottomInset = dpConstant(hero, "HeroBottomInset")
        val bottomDissolve = dpConstant(hero, "BottomDissolve")
        val headroom = dpConstant(hero, "CollapseHeadroom")

        assertTrue("the top inset is missing", topInset != null)
        assertTrue("HeroTopInset must be small, was $topInset", topInset!! <= 24f)
        assertTrue("the message must end above the bottom ramp", bottomInset!! >= bottomDissolve!!)
        assertTrue(
            "the Hero must not trail dead space below the message, was $bottomInset",
            bottomInset <= 56f,
        )
        assertTrue(
            "the Hero's extra height is the open band above the content, not a void",
            headroom != null && headroom in 32f..160f,
        )
        // All of the extra height must go into the single weighted spacer that
        // sits ABOVE the content block, never below it.
        val column = hero.substringAfter("Spacer(Modifier.height(HeaderContentGap))")
        val above = column.indexOf("Modifier.weight(1f)")
        val content = column.indexOf("HeroContent(")
        val below = column.lastIndexOf("Modifier.height(HeroBottomInset)")
        assertTrue("the open band must be weighted", above in 0 until content)
        assertTrue(
            "nothing weighted may sit below the content: that is the void",
            !column.substring(content, below).contains("Modifier.weight(1f)"),
        )
        // Height is content-driven: no screen-percentage floor that could inflate
        // the Hero into empty background.
        assertFalse(
            "the Hero height must come from its content, not a screen percentage",
            hero.contains("screenHeightDp"),
        )
    }

    @Test
    fun `the hero is flush with the top of the scroll surface`() {
        val screen = File("src/main/java/com/momentum/app/presentation/screens/today/TodayScreen.kt")
        assertTrue("missing Home screen source", screen.isFile)
        val code = screen.readText()
            .lines()
            .filterNot { it.trimStart().startsWith("//") || it.trimStart().startsWith("*") }
            .joinToString("\n")
        assertTrue(
            "the scroll surface must not push the Hero down from the top (ADR-32)",
            code.contains("top = 0.dp"),
        )
        assertFalse(
            "a top content padding would expose page background above the Aurora",
            code.contains("top = MomentumSpacing.card"),
        )
    }

    @Test
    fun `the greeting still follows the local clock`() {
        assertTrue("the time-aware greeting must survive", viewModel.contains("LocalTime.now()"))
        listOf("Good morning", "Good afternoon", "Good evening", "Good night").forEach {
            assertTrue("greeting missing: $it", viewModel.contains(it))
        }
    }

    @Test
    fun `HomeHero keeps the artwork procedural-free and non-nested`() {
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
            assertTrue("procedural artwork token still present: $token", !hero.contains(token))
        }
        // The Hero must stay a plain item of Home's single scroll surface.
        assertTrue("the Hero must read its collapse progress", hero.contains("collapseProgress"))
        assertTrue("the Hero must report its collapse range", hero.contains("onCollapseRangePx"))
    }
}

/**
 * DASHBOARD_SPECS §4/§8 / DECISIONS §8: header, Hero and both activity
 * sections share **one** vertical scrolling surface, with no nested scrolling
 * container for the list and no second scrollable anywhere on Home.
 */
class HomeScrollSurfaceTest {

    private fun screenSource(): String {
        val file = File("src/main/java/com/momentum/app/presentation/screens/today/TodayScreen.kt")
        assertTrue("missing Home screen source: ${file.absolutePath}", file.isFile)
        return file.readText()
    }

    private fun codeOf(text: String): String = text
        .lines()
        .filterNot { it.trimStart().startsWith("//") || it.trimStart().startsWith("*") }
        .joinToString("\n")

    private fun String.occurrences(token: String): Int =
        windowed(token.length).count { it == token }

    @Test
    fun `Home is one scroll surface with the hero as an item of it`() {
        val code = codeOf(screenSource())

        assertEquals(
            "Home must own exactly one LazyColumn — no nested list container",
            1,
            code.occurrences("LazyColumn("),
        )
        assertTrue("the single scroll surface must host the hero collapse", code.contains(".nestedScroll(heroNestedScroll)"))
        assertFalse("a second scrollable would break the shared surface", code.contains("verticalScroll("))
        assertTrue("the hero must be an item of that LazyColumn", code.contains("HomeHero("))
        assertFalse("the retired glass hero must not come back", code.contains("TodayProgressHero"))
        assertFalse("the summary line moved into the hero", code.contains("TodaySummary"))
    }

    @Test
    fun `the header lives inside the hero, not beside it as a separate surface`() {
        val code = codeOf(screenSource())

        assertFalse(
            "the header must not be a sibling of the hero again (ADR-31)",
            code.contains("HomeHeader("),
        )
        assertEquals("the hero must be one item of the single scroll surface", 1, code.occurrences("HomeHero("))
        // The hero owns the header copy, so the artwork really backs all of it.
        listOf(
            "dateLabel = state.dateLabel",
            "greeting = state.greeting",
            "onOpenSettings = onOpenSettings",
        ).forEach { argument ->
            assertTrue("the hero must receive $argument", code.contains(argument))
        }
        assertFalse(
            "no wrapper may split the header from the hero",
            code.contains("Column {"),
        )
    }

    @Test
    fun `Home renders both sections with the fixed spec titles`() {
        val code = codeOf(screenSource())

        assertTrue("Remaining today section missing", code.contains("\"Remaining today\""))
        assertTrue("Completed today section missing", code.contains("\"Completed today\""))
        assertFalse("section counts are not part of the spec titles", code.contains("Up next ·"))
        assertFalse("section counts are not part of the spec titles", code.contains("Completed ·"))
        assertTrue("empty sections must be hidden", code.contains("if (remaining.isNotEmpty())"))
        assertTrue("empty sections must be hidden", code.contains("if (finished.isNotEmpty())"))
        assertFalse("no artificial item limit is allowed", code.contains(".take("))
        assertFalse("no See All control is allowed", code.contains("See All"))
    }
}
