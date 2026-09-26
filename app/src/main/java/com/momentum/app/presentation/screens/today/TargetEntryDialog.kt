package com.momentum.app.presentation.screens.today

import android.view.WindowManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.presentation.components.GlassLevel
import com.momentum.app.presentation.components.formatAmount
import com.momentum.app.presentation.components.liquidGlass
import com.momentum.app.presentation.screens.creation.glassFieldColors
import com.momentum.app.presentation.theme.MomentumGlass
import com.momentum.app.presentation.theme.MomentumHeights
import com.momentum.app.presentation.theme.MomentumRadii
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumTheme
import com.momentum.app.presentation.theme.MomentumThemeColors

/**
 * Reach a Target entry (DASHBOARD_SPECS §10.2, DECISIONS §13–§15).
 *
 * Shows the configured **target**, its **unit** and an editable **actual**
 * value. Saving never decides completion here — the value is handed to
 * [com.momentum.app.domain.usecase.SetActualValueUseCase] and completion is
 * re-derived by [com.momentum.app.domain.engine.CompletionEngine], so
 * `actual >= target` completes and anything below stays incomplete with the
 * partial value retained. Above-target entries are accepted unclamped.
 *
 * "Clear" reverses a completion in one action (record removed ⇒ no progress).
 *
 * **Surface.** The dialog is an opaque [GlassLevel.DIALOG] panel with a window
 * dim behind it ([MomentumGlass.dialogScrim]): Home visibly recedes while the
 * title, target, input and actions keep full theme contrast on a solid
 * surface. The panel itself is never darkened and the screen behind is never
 * tinted — the layering does the work, in Light and Dark alike.
 *
 * Accessibility: numeric keyboard, label + supporting text announced with the
 * field, 48 dp minimum targets from the Material 3 buttons, and no meaning
 * carried by colour or animation alone.
 */
@Composable
fun TargetEntryDialog(
    activityTitle: String,
    target: Double,
    unit: String,
    currentActual: Double,
    completed: Boolean,
    onConfirm: (Double) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = MomentumThemeColors.current
    var input by remember(currentActual, activityTitle) {
        mutableStateOf(if (currentActual > 0.0) formatAmount(currentActual) else "")
    }
    val unitSuffix = if (unit.isBlank()) "" else " $unit"

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
    ) {
        // Compose's Dialog draws no scrim of its own (b/159900354), so the
        // layer behind this modal is dimmed explicitly — Home is pushed back
        // without touching the dialog's own surface brightness.
        val dialogView = LocalView.current
        SideEffect {
            val window = (dialogView as? DialogWindowProvider)?.window
                ?: (dialogView.parent as? DialogWindowProvider)?.window
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                window.setDimAmount(MomentumGlass.dialogScrim)
            }
        }
        Column(
            modifier = modifier
                .fillMaxWidth()
                .liquidGlass(RoundedCornerShape(MomentumRadii.dialog), GlassLevel.DIALOG)
                .padding(MomentumSpacing.card),
            verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
        ) {
            Text(
                text = activityTitle,
                style = MaterialTheme.typography.titleLarge,
                color = c.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Target",
                    style = MaterialTheme.typography.labelLarge,
                    color = c.textSecondary,
                )
                Text(
                    text = "${formatAmount(target)}$unitSuffix",
                    style = MaterialTheme.typography.titleMedium,
                    color = c.textPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            val parsed = input.trim().toDoubleOrNull()
            OutlinedTextField(
                value = input,
                onValueChange = { value ->
                    // Digits plus at most one two-decimal fraction: keeps the
                    // field usable with numeric keyboards and screen readers.
                    if (value.isEmpty() || value.matches(Regex("^\\d{0,7}(\\.\\d{0,2})?$"))) {
                        input = value
                    }
                },
                label = { Text("Actual") },
                placeholder = { Text("e.g. ${formatAmount(target)}") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(MomentumRadii.control),
                colors = glassFieldColors(),
                supportingText = {
                    val shown = parsed ?: currentActual
                    Text(
                        text = progressLabel(shown, target, unitSuffix),
                        color = if (completed) c.success else c.textSecondary,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.tight),
            ) {
                if (completed || currentActual > 0.0) {
                    TextButton(
                        onClick = onClear,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = MomentumHeights.controlMin),
                    ) {
                        Text("Clear", color = c.textSecondary)
                    }
                }
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = MomentumHeights.controlMin),
                ) {
                    Text("Cancel", color = c.textSecondary)
                }
                TextButton(
                    onClick = { input.trim().toDoubleOrNull()?.let(onConfirm) },
                    enabled = parsed != null && parsed >= 0.0,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = MomentumHeights.controlMin),
                ) {
                    Text(
                        text = "Save",
                        color = if (parsed != null) c.primary else c.textSecondary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

/** "5/8 glasses" — the same factual progress label the activity card shows. */
private fun progressLabel(value: Double, target: Double, unitSuffix: String): String =
    "${formatAmount(value)}/${formatAmount(target)}$unitSuffix"

@Preview(name = "Target entry light", showBackground = true)
@Composable
private fun TargetEntryLightPreview() {
    MomentumTheme(darkMode = DarkModeSetting.LIGHT) {
        TargetEntryDialog(
            activityTitle = "Drink water",
            target = 8.0,
            unit = "glasses",
            currentActual = 5.0,
            completed = false,
            onConfirm = {},
            onClear = {},
            onDismiss = {},
        )
    }
}

@Preview(name = "Target entry dark", showBackground = true)
@Composable
private fun TargetEntryDarkPreview() {
    MomentumTheme(darkMode = DarkModeSetting.DARK) {
        TargetEntryDialog(
            activityTitle = "Drink water",
            target = 8.0,
            unit = "glasses",
            currentActual = 8.0,
            completed = true,
            onConfirm = {},
            onClear = {},
            onDismiss = {},
        )
    }
}
