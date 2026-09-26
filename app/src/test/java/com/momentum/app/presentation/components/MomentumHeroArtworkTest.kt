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
    fun `a zero band means no fade on that edge`() {
        // Home asks for no top ramp at all; Habits passes 44 dp and must keep the
        // full dissolve profile. The component therefore has to honour 0 as "no
        // fade" instead of coercing it to a visible minimum.
        assertTrue(
            "a 0.dp band must be honoured as no fade",
            source.contains("if (topDissolve <= 0.dp)") && source.contains("0f\n                    }"),
        )
        assertTrue(
            "the ramp stops are built conditionally so a zero band adds no transparent stop",
            source.contains("if (topEnd > 0f)"),
        )
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
