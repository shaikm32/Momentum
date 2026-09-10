package com.momentum.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.momentum.app.presentation.theme.MomentumThemeColors

/**
 * Layered gradient canvas for every screen. Establishes the visual
 * direction: rich gradient + subtle depth instead of flat gray surfaces.
 */
@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val c = MomentumThemeColors.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(c.backgroundTop, c.backgroundBottom))),
    ) {
        content()
    }
}
