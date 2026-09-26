package com.momentum.app.presentation.screens.today

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Reach a Target dialog surface (DASHBOARD_SPECS §10.2, AGENTS §9 contrast):
 * the dialog must stand on an **opaque** Momentum surface so its title, target,
 * input and actions stay readable whatever Home is doing behind it, while the
 * layer behind is pushed back with a window dim — never by darkening the
 * dialog or the whole screen.
 */
class TargetEntryDialogSurfaceTest {

    private fun source(path: String): String {
        val file = File(path)
        assertTrue("missing source: ${file.absolutePath}", file.isFile)
        return file.readText()
    }

    private val dialog = source(
        "src/main/java/com/momentum/app/presentation/screens/today/TargetEntryDialog.kt",
    )

    private val glass = source(
        "src/main/java/com/momentum/app/presentation/components/LiquidGlass.kt",
    )

    private val tokens = source(
        "src/main/java/com/momentum/app/presentation/theme/Tokens.kt",
    )

    @Test
    fun `the dialog sits on an opaque surface level rather than translucent hero glass`() {
        assertTrue("the dialog must opt into the dialog surface", dialog.contains("GlassLevel.DIALOG"))
        assertFalse("translucent hero glass is what made Home show through", dialog.contains("GlassLevel.HERO"))
        assertTrue("the dialog level must exist in the shared glass system", glass.contains("DIALOG(MomentumGlass.dialogFill"))

        val fill = requireNotNull(
            Regex("""dialogFill = ([0-9.]+)f""").find(tokens)?.groupValues?.get(1)?.toFloatOrNull(),
        ) { "MomentumGlass.dialogFill token missing" }
        assertTrue("the dialog surface must be opaque, was $fill", fill >= 0.98f)
    }

    @Test
    fun `the opaque surface keeps its brightest edge opaque too`() {
        assertTrue("solid levels must be recognised", glass.contains("solid = level.fillAlpha >= 1f"))
        assertTrue(
            "the top-edge streak must be baked into an opaque stop, not painted translucent",
            glass.contains("solid -> c.surface"),
        )
    }

    @Test
    fun `the layer behind the dialog is dimmed instead of the dialog being darkened`() {
        assertTrue("Compose draws no scrim of its own — the window must be dimmed",
            dialog.contains("DialogWindowProvider"))
        assertTrue("the dim flag must be applied", dialog.contains("FLAG_DIM_BEHIND"))
        assertTrue("the dim must read a design-system token", dialog.contains("MomentumGlass.dialogScrim"))
        val scrim = requireNotNull(
            Regex("""dialogScrim = ([0-9.]+)f""").find(tokens)?.groupValues?.get(1)?.toFloatOrNull(),
        ) { "MomentumGlass.dialogScrim token missing" }
        assertTrue("the dim must actually de-emphasise Home", scrim > 0f && scrim < 1f)
    }

    @Test
    fun `copy, input and actions keep their design-system contrast tokens`() {
        assertTrue("target value reads primary ink", dialog.contains("color = c.textPrimary"))
        assertTrue("input keeps themed field colors", dialog.contains("glassFieldColors()"))
        assertTrue("supporting progress text is themed", dialog.contains("c.success"))
        assertTrue("actions are real Material buttons", dialog.split("TextButton(").size - 1 >= 3)
    }
}
