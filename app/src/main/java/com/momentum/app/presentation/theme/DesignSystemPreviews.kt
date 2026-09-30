package com.momentum.app.presentation.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.momentum.app.R
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.presentation.aurora.AuroraBackground
import com.momentum.app.presentation.aurora.AuroraState
import com.momentum.app.presentation.components.CategoryChip
import com.momentum.app.presentation.components.CompletionButton
import com.momentum.app.presentation.components.MomentumButton
import com.momentum.app.presentation.components.MomentumCard
import com.momentum.app.presentation.components.MomentumIcons
import com.momentum.app.presentation.components.StreakBadge

/**
 * Visual inspection points for the Phase 1 design foundation: both Aurora
 * environments, the typography hierarchy, surfaces, buttons/controls,
 * shapes/spacing, and the Aurora state ramp. These previews are the fast
 * way to verify the foundation stays coherent before screen work begins.
 */

@Composable
private fun AuroraPreviewShell(
    dark: Boolean,
    content: @Composable () -> Unit,
) {
    MomentumTheme(
        darkMode = if (dark) DarkModeSetting.DARK else DarkModeSetting.LIGHT,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MomentumThemeColors.current.background),
        ) {
            content()
        }
    }
}

@Preview(name = "Aurora light", group = "Theme", showBackground = true, backgroundColor = 0xFFF7F8FC)
@Composable
private fun AuroraLightPreview() {
    AuroraPreviewShell(dark = false) { SurfacesAndTextSample() }
}

@Preview(name = "Aurora dark", group = "Theme", showBackground = true, backgroundColor = 0xFF080D18)
@Composable
private fun AuroraDarkPreview() {
    AuroraPreviewShell(dark = true) { SurfacesAndTextSample() }
}

@Composable
private fun SurfacesAndTextSample() {
    val c = MomentumThemeColors.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MomentumSpacing.screen),
        verticalArrangement = Arrangement.spacedBy(MomentumSpacing.section),
    ) {
        Text(stringResource(R.string.app_name), style = MaterialTheme.typography.displayLarge, color = c.textPrimary)
        Text(
            stringResource(R.string.ds_tagline),
            style = MaterialTheme.typography.bodyLarge,
            color = c.textSecondary,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.compact)) {
            SurfaceSwatch("Canvas", c.background, Modifier.weight(1f))
            SurfaceSwatch("Surface", c.surface, Modifier.weight(1f))
            SurfaceSwatch("Soft", c.surfaceSoft, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.compact)) {
            SurfaceSwatch("Elevated", c.surfaceElevated, Modifier.weight(1f))
            SurfaceSwatch("Interactive", c.surfaceInteractive, Modifier.weight(1f))
            SurfaceSwatch("Accent", c.accentPrimary, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.compact)) {
            Text(stringResource(R.string.ds_color_primary), style = MaterialTheme.typography.bodyLarge, color = c.textPrimary)
            Text(stringResource(R.string.ds_color_secondary), style = MaterialTheme.typography.bodyLarge, color = c.textSecondary)
            Text(stringResource(R.string.ds_color_muted), style = MaterialTheme.typography.bodyLarge, color = c.textMuted)
        }
    }
}

@Composable
private fun SurfaceSwatch(label: String, color: Color, modifier: Modifier = Modifier) {
    val c = MomentumThemeColors.current
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(width = 64.dp, height = 48.dp)
                .clip(RoundedCornerShape(MomentumShapes.control))
                .background(color)
                .border(Dp.Hairline, c.borderStrong, RoundedCornerShape(MomentumShapes.control)),
        )
        Spacer(Modifier.height(MomentumSpacing.micro))
        Text(label, style = MaterialTheme.typography.labelSmall, color = c.textSecondary)
    }
}

@Preview(name = "Typography hierarchy", group = "Theme", showBackground = true, backgroundColor = 0xFFF7F8FC)
@Composable
private fun TypographyLightPreview() {
    AuroraPreviewShell(dark = false) { TypographySample() }
}

@Preview(name = "Typography hierarchy dark", group = "Theme", showBackground = true, backgroundColor = 0xFF080D18)
@Composable
private fun TypographyDarkPreview() {
    AuroraPreviewShell(dark = true) { TypographySample() }
}

@Composable
private fun TypographySample() {
    val c = MomentumThemeColors.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MomentumSpacing.screen),
        verticalArrangement = Arrangement.spacedBy(MomentumSpacing.compact),
    ) {
        Text(stringResource(R.string.ds_type_display), style = MaterialTheme.typography.displayLarge, color = c.textPrimary)
        Text(stringResource(R.string.ds_type_headline), style = MaterialTheme.typography.headlineLarge, color = c.textPrimary)
        Text(stringResource(R.string.ds_type_title), style = MaterialTheme.typography.titleLarge, color = c.textPrimary)
        Text(stringResource(R.string.ds_type_body), style = MaterialTheme.typography.bodyLarge, color = c.textPrimary)
        Text(stringResource(R.string.ds_type_body_small), style = MaterialTheme.typography.bodyMedium, color = c.textSecondary)
        Text(stringResource(R.string.ds_type_label), style = MaterialTheme.typography.labelLarge, color = c.textPrimary)
        Text(stringResource(R.string.ds_type_caption), style = MaterialTheme.typography.labelMedium, color = c.textMuted)
        Text(stringResource(R.string.ds_type_sample), style = MaterialTheme.typography.headlineMedium, color = c.accentPrimary)
    }
}

@Preview(name = "Buttons and controls", group = "Theme", showBackground = true, backgroundColor = 0xFFF7F8FC)
@Composable
private fun ControlsLightPreview() {
    AuroraPreviewShell(dark = false) { ControlsSample() }
}

@Preview(name = "Buttons and controls dark", group = "Theme", showBackground = true, backgroundColor = 0xFF080D18)
@Composable
private fun ControlsDarkPreview() {
    AuroraPreviewShell(dark = true) { ControlsSample() }
}

@Composable
private fun ControlsSample() {
    val c = MomentumThemeColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MomentumSpacing.screen),
        verticalArrangement = Arrangement.spacedBy(MomentumSpacing.compact),
    ) {
        MomentumButton(text = "Primary", onClick = {})
        MomentumButton(text = "Secondary", onClick = {}, secondary = true)
        Row(
            horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.tight),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CategoryChip(label = "Selected", selected = true, onClick = {})
            CategoryChip(label = "Unselected", selected = false, onClick = {})
            CompletionButton(completed = false, onToggle = {})
            CompletionButton(completed = true, onToggle = {})
            StreakBadge(streak = 5)
        }
        MomentumCard {
            Row(
                horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.section),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconLabel(MomentumIcons.home, "home")
                IconLabel(MomentumIcons.list, "list")
                IconLabel(MomentumIcons.insights, "insights")
                IconLabel(MomentumIcons.calendar, "calendar")
                IconLabel(MomentumIcons.settings, "settings")
            }
        }
        Text(
            stringResource(R.string.ds_state_note),
            style = MaterialTheme.typography.bodySmall,
            color = c.textSecondary,
        )
    }
}

@Composable
private fun IconLabel(icon: ImageVector, label: String) {
    val c = MomentumThemeColors.current
    Icon(
        imageVector = icon,
        contentDescription = label,
        tint = c.textSecondary,
        modifier = Modifier.size(MomentumIconSizes.primary),
    )
}

@Preview(name = "Shapes and spacing", group = "Theme", showBackground = true, backgroundColor = 0xFFF7F8FC)
@Composable
private fun ShapesLightPreview() {
    AuroraPreviewShell(dark = false) { ShapesSample() }
}

@Preview(name = "Shapes and spacing dark", group = "Theme", showBackground = true, backgroundColor = 0xFF080D18)
@Composable
private fun ShapesDarkPreview() {
    AuroraPreviewShell(dark = true) { ShapesSample() }
}

@Composable
private fun ShapesSample() {
    val c = MomentumThemeColors.current
    val radii = listOf(
        "small" to MomentumShapes.small,
        "control" to MomentumShapes.control,
        "surface" to MomentumShapes.surface,
        "feature" to MomentumShapes.feature,
        "hero" to MomentumShapes.hero,
    )
    val spacings = listOf(
        "4 micro" to MomentumSpacing.micro,
        "8 tight" to MomentumSpacing.tight,
        "12 compact" to MomentumSpacing.compact,
        "16 standard" to MomentumSpacing.standard,
        "20 screen" to MomentumSpacing.screen,
        "24 section" to MomentumSpacing.section,
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MomentumSpacing.screen),
        verticalArrangement = Arrangement.spacedBy(MomentumSpacing.compact),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.compact)) {
            radii.forEach { (label, radius) ->
                Box(
                    modifier = Modifier
                        .size(width = 56.dp, height = 56.dp)
                        .clip(RoundedCornerShape(radius))
                        .background(c.surfaceSoft)
                        .border(Dp.Hairline, c.borderStrong, RoundedCornerShape(radius)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(label.take(4), style = MaterialTheme.typography.labelSmall, color = c.textSecondary)
                }
            }
        }
        Text(stringResource(R.string.ds_grid), style = MaterialTheme.typography.labelLarge, color = c.textPrimary)
        spacings.forEach { (label, space) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.compact),
            ) {
                Text(label, style = MaterialTheme.typography.labelSmall, color = c.textSecondary, modifier = Modifier.width(96.dp))
                Spacer(
                    modifier = Modifier
                        .width(space * 3)
                        .height(MomentumSpacing.standard)
                        .background(c.accentPrimary.copy(alpha = 0.55f), RoundedCornerShape(MomentumShapes.small)),
                )
            }
        }
    }
}

@Preview(name = "Aurora states light", group = "Theme", showBackground = true, backgroundColor = 0xFFF7F8FC)
@Composable
private fun AuroraStatesLightPreview() {
    AuroraPreviewShell(dark = false) { AuroraStatesSample() }
}

@Preview(name = "Aurora states dark", group = "Theme", showBackground = true, backgroundColor = 0xFF080D18)
@Composable
private fun AuroraStatesDarkPreview() {
    AuroraPreviewShell(dark = true) { AuroraStatesSample() }
}

@Composable
private fun AuroraStatesSample() {
    val c = MomentumThemeColors.current
    val states = listOf(
        AuroraState.REST to "Rest",
        AuroraState.PROGRESS to "Progress",
        AuroraState.COMPLETION to "Completion",
        AuroraState.MILESTONE to "Milestone",
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MomentumSpacing.screen),
        verticalArrangement = Arrangement.spacedBy(MomentumSpacing.section),
    ) {
        Text(stringResource(R.string.ds_aurora_states), style = MaterialTheme.typography.titleLarge, color = c.textPrimary)
        Row(horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.compact)) {
            states.forEach { (state, label) ->
                AuroraBackground(
                    state = state,
                    animated = false,
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp)
                        .clip(RoundedCornerShape(MomentumShapes.surface))
                        .background(c.surfaceSoft)
                        .border(Dp.Hairline, c.borderSubtle, RoundedCornerShape(MomentumShapes.surface)),
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(label, style = MaterialTheme.typography.labelLarge, color = c.textPrimary)
                    }
                }
            }
        }
        Text(
            stringResource(
                R.string.ds_ramp_note,
                MomentumMotion.fast,
                MomentumMotion.standard,
                MomentumMotion.emphasis,
            ),
            style = MaterialTheme.typography.bodySmall,
            color = c.textSecondary,
        )
    }
}
