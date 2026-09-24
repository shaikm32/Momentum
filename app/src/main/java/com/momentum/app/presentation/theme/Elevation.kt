package com.momentum.app.presentation.theme

import androidx.compose.ui.unit.dp

/**
 * Surface depth model (§7): Canvas → Surface → Elevated → Interactive.
 * Values stay deliberately small — Heavy Material elevation shadows are
 * rejected. Dark Aurora expresses depth through tonal contrast, subtle
 * borders, and atmospheric glow instead of shadow.
 */
object MomentumElevation {
    /** Canvas / flat surfaces. */
    val none = 0.dp

    /** Resting surfaces with a whisper of shadow (Light Aurora). */
    val subtle = 1.dp

    /** Elevated surfaces: cards lifted above the canvas. */
    val raised = 4.dp

    /** Floating layers only: dialogs, floating controls. */
    val floating = 8.dp
}
