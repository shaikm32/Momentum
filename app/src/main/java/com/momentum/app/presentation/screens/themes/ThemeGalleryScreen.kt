package com.momentum.app.presentation.screens.themes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.presentation.momentumViewModel
import com.momentum.app.presentation.screens.settings.SettingsViewModel
import com.momentum.app.presentation.theme.MomentumTheme
import com.momentum.app.presentation.theme.MomentumThemeDefinition
import com.momentum.app.presentation.theme.MomentumThemes
import com.momentum.app.presentation.theme.MomentumThemeColors

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
    val c = MomentumThemeColors.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
                Text("Themes", style = MaterialTheme.typography.headlineMedium, color = c.textPrimary)
            }
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(c.surface.copy(alpha = 0.72f))
            .clickable(onClick = {
                onSelect()
                haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
            })
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Mini gradient preview: dark canvas + light swatch + palette dots.
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.linearGradient(listOf(theme.dark.primary, theme.dark.secondary, theme.dark.accent))),
            contentAlignment = Alignment.Center,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(theme.light.primary, theme.light.secondary, theme.light.accent).forEach { color ->
                    Box(Modifier.size(12.dp).clip(CircleShape).background(color))
                }
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(theme.name, style = MaterialTheme.typography.titleLarge, color = c.textPrimary, fontWeight = FontWeight.SemiBold)
            Text("Light + dark", style = MaterialTheme.typography.bodyMedium, color = c.textSecondary)
        }
        if (selected) {
            Icon(Icons.Filled.Check, contentDescription = "${theme.name} selected", tint = c.primary)
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
