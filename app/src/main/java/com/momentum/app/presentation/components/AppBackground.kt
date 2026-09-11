package com.momentum.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.momentum.app.presentation.theme.MomentumThemeColors

/**
 * Calm canvas for every screen: a solid theme background. The app is designed
 * to look intentional with no gradients at all; gradient accents live only in
 * hero/progress moments on individual screens.
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
            .background(c.backgroundBottom),
    ) {
        content()
    }
}
