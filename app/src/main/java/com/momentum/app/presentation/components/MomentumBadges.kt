package com.momentum.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.presentation.theme.LocalMomentumColors
import com.momentum.app.presentation.theme.MomentumTheme

/** Flame-free streak badge: count + label, celebratory gradient at milestones. */
@Composable
fun StreakBadge(
    streak: Int,
    modifier: Modifier = Modifier,
) {
    val c = LocalMomentumColors.current
    val milestone = streak > 0 && streak % 7 == 0
    val bg = if (milestone) {
        Brush.linearGradient(listOf(c.accent, c.secondary))
    } else {
        Brush.linearGradient(listOf(c.surfaceVariant, c.surfaceVariant))
    }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = if (milestone) Color.White else c.accent,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = "$streak",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = if (milestone) Color.White else c.textPrimary,
        )
    }
}

/** Filter pill for categories / status / schedule filters. */
@Composable
fun CategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = LocalMomentumColors.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) c.primary else c.surface.copy(alpha = 0.72f))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) c.onPrimary else c.textSecondary,
        )
    }
}

/** Metric tile: value hero + label + optional delta line. */
@Composable
fun StatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    sublabel: String? = null,
) {
    val c = LocalMomentumColors.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(c.surface.copy(alpha = 0.72f))
            .padding(16.dp),
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = c.textPrimary,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = c.textSecondary,
        )
        if (sublabel != null) {
            Text(
                text = sublabel,
                style = MaterialTheme.typography.bodyMedium,
                color = c.primary,
            )
        }
    }
}

/** Rewarding empty state with a CTA — never a blank list. */
@Composable
fun EmptyState(
    title: String,
    body: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = LocalMomentumColors.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(listOf(c.primary.copy(alpha = 0.25f), c.secondary.copy(alpha = 0.18f))),
            )
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, color = c.textPrimary)
        Text(body, style = MaterialTheme.typography.bodyMedium, color = c.textSecondary)
        androidx.compose.material3.TextButton(onClick = onAction) {
            Text(actionLabel, color = c.primary, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Preview(name = "Badges light")
@Composable
private fun BadgesPreview() {
    MomentumTheme(themeId = "sunset", darkMode = DarkModeSetting.LIGHT) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StreakBadge(streak = 5)
            StreakBadge(streak = 14)
            CategoryChip(label = "Health", selected = true, onClick = {})
        }
    }
}

@Preview(name = "Stat dark")
@Composable
private fun StatPreview() {
    MomentumTheme(themeId = "midnight", darkMode = DarkModeSetting.DARK) {
        StatCard(value = "82%", label = "This week", sublabel = "+6% vs last week")
    }
}
