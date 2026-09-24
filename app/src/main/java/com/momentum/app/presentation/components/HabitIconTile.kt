package com.momentum.app.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.presentation.theme.LocalMomentumColors
import com.momentum.app.presentation.theme.MomentumIconSizes
import com.momentum.app.presentation.theme.MomentumShapes
import com.momentum.app.presentation.theme.MomentumTheme
import com.momentum.app.presentation.theme.habitIconAccent

/**
 * Graphical habit icon tile: accent-tinted Soft Aurora container, hairline
 * jewel border, and a tinted Material Symbols Rounded glyph. Shared by the
 * Habits list and the Habit Details hero so every habit surface uses one
 * premium icon language. [deEmphasized] quiets paused habits without
 * disabling them.
 */
@Composable
fun HabitIconTile(
    iconKey: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    iconSize: Dp = MomentumIconSizes.tile,
    deEmphasized: Boolean = false,
) {
    val c = LocalMomentumColors.current
    val accent = habitIconAccent(iconKey)
    val shape = RoundedCornerShape(MomentumShapes.small)
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    0f to Color.White.copy(alpha = 0.14f),
                    1f to accent.copy(alpha = if (deEmphasized) 0.10f else 0.16f),
                ),
                shape = shape,
            )
            .border(
                BorderStroke(
                    Dp.Hairline,
                    accent.copy(alpha = if (deEmphasized) 0.22f else 0.34f),
                ),
                shape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = momentumIcon(iconKey),
            contentDescription = null,
            tint = if (deEmphasized) c.textSecondary else accent,
            modifier = Modifier.size(iconSize),
        )
    }
}

@Preview(name = "HabitIconTile light", showBackground = true, backgroundColor = 0xFFF7F8FC)
@Composable
private fun HabitIconTileLightPreview() {
    MomentumTheme(themeId = "aurora", darkMode = DarkModeSetting.LIGHT) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HabitIconTile(iconKey = "water")
            HabitIconTile(iconKey = "fitness")
            HabitIconTile(iconKey = "mind", deEmphasized = true)
            HabitIconTile(iconKey = "streak", size = 48.dp, iconSize = 26.dp)
            HabitIconTile(iconKey = "spark", size = 72.dp, iconSize = 32.dp)
        }
    }
}

@Preview(name = "HabitIconTile dark", showBackground = true, backgroundColor = 0xFF080D18)
@Composable
private fun HabitIconTileDarkPreview() {
    MomentumTheme(themeId = "aurora", darkMode = DarkModeSetting.DARK) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HabitIconTile(iconKey = "water")
            HabitIconTile(iconKey = "timer")
            HabitIconTile(iconKey = "people", deEmphasized = true)
            HabitIconTile(iconKey = "streak", size = 48.dp, iconSize = 26.dp)
            HabitIconTile(iconKey = "learning", size = 72.dp, iconSize = 32.dp)
        }
    }
}
