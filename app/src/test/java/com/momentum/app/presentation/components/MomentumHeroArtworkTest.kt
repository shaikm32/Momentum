package com.momentum.app.presentation.components

import com.momentum.app.R
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Shared Momentum hero artwork (DECISIONS ADR-30): one Light/Dark aurora pair
 * behind every major Hero/Header surface, selected by the application theme —
 * never by the clock — and rendered as decorative, edge-dissolved background.
 * Pure file checks plus the selection function itself; no device or resource
 * decoding required.
 */
class MomentumHeroArtworkTest {

    private val source: String = run {
        val file = File("src/main/java/com/momentum/app/presentation/components/MomentumHeroArtwork.kt")
        assertTrue("missing shared artwork source: ${file.absolutePath}", file.isFile)
        file.readText()
    }

    @Test
    fun `light theme resolves to the light artwork and dark theme to the dark artwork`() {
        assertEquals(R.drawable.habits_hero_light, momentumHeroArtworkRes(dark = false))
        assertEquals(R.drawable.habits_hero_dark, momentumHeroArtworkRes(dark = true))
    }

    @Test
    fun `both bundled artworks exist as resource-safe webp files`() {
        val dir = File("src/main/res/drawable")
        listOf("habits_hero_light", "habits_hero_dark").forEach { name ->
            val file = File(dir, "$name.webp")
            assertTrue("missing shared artwork: ${file.absolutePath}", file.isFile)
            assertTrue("$name must be real artwork", file.length() > 10 * 1024)
            assertTrue(
                "$name must be Android-resource-safe",
                file.nameWithoutExtension.matches(Regex("[a-z0-9_]+")),
            )
        }
        val legacy = dir.listFiles { f -> f.name.startsWith("home_hero") }.orEmpty()
        assertTrue("retired time-of-day artwork is back: ${legacy.map { it.name }}", legacy.isEmpty())
    }

    @Test
    fun `the mask is full strength from the top with no top fade`() {
        // ADR-39 / §7.7: the profile is α = 1 from the container's top edge and
        // the only fade is the caller's bottomDissolve. Source-level stop-list
        // assertion — the JVM unit test cannot rasterize the DstIn mask, so it
        // pins the exact stops that produce the geometry.
        assertTrue(
            "the profile must start opaque at the container top",
            source.contains("0f to Color.White"),
        )
        assertTrue(
            "the bottom band must start opaque",
            source.contains("bottomStart to Color.White"),
        )
        assertTrue(
            "the single transparent stop must be the terminal 1f stop",
            source.contains("1f to Color.Transparent") &&
                source.split("to Color.Transparent").size - 1 == 1,
        )
        assertFalse(
            "a top-fade transparent stop at 0f is back",
            source.contains("0f to Color.Transparent"),
        )
        assertTrue(
            "topDissolve must remain a parameter (ADR-30 caller contract)",
            source.contains("topDissolve: Dp"),
        )
        assertTrue(
            "bottom blend bounds must stay at floor 0.10 / ceiling 0.95",
            source.contains("coerceIn(0.10f, 0.95f)"),
        )
    }

    @Test
    fun `a top fade at the status-bar inset may not return`() {
        // Regression guard for ISSUE-017 / A4(h): the old implementation read
        // the status-bar inset and built a topEnd ramp whose transparent stop
        // at y=0 reproduced the pale band. All of that machinery must stay gone
        // — the declared top band is absorbed at the window edge (ADR-39).
        listOf("statusBars", "statusBarTop", "topEnd", "TopDissolveMax").forEach { token ->
            assertFalse("top-fade machinery is back: $token", source.contains(token))
        }
    }

    @Test
    fun `the artwork is decorative, cover-cropped and dissolved at both edges`() {
        assertTrue("cover crop required", source.contains("ContentScale.Crop"))
        assertTrue("artwork must be silent to screen readers", source.contains("contentDescription = null"))
        assertTrue("edge dissolve missing", source.contains("BlendMode.DstIn"))
        assertTrue("top dissolve band missing", source.contains("topDissolve"))
        assertTrue("bottom dissolve band missing", source.contains("bottomDissolve"))
        assertTrue(
            "bottom anchoring keeps the quiet third of the art behind the copy",
            source.contains("alignment: Alignment = Alignment.BottomCenter"),
        )
        assertTrue("zoom and parallax must be parameters, not constants", source.contains("parallax: Dp"))
    }

    @Test
    fun `the shared artwork system never reads the clock`() {
        listOf("LocalTime", "LocalDate", "java.time", "HeroAtmosphere").forEach { token ->
            assertFalse("clock token in the shared artwork: $token", source.contains(token))
        }
        assertTrue(
            "artwork selection must be a pure function of the theme",
            source.contains("fun momentumHeroArtworkRes(dark: Boolean)"),
        )
    }
}
