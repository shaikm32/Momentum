package com.momentum.app.presentation.screens.habits

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Guards the shared Momentum hero artwork contract (DECISIONS ADR-30): the two
 * bundled Aurora illustrations (`habits_hero_light` / `habits_hero_dark`) are
 * now the **shared** Light/Dark artwork for every major Hero surface, live at
 * their native dimensions under Android-resource-safe (lowercase) names with no
 * legacy variants, and the Habits header consumes them through the shared
 * component with none of the removed procedural-landscape drawing code left
 * behind. Pure file checks — no device or resource loading required.
 */
class HabitsHeroAssetsTest {

    private fun drawableDir(): File {
        // Unit-test workers run with the module dir as working directory.
        val dir = File("src/main/res/drawable")
        assertTrue("missing res dir: ${dir.absolutePath}", dir.isDirectory)
        return dir
    }

    private fun heroSource(): String {
        val file = File("src/main/java/com/momentum/app/presentation/screens/habits/HabitsHero.kt")
        assertTrue("missing hero source: ${file.absolutePath}", file.isFile)
        return file.readText()
    }

    private fun requireWebp(dir: File, name: String): ByteArray {
        val file = File(dir, "$name.webp")
        assertTrue("missing asset: ${file.absolutePath}", file.isFile)
        val bytes = file.readBytes()
        assertTrue("$name is too small to be real artwork", bytes.size > 10 * 1024)
        assertEquals("$name is not RIFF", "RIFF", bytes.sliceArray(0..3).toString(Charsets.US_ASCII))
        assertEquals("$name is not WEBP", "WEBP", bytes.sliceArray(8..11).toString(Charsets.US_ASCII))
        return bytes
    }

    /** Lossy VP8 frame header: width/height at bytes 26-29 (14 bits each). */
    private fun vp8Dimensions(bytes: ByteArray): Pair<Int, Int> {
        assertEquals("expected lossy VP8 frame", "VP8 ", bytes.sliceArray(12..15).toString(Charsets.US_ASCII))
        val w = ((bytes[27].toInt() and 0xFF) shl 8 or (bytes[26].toInt() and 0xFF)) and 0x3FFF
        val h = ((bytes[29].toInt() and 0xFF) shl 8 or (bytes[28].toInt() and 0xFF)) and 0x3FFF
        return w to h
    }

    @Test
    fun `both hero artworks are valid WebP at matching dimensions`() {
        val dir = drawableDir()
        val expected = 1024 to 1536
        assertEquals(expected, vp8Dimensions(requireWebp(dir, "habits_hero_light")))
        assertEquals(expected, vp8Dimensions(requireWebp(dir, "habits_hero_dark")))
    }

    @Test
    fun `hero artwork names are Android resource safe with no legacy variants`() {
        val dir = drawableDir()
        val heroFiles = dir.listFiles { f -> f.name.contains("habits", ignoreCase = true) }.orEmpty()
        assertTrue(
            "expected exactly the two lowercase hero assets, found: ${heroFiles.map { it.name }}",
            heroFiles.map { it.name }.toSet() == setOf("habits_hero_light.webp", "habits_hero_dark.webp"),
        )
        heroFiles.forEach { file ->
            assertTrue(
                "hero asset name is not resource-safe: ${file.name}",
                file.nameWithoutExtension.matches(Regex("[a-z0-9_]+")),
            )
        }
    }

    @Test
    fun `HabitsHero renders the shared artwork and no procedural landscape`() {
        val code = heroSource()
        assertTrue(
            "the Habits header must draw through the shared Momentum hero artwork (ADR-30)",
            code.contains("MomentumHeroArtwork("),
        )
        assertFalse(
            "screen-specific artwork selection is retired",
            code.contains("R.drawable"),
        )
        // The removed Canvas landscape (sky/sun/ridges/lake/shore/trail) may not return.
        listOf(
            "drawPath",
            "drawCircle",
            "quadraticTo",
            "cubicTo",
            "StrokeCap",
            "smoothstep",
            "farRidge",
            "nearRidge",
            "shorePath",
            "trailPath",
            "sunCenter",
        ).forEach { token ->
            assertTrue("procedural artwork token still present: $token", !code.contains(token))
        }
    }

    @Test
    fun `the Habits header keeps its own composition`() {
        val code = heroSource()
        // ADR-31: the Habits hero is the reference and must not be pulled into
        // Home's layout — its own dissolve bands, its own (unveiled) treatment.
        assertTrue(code.contains("private val TopDissolve = 44.dp"))
        assertTrue(code.contains("private val BottomDissolve = 88.dp"))
        assertFalse(
            "Home's readability veil must not leak into the Habits header",
            code.contains("HeroReadabilityVeil"),
        )
        assertFalse(
            "the Habits header must not gain Home's header/content blocks",
            code.contains("HeroContent(") || code.contains("HeroHeader("),
        )
    }
}
