package com.momentum.app.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The complete shape vocabulary (§6): 8 / 12 / 16 / 20 / 28 dp. No random
 * corner radii — screens pick a token, never a literal.
 */
object MomentumShapes {
    /** Small controls: icon tiles, day cells, thumbs. */
    val small = 8.dp

    /** Buttons, chips, fields. */
    val control = 12.dp

    /** Standard surfaces: cards, rows, panels. */
    val surface = 16.dp

    /** Feature surfaces: dialogs, sheets, hero panels. */
    val feature = 20.dp

    /** Hero / special progress elements. */
    val hero = 28.dp

    /** Pills: badges and filter chips only (never buttons). */
    val pill = 999.dp

    /** Material `Theme.shapes` mapping so M3 defaults never invent a radius. */
    val material = Shapes(
        small = RoundedCornerShape(small),
        medium = RoundedCornerShape(control),
        large = RoundedCornerShape(surface),
        extraLarge = RoundedCornerShape(hero),
    )
}

/**
 * Legacy radius names kept for screens that have not yet migrated to
 * [MomentumShapes]. Delegates to the v1 tokens — never a second source of
 * radius values.
 */
object MomentumRadii {
    val card: Dp get() = MomentumShapes.surface
    val control: Dp get() = MomentumShapes.control
    val small: Dp get() = MomentumShapes.small
    val dialog: Dp get() = MomentumShapes.feature
    val pill: Dp get() = MomentumShapes.pill
}
