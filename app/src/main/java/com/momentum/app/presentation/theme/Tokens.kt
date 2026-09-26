package com.momentum.app.presentation.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Central dimension tokens (§5). Base grid: **4dp**. Screens and components
 * read rhythm from here — no scattered arbitrary dp values in composables.
 * Default horizontal screen padding: [MomentumSpacing.screen] (20dp).
 */
object MomentumSpacing {
    /** 4dp — micro: hairline-adjacent nudges inside compound controls. */
    val micro = 4.dp

    /** 8dp — tight: gaps inside compound controls (chips, rows). */
    val tight = 8.dp

    /** 12dp — compact: gaps between cards / list items, row padding. */
    val compact = 12.dp

    /** 16dp — standard: card padding, standard gaps. */
    val standard = 16.dp

    /** 20dp — screen/content padding (default horizontal margin). */
    val screen = 20.dp

    /** 24dp — section: gaps between major sections. */
    val section = 24.dp

    /** 32dp — major section: separation between screen regions. */
    val majorSection = 32.dp

    /** 40dp — large separation. */
    val large = 40.dp

    /** 48dp — hero: dramatic vertical air around hero elements. */
    val hero = 48.dp

    /** 64dp — exceptional: reserved, near-empty breathing room. */
    val exceptional = 64.dp

    // Legacy aliases — delegate to the v1 grid (no second source of values).

    val item: Dp get() = compact
    val group: Dp get() = tight
    val card: Dp get() = standard
    val row: Dp get() = compact
}

/**
 * Control heights (§30 accessibility): interactive targets never fall
 * below 48dp.
 */
object MomentumHeights {
    /** Primary buttons and CTA rows. */
    val button = 52.dp

    /** Minimum touch height for chips and rows. */
    val controlMin = 48.dp

    /** Icon-only touch targets. */
    val iconTarget = 48.dp
}

/**
 * Icon sizes (§8): secondary/action 20dp, primary UI/navigation 24dp,
 * prominent contextual 28dp. Legacy 18/22dp names remain for unmigrated
 * call sites.
 */
object MomentumIconSizes {
    /** 20dp — secondary/action. */
    val secondary = 20.dp

    /** 24dp — primary UI/navigation. */
    val primary = 24.dp

    /** 28dp — prominent contextual icons. */
    val prominent = 28.dp

    // Legacy aliases.

    val small = 18.dp
    val medium: Dp get() = secondary
    val large: Dp get() = primary
    val hero: Dp get() = prominent
    val tile = 22.dp
}

/**
 * Liquid Glass translucency + light tokens (§7). Glass fills let the
 * ambient background breathe through; borders and top highlights sell the
 * depth. Values are alphas applied to theme slots — never hardcoded colors.
 */
object MomentumGlass {
    /** Primary hero surfaces (progress hero, nav bar). */
    const val heroFill = 0.68f

    /**
     * Modal dialog fill. **Opaque by design**: a dialog must establish a
     * strong, readable layer above the content behind it, so its surface never
     * lets the layer below bleed through into titles, values or input fields.
     */
    const val dialogFill = 1.0f

    /** Standard cards, rows, panels. */
    const val cardFill = 0.55f

    /** Chips, badges, tiles, controls. */
    const val controlFill = 0.45f

    /** Hairline border highlight (white in dark mode). */
    const val borderDark = 0.16f

    /** Hairline border (ink) in light mode, where white is invisible. */
    const val borderLight = 0.10f

    /** Top-edge illumination streak. */
    const val highlight = 0.10f

    /**
     * Window dim behind a modal dialog, de-emphasising the layer it covers.
     * Compose's `Dialog` draws no scrim of its own (b/159900354), so the dim
     * is applied to the dialog window explicitly — the dialog surface itself
     * stays at full theme brightness.
     */
    const val dialogScrim = 0.45f

    /** Ambient light field opacity over the base background. */
    const val ambientDark = 0.13f
    const val ambientLight = 0.16f

    /** Soft shadow elevation for floating layers (dialogs, nav, hero). */
    val dialogElevation = 16.dp
    val heroElevation = 12.dp

    /**
     * True backdrop blur is deliberately NOT used: no reliable cross-device
     * API exists on our minSdk, and no fragile dependency is worth it.
     * Depth comes from translucency + highlight + shadow + ambient light,
     * so every screen stays beautiful with blur unavailable.
     */
    val backdropBlur = 0.dp
}
