package com.momentum.app.presentation.screens.onboarding

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Guards the 4-screen Twilight onboarding asset contract: exactly the four
 * `onboarding_screen_0X` WebPs back the flow at their native dimensions, and
 * no obsolete onboarding asset or code reference remains (5-screen era
 * backgrounds/icons, per-icon screen-2 composition, procedural overlays).
 * Pure file checks — no device or resource loading required.
 */
class OnboardingAssetsTest {

    private fun drawableDir(): File {
        // Unit-test workers run with the module dir as working directory.
        val dir = File("src/main/res/drawable")
        assertTrue("missing res dir: ${dir.absolutePath}", dir.isDirectory)
        return dir
    }

    private fun onboardingDir(): File {
        val dir = File("src/main/java/com/momentum/app/presentation/screens/onboarding")
        assertTrue("missing onboarding dir: ${dir.absolutePath}", dir.isDirectory)
        return dir
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
        assertEquals("expected lossy VP8 background", "VP8 ", bytes.sliceArray(12..15).toString(Charsets.US_ASCII))
        val w = ((bytes[27].toInt() and 0xFF) shl 8 or (bytes[26].toInt() and 0xFF)) and 0x3FFF
        val h = ((bytes[29].toInt() and 0xFF) shl 8 or (bytes[28].toInt() and 0xFF)) and 0x3FFF
        return w to h
    }

    @Test
    fun `all four onboarding artworks are valid WebP at native dimensions`() {
        val dir = drawableDir()
        val expected = mapOf(
            "onboarding_screen_01" to (852 to 1846),
            "onboarding_screen_02" to (852 to 1846),
            "onboarding_screen_03" to (852 to 1846),
            "onboarding_screen_04" to (950 to 1655),
        )
        expected.forEach { (name, size) ->
            assertEquals(name, size, vp8Dimensions(requireWebp(dir, name)))
        }
    }

    @Test
    fun `no obsolete onboarding assets or references remain`() {
        val dir = drawableDir()
        // No fifth screen, no previous backgrounds/icons may linger on disk.
        val obsoleteFiles = listOf(
            "onboarding_01_sunrise_lake",
            "onboarding_02_soft_clouds",
            "onboarding_03_aurora_mist",
            "onboarding_04_glass_orb",
            "onboarding_05_twilight_peak",
            "onboarding_activity_icon",
            "onboarding_learning_icon",
            "onboarding_wellness_icon",
            "onboarding_health_icon",
            "onboarding_screen2_icons",
            "healthy_habits_icon",
            "greater_focus_icon",
            "less_stress_icon",
            "better_you_icon",
        )
        obsoleteFiles.forEach { name ->
            assertTrue(
                "obsolete asset still present: $name",
                dir.listFiles { f -> f.nameWithoutExtension == name }.isNullOrEmpty(),
            )
        }
        // ...nor referenced from code.
        val code = onboardingDir().listFiles { f -> f.extension == "kt" }!!
            .joinToString("\n") { it.readText() }
        (obsoleteFiles + listOf("WELCOME", "FEATURES", "PROGRESS", "BENEFITS", "FINAL")).forEach { token ->
            assertTrue("obsolete reference still present: $token", !code.contains(token))
        }
        // Exactly the four new artworks are referenced.
        (1..4).forEach { n ->
            assertTrue(
                "missing reference to onboarding_screen_0$n",
                code.contains("R.drawable.onboarding_screen_0$n"),
            )
        }
    }
}
