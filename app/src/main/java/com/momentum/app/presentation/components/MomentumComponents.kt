package com.momentum.app.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.momentum.app.R
import com.momentum.app.presentation.theme.LocalMomentumColors
import com.momentum.app.presentation.theme.MomentumHeights
import com.momentum.app.presentation.theme.MomentumRadii
import com.momentum.app.presentation.theme.MomentumSpacing

/**
 * Shared screen chrome. Every screen assembles the same small vocabulary —
 * top bar, section headers, cards, buttons, setting rows, dialogs — so the
 * app reads as one deliberate product.
 */

/** Consistent screen header: optional back, compact title, optional subtitle, actions. */
@Composable
fun MomentumTopBar(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val c = LocalMomentumColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .padding(
                horizontal = MomentumSpacing.screen,
                vertical = MomentumSpacing.row,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack, modifier = Modifier.size48()) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(R.string.cd_back), tint = c.textPrimary)
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = c.textPrimary,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = c.textSecondary,
                )
            }
        }
        actions()
    }
}

private fun Modifier.size48(): Modifier = this.then(Modifier.size(MomentumHeights.iconTarget))

/** Section heading with an optional trailing action. */
@Composable
fun MomentumSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val c = LocalMomentumColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = c.textPrimary,
            modifier = Modifier.weight(1f),
        )
        if (actionLabel != null && onAction != null) {
            Box(
                modifier = Modifier
                    .heightIn(min = MomentumHeights.controlMin)
                    .clip(RoundedCornerShape(MomentumRadii.control))
                    .clickable(role = Role.Button, onClick = onAction)
                    // One announcement for the row: the label text is the row's
                    // own label, so merging stops the double read.
                    .semantics(mergeDescendants = true) {}
                    .padding(horizontal = MomentumSpacing.row, vertical = MomentumSpacing.group),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = c.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

/** Quiet glass surface card: translucent fill, hairline light, consistent padding. */
@Composable
fun MomentumCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    level: GlassLevel = GlassLevel.CARD,
    content: @Composable ColumnScope.() -> Unit,
) {
    LiquidGlassCard(
        modifier = modifier,
        onClick = onClick,
        level = level,
        content = content,
    )
}

/** Primary CTA (52 dp) with a quiet secondary variant. */
@Composable
fun MomentumButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    secondary: Boolean = false,
    destructive: Boolean = false,
) {
    LiquidGlassButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        secondary = secondary,
        destructive = destructive,
    )
}

/**
 * One settings row: title, optional explanation, trailing control.
 * Parents place [HorizontalDivider] between rows.
 */
@Composable
fun MomentumSettingRow(
    title: String,
    modifier: Modifier = Modifier,
    supporting: String? = null,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit,
) {
    val c = LocalMomentumColors.current
    val base = modifier
        .fillMaxWidth()
        .heightIn(min = 64.dp)
    val clickable = if (onClick != null) {
        // `onClick` rows here carry only text (and decorative glyphs) in their
        // trailing slot, so merging their descendants keeps the whole row a
        // single announcement. Rows whose trailing slot is itself interactive
        // (a `Switch`) never reach this branch — they pass `onClick = null`.
        base.clip(RoundedCornerShape(MomentumRadii.control))
            .clickable(role = Role.Button, onClick = onClick)
            .semantics(mergeDescendants = true) {}
    } else {
        base
    }
    Row(
        modifier = clickable.padding(
            horizontal = MomentumSpacing.card,
            vertical = MomentumSpacing.row,
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.row),
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = c.textPrimary,
            )
            if (supporting != null) {
                Text(
                    text = supporting,
                    style = MaterialTheme.typography.bodySmall,
                    color = c.textSecondary,
                )
            }
        }
        trailing()
    }
}

/** Unified confirmation dialog on a floating glass panel; [destructive] tints confirm. */
@Composable
fun MomentumDialog(
    title: String,
    body: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    dismissLabel: String,
    onDismiss: () -> Unit,
    destructive: Boolean = false,
) {
    val c = LocalMomentumColors.current
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlass(
                    RoundedCornerShape(MomentumRadii.dialog),
                    GlassLevel.HERO,
                )
                .padding(MomentumSpacing.card),
            verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge, color = c.textPrimary)
            Text(body, style = MaterialTheme.typography.bodyMedium, color = c.textSecondary)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onDismiss) {
                    Text(dismissLabel, color = c.textSecondary)
                }
                TextButton(onClick = onConfirm) {
                    Text(
                        confirmLabel,
                        color = if (destructive) c.error else c.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

/** Thin linear progress with theme track/fill. */
@Composable
fun MomentumLinearProgress(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val c = LocalMomentumColors.current
    LinearProgressIndicator(
        progress = { progress.coerceIn(0f, 1f) },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 6.dp)
            .clip(RoundedCornerShape(MomentumRadii.pill)),
        color = c.primary,
        trackColor = c.surfaceVariant,
        strokeCap = StrokeCap.Round,
    )
}

/** Hairline separator for grouped rows. */
@Composable
fun MomentumDivider(modifier: Modifier = Modifier) {
    val c = LocalMomentumColors.current
    HorizontalDivider(
        modifier = modifier.padding(horizontal = MomentumSpacing.card),
        thickness = Dp.Hairline,
        color = c.textSecondary.copy(alpha = 0.16f),
    )
}
