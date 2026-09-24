package com.momentum.app.presentation.screens.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.momentum.app.BuildConfig
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.domain.model.WeekStart
import com.momentum.app.presentation.components.CategoryChip
import com.momentum.app.presentation.components.MomentumCard
import com.momentum.app.presentation.components.MomentumDialog
import com.momentum.app.presentation.components.MomentumDivider
import com.momentum.app.presentation.components.MomentumSectionHeader
import com.momentum.app.presentation.components.MomentumSettingRow
import com.momentum.app.presentation.components.MomentumTopBar
import com.momentum.app.presentation.momentumViewModel
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumThemeColors
import com.momentum.app.presentation.theme.themeById

@Composable
fun SettingsRoute(
    onOpenThemes: () -> Unit,
    onBack: () -> Unit,
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
        onBack = onBack,
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
    onBack: () -> Unit,
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
        contentPadding = PaddingValues(
            horizontal = MomentumSpacing.screen,
            vertical = MomentumSpacing.group,
        ),
        verticalArrangement = Arrangement.spacedBy(MomentumSpacing.item),
    ) {
        item {
            MomentumTopBar(title = "Settings", onBack = onBack)
        }
        item {
            MomentumSectionHeader(title = "Appearance")
        }
        item {
            MomentumCard {
                MomentumSettingRow(
                    title = "Theme",
                    supporting = themeById(themeId).name,
                    onClick = onOpenThemes,
                    trailing = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Text(
                                "Change",
                                style = MaterialTheme.typography.labelLarge,
                                color = c.primary,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Icon(
                                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                contentDescription = null,
                                tint = c.primary,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    },
                )
                MomentumDivider()
                Column(verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group)) {
                    Text(
                        "Mode",
                        style = MaterialTheme.typography.titleMedium,
                        color = c.textPrimary,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        DarkModeSetting.entries.forEach { mode ->
                            CategoryChip(
                                label = mode.name.lowercase().replaceFirstChar(Char::titlecase),
                                selected = darkMode == mode,
                                onClick = { onDarkMode(mode) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
                MomentumDivider()
                MomentumSettingRow(
                    title = "Reduce motion",
                    supporting = "Calm animations throughout the app",
                    trailing = {
                        Switch(
                            checked = reduceMotion == true,
                            onCheckedChange = { onReduceMotion(if (it) true else null) },
                        )
                    },
                )
            }
        }
        item {
            MomentumSectionHeader(title = "Notifications")
        }
        item {
            MomentumCard {
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
            MomentumSectionHeader(title = "Preferences")
        }
        item {
            MomentumCard {
                Column(verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group)) {
                    Text(
                        "Week starts on",
                        style = MaterialTheme.typography.titleMedium,
                        color = c.textPrimary,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        CategoryChip(
                            label = "Monday",
                            selected = weekStart == WeekStart.MONDAY,
                            onClick = { onWeekStart(WeekStart.MONDAY) },
                            modifier = Modifier.weight(1f),
                        )
                        CategoryChip(
                            label = "Sunday",
                            selected = weekStart == WeekStart.SUNDAY,
                            onClick = { onWeekStart(WeekStart.SUNDAY) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                MomentumDivider()
                MomentumSettingRow(
                    title = "Motivational messages",
                    supporting = "Gentle encouragement on Today",
                    trailing = {
                        Switch(checked = motivational, onCheckedChange = onMotivational)
                    },
                )
            }
        }
        item {
            MomentumSectionHeader(title = "Data & privacy")
        }
        item {
            MomentumCard {
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
            MomentumSectionHeader(title = "About")
        }
        item {
            MomentumCard {
                Text(
                    "Momentum ${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.titleMedium,
                    color = c.textPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    "Your data stays on your device. No account, no cloud, no tracking — offline by design.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = c.textSecondary,
                )
            }
        }
    }
    if (confirmClear) {
        MomentumDialog(
            title = "Clear everything?",
            body = "All activities, completion history, and reminders will be deleted. This can't be undone.",
            confirmLabel = "Clear all",
            onConfirm = { confirmClear = false; onClearData() },
            dismissLabel = "Keep my data",
            onDismiss = { confirmClear = false },
            destructive = true,
        )
    }
}
