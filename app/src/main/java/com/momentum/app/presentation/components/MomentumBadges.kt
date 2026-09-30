package com.momentum.app.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.momentum.app.R
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.presentation.theme.LocalMomentumColors
import com.momentum.app.presentation.theme.MomentumGlass
import com.momentum.app.presentation.theme.MomentumHeights
import com.momentum.app.presentation.theme.MomentumRadii
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumTheme
import com.momentum.app.presentation.theme.isDark

/** Compact streak pill: accent dot + count. Milestones tint the text, quietly. */
@Composable
fun StreakBadge(
    streak: Int,
    modifier: Modifier = Modifier,
) {
    val c = LocalMomentumColors.current
    val milestone = streak > 0 && streak % 7 == 0
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(MomentumRadii.pill))
            .background(
                if (milestone) {
                    c.success.copy(alpha = 0.16f)
                } else {
                    c.surface.copy(alpha = MomentumGlass.controlFill)
                },
            )
            .border(
                BorderStroke(
                    width = androidx.compose.ui.unit.Dp.Hairline,
                    color = if (milestone) {
                        c.success.copy(alpha = 0.4f)
                    } else {
                        c.textSecondary.copy(alpha = 0.2f)
                    },
                ),
                RoundedCornerShape(MomentumRadii.pill),
            )
            .padding(horizontal = MomentumSpacing.group, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(if (milestone) c.success else c.accent),
        )
        Text(
            text = stringResource(R.string.badge_streak_days, streak),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = if (milestone) c.success else c.textSecondary,
        )
    }
}

/** Glass filter pill with a guaranteed 48 dp touch height. */
@Composable
fun CategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = LocalMomentumColors.current
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .heightIn(min = MomentumHeights.controlMin)
            .clip(RoundedCornerShape(MomentumRadii.pill))
            .background(
                if (selected) {
                    c.primary.copy(alpha = 0.85f)
                } else {
                    c.surface.copy(alpha = MomentumGlass.controlFill)
                },
            )
            .border(
                BorderStroke(
                    width = androidx.compose.ui.unit.Dp.Hairline,
                    color = if (selected) {
                        Color.White.copy(alpha = 0.25f)
                    } else {
                        c.textSecondary.copy(alpha = 0.22f)
                    },
                ),
                RoundedCornerShape(MomentumRadii.pill),
            )
            .glassPress(interaction)
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) c.onPrimary else c.textPrimary,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}

/** Compact glass metric tile: emphasized number, quiet label, optional delta. */
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
            .clip(RoundedCornerShape(MomentumRadii.card))
            .background(c.surface.copy(alpha = MomentumGlass.cardFill))
            .border(
                BorderStroke(
                    width = androidx.compose.ui.unit.Dp.Hairline,
                    color = if (c.isDark) {
                        Color.White.copy(alpha = MomentumGlass.borderDark)
                    } else {
                        c.textPrimary.copy(alpha = MomentumGlass.borderLight)
                    },
                ),
                RoundedCornerShape(MomentumRadii.card),
            )
            .padding(MomentumSpacing.card),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = c.textPrimary,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = c.textSecondary,
        )
        if (sublabel != null) {
            Text(
                text = sublabel,
                style = MaterialTheme.typography.bodySmall,
                color = c.primary,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

/** Calm glass empty state: small icon tile, centered copy, text CTA. */
@Composable
fun EmptyState(
    title: String,
    body: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    val c = LocalMomentumColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MomentumRadii.card))
            .background(c.surface.copy(alpha = MomentumGlass.cardFill * 0.7f))
            .border(
                BorderStroke(
                    width = androidx.compose.ui.unit.Dp.Hairline,
                    color = if (c.isDark) {
                        Color.White.copy(alpha = MomentumGlass.borderDark)
                    } else {
                        c.textPrimary.copy(alpha = MomentumGlass.borderLight)
                    },
                ),
                RoundedCornerShape(MomentumRadii.card),
            )
            .padding(
                horizontal = MomentumSpacing.card,
                vertical = MomentumSpacing.section,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(MomentumRadii.small))
                    .background(c.primary.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = c.primary,
                    modifier = Modifier.size(26.dp),
                )
            }
        }
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            color = c.textPrimary,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            body,
            style = MaterialTheme.typography.bodyMedium,
            color = c.textSecondary,
        )
        TextButton(onClick = onAction) {
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
