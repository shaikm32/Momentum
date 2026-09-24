package com.momentum.app.presentation.screens.createitem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.momentum.app.domain.model.ActivityType
import com.momentum.app.presentation.components.GlassLevel
import com.momentum.app.presentation.components.MomentumSectionHeader
import com.momentum.app.presentation.components.liquidGlass
import com.momentum.app.presentation.theme.MomentumRadii
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumTheme
import com.momentum.app.presentation.theme.MomentumThemeColors

/**
 * Creation entry point: + → Create Item → Habit / Task.
 *
 * A thin chooser over the guided creation flow — Habit continues to the
 * unified habit creation screen
 * ([com.momentum.app.presentation.screens.creation.HabitConfigRoute]),
 * Task goes straight to its configuration
 * ([com.momentum.app.presentation.screens.creation.TaskConfigRoute]).
 * No creation logic lives here.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateItemSheet(
    onPick: (ActivityType) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        CreateItemSheetContent(onPick = onPick)
    }
}

@Composable
private fun CreateItemSheetContent(onPick: (ActivityType) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MomentumSpacing.screen)
            .padding(bottom = MomentumSpacing.screen),
        verticalArrangement = Arrangement.spacedBy(MomentumSpacing.item),
    ) {
        MomentumSectionHeader(title = "Create item")
        CreateItemOption(
            icon = Icons.Rounded.Refresh,
            title = "Habit",
            supporting = "Build it consistently, one day at a time",
            onClickLabel = "Create habit",
            onClick = { onPick(ActivityType.HABIT) },
        )
        CreateItemOption(
            icon = Icons.Rounded.CheckCircle,
            title = "Task",
            supporting = "Get it done, once or on repeat",
            onClickLabel = "Create task",
            onClick = { onPick(ActivityType.TASK) },
        )
    }
}

@Composable
private fun CreateItemOption(
    icon: ImageVector,
    title: String,
    supporting: String,
    onClickLabel: String,
    onClick: () -> Unit,
) {
    val c = MomentumThemeColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MomentumRadii.card))
            .liquidGlass(
                RoundedCornerShape(MomentumRadii.card),
                GlassLevel.CARD,
            )
            .clickable(onClickLabel = onClickLabel, onClick = onClick)
            .padding(MomentumSpacing.card),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.card),
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = c.primary,
            modifier = Modifier.size(28.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = c.textPrimary)
            Text(supporting, style = MaterialTheme.typography.bodySmall, color = c.textSecondary)
        }
    }
}

@Preview(name = "Create item — light", showBackground = true)
@Preview(name = "Create item — dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CreateItemSheetPreview() {
    MomentumTheme {
        CreateItemSheetContent(onPick = {})
    }
}
