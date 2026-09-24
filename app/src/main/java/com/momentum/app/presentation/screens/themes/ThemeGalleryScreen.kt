package com.momentum.app.presentation.screens.themes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.presentation.components.GlassLevel
import com.momentum.app.presentation.components.MomentumTopBar
import com.momentum.app.presentation.components.glassPress
import com.momentum.app.presentation.components.liquidGlass
import com.momentum.app.presentation.momentumViewModel
import com.momentum.app.presentation.screens.settings.SettingsViewModel
import com.momentum.app.presentation.theme.MomentumTheme
import com.momentum.app.presentation.theme.MomentumThemeDefinition
import com.momentum.app.presentation.theme.MomentumThemes
import com.momentum.app.presentation.theme.MomentumThemeColors
import com.momentum.app.presentation.theme.MomentumRadii
import com.momentum.app.presentation.theme.MomentumSpacing

@Composable
fun ThemeGalleryRoute(onBack: () -> Unit) {
    val vm: SettingsViewModel = momentumViewModel { SettingsViewModel(it) }
    val settings by vm.settings.collectAsStateWithLifecycle()
    ThemeGalleryScreen(
        themes = MomentumThemes,
        selectedId = settings.themeId,
        onSelect = { vm.setTheme(it) },
        onBack = onBack,
    )
}

@Composable
fun ThemeGalleryScreen(
    themes: List<MomentumThemeDefinition>,
    selectedId: String,
    onSelect: (String) -> Unit,
    onBack: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = MomentumSpacing.screen,
            vertical = MomentumSpacing.group,
        ),
        verticalArrangement = Arrangement.spacedBy(MomentumSpacing.item),
    ) {
        item {
            MomentumTopBar(title = "Themes", onBack = onBack)
        }
        items(themes, key = { it.id }) { theme ->
            ThemePreviewCard(
                theme = theme,
                selected = theme.id == selectedId,
                onSelect = { onSelect(theme.id) },
            )
        }
    }
}

@Composable
fun ThemePreviewCard(
    theme: MomentumThemeDefinition,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    val c = MomentumThemeColors.current
    val haptics = LocalHapticFeedback.current
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlass(
                RoundedCornerShape(MomentumRadii.card),
                if (selected) GlassLevel.HERO else GlassLevel.CARD,
            )
            .then(
                if (selected) {
                    Modifier.border(
                        BorderStroke(Dp.Hairline, c.primary.copy(alpha = 0.45f)),
                        RoundedCornerShape(MomentumRadii.card),
                    )
                } else {
                    Modifier
                },
            )
            .glassPress(interaction)
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = {
                    onSelect()
                    haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                },
            )
            .padding(MomentumSpacing.card),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.card),
    ) {
        // Compact swatch: theme gradient chip with light-mode dots.
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(MomentumRadii.small))
                .background(Brush.linearGradient(listOf(theme.dark.primary, theme.dark.secondary, theme.dark.accent)))
                .border(
                    BorderStroke(Dp.Hairline, Color.White.copy(alpha = 0.25f)),
                    RoundedCornerShape(MomentumRadii.small),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(theme.light.primary, theme.light.secondary, theme.light.accent).forEach { color ->
                    Box(Modifier.size(10.dp).clip(CircleShape).background(color))
                }
            }
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(theme.name, style = MaterialTheme.typography.titleMedium, color = c.textPrimary)
            Text("Light · Dark", style = MaterialTheme.typography.bodySmall, color = c.textSecondary)
        }
        if (selected) {
            Icon(Icons.Rounded.Check, contentDescription = "${theme.name} selected", tint = c.primary)
        }
    }
}

@Preview(name = "Theme gallery")
@Composable
private fun ThemeGalleryPreview() {
    MomentumTheme(themeId = "cosmic", darkMode = DarkModeSetting.DARK) {
        Box(Modifier.background(Color.Transparent)) {
            ThemeGalleryScreen(themes = MomentumThemes, selectedId = "cosmic", onSelect = {}, onBack = {})
        }
    }
}
