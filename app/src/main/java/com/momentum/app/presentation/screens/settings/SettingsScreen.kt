package com.momentum.app.presentation.screens.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.momentum.app.BuildConfig
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.domain.model.WeekStart
import com.momentum.app.presentation.components.CategoryChip
import com.momentum.app.presentation.momentumViewModel
import com.momentum.app.presentation.theme.MomentumThemeColors
import com.momentum.app.presentation.theme.themeById

@Composable
fun SettingsRoute(
    onOpenThemes: () -> Unit,
    snackbar: SnackbarHostState,
) {
    val vm: SettingsViewModel = momentumViewModel { SettingsViewModel(it) }
    val settings by vm.settings.collectAsStateWithLifecycle()

    LaunchedEffect(vm) {
        vm.effect.collect { effect ->
            when (effect) {
                is SettingsEffect.ShowMessage -> snackbar.showSnackbar(effect.message)
            }
        }
    }
    SettingsScreen(
        themeId = settings.themeId,
        darkMode = settings.darkMode,
        weekStart = settings.weekStart,
        reduceMotion = settings.reduceMotionOverride,
        motivational = settings.motivationalMessagesEnabled,
        onOpenThemes = onOpenThemes,
        onDarkMode = { vm.setDarkMode(it) },
        onWeekStart = { vm.setWeekStart(it) },
        onReduceMotion = { vm.setReduceMotion(it) },
        onMotivational = { vm.setMotivational(it) },
        onClearData = { vm.clearAllData() },
    )
}

@Composable
fun SettingsScreen(
    themeId: String,
    darkMode: DarkModeSetting,
    weekStart: WeekStart,
    reduceMotion: Boolean?,
    motivational: Boolean,
    onOpenThemes: () -> Unit,
    onDarkMode: (DarkModeSetting) -> Unit,
    onWeekStart: (WeekStart) -> Unit,
    onReduceMotion: (Boolean?) -> Unit,
    onMotivational: (Boolean) -> Unit,
    onClearData: () -> Unit,
) {
    val c = MomentumThemeColors.current
    val context = LocalContext.current
    var confirmClear by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Text("Settings", style = MaterialTheme.typography.displayLarge, color = c.textPrimary)
        }
        item {
            Section("Appearance") {
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                        .background(c.surface.copy(alpha = 0.72f))
                        .clickable(onClick = onOpenThemes)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Theme", color = c.textPrimary, fontWeight = FontWeight.SemiBold)
                        Text(themeById(themeId).name, color = c.textSecondary)
                    }
                    Text("Change", color = c.primary, fontWeight = FontWeight.SemiBold)
                }
                LabelRow("Mode")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DarkModeSetting.entries.forEach { mode ->
                        CategoryChip(
                            label = mode.name.lowercase().replaceFirstChar(Char::titlecase),
                            selected = darkMode == mode,
                            onClick = { onDarkMode(mode) },
                        )
                    }
                }
                SwitchRow(
                    label = "Reduce motion",
                    checked = reduceMotion == true,
                    onChange = { onReduceMotion(if (it) true else null) },
                )
            }
        }
        item {
            Section("Notifications") {
                Text(
                    "Reminders fire on-device. Enable notifications so Momentum can nudge you.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = c.textSecondary,
                )
                TextButton(onClick = {
                    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        }
                    } else {
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                    }
                    runCatching { context.startActivity(intent) }
                }) {
                    Text("Open notification settings")
                }
            }
        }
        item {
            Section("Preferences") {
                LabelRow("Week starts on")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryChip(label = "Monday", selected = weekStart == WeekStart.MONDAY, onClick = { onWeekStart(WeekStart.MONDAY) })
                    CategoryChip(label = "Sunday", selected = weekStart == WeekStart.SUNDAY, onClick = { onWeekStart(WeekStart.SUNDAY) })
                }
                SwitchRow(
                    label = "Motivational messages",
                    checked = motivational,
                    onChange = onMotivational,
                )
            }
        }
        item {
            Section("Data") {
                Text(
                    "Everything stays on this device. Clearing deletes all activities, history, and reminders.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = c.textSecondary,
                )
                TextButton(onClick = { confirmClear = true }) {
                    Text("Clear all local data", color = c.error)
                }
            }
        }
        item {
            Section("About") {
                Text("Momentum ${BuildConfig.VERSION_NAME}", color = c.textPrimary, fontWeight = FontWeight.SemiBold)
                Text(
                    "Your data stays on your device. No account, no cloud, no tracking — offline by design.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = c.textSecondary,
                )
            }
        }
    }
    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text("Clear everything?") },
            text = { Text("All activities, completion history, and reminders will be deleted. This can't be undone.") },
            confirmButton = {
                TextButton(onClick = { confirmClear = false; onClearData() }) {
                    Text("Clear all", color = MomentumThemeColors.current.error)
                }
            },
            dismissButton = { TextButton(onClick = { confirmClear = false }) { Text("Keep my data") } },
        )
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    val c = MomentumThemeColors.current
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge, color = c.textPrimary)
        content()
    }
}

@Composable
private fun LabelRow(text: String) {
    Text(text, color = MomentumThemeColors.current.textSecondary, style = MaterialTheme.typography.bodyMedium)
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = MomentumThemeColors.current.textPrimary, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
