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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.momentum.app.BuildConfig
import com.momentum.app.R
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.domain.model.WeekStart
import com.momentum.app.presentation.components.CategoryChip
import com.momentum.app.presentation.components.MomentumCard
import com.momentum.app.presentation.components.MomentumDialog
import com.momentum.app.presentation.components.MomentumDivider
import com.momentum.app.presentation.components.MomentumSectionHeader
import com.momentum.app.presentation.components.MomentumSettingRow
import com.momentum.app.presentation.components.MomentumTopBar
import com.momentum.app.presentation.resolve
import androidx.hilt.navigation.compose.hiltViewModel
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumThemeColors
import com.momentum.app.presentation.theme.themeById

@Composable
fun SettingsRoute(
    onOpenThemes: () -> Unit,
    onBack: () -> Unit,
    snackbar: SnackbarHostState,
) {
    val vm: SettingsViewModel = hiltViewModel()
    val settings by vm.settings.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(vm) {
        vm.effect.collect { effect ->
            when (effect) {
                is SettingsEffect.ShowMessage -> snackbar.showSnackbar(effect.message.resolve(context))
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
            MomentumTopBar(title = stringResource(R.string.settings_title), onBack = onBack)
        }
        item {
            MomentumSectionHeader(title = stringResource(R.string.section_appearance))
        }
        item {
            MomentumCard {
                MomentumSettingRow(
                    title = stringResource(R.string.settings_theme),
                    supporting = stringResource(themeById(themeId).nameRes),
                    onClick = onOpenThemes,
                    trailing = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Text(
                                stringResource(R.string.settings_change),
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
                        stringResource(R.string.settings_mode),
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
                    title = stringResource(R.string.settings_reduce_motion),
                    supporting = stringResource(R.string.settings_reduce_motion_supporting),
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
            MomentumSectionHeader(title = stringResource(R.string.section_notifications))
        }
        item {
            MomentumCard {
                Text(
                    stringResource(R.string.settings_notifications_body),
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
                    Text(stringResource(R.string.settings_open_notification_settings))
                }
            }
        }
        item {
            MomentumSectionHeader(title = stringResource(R.string.section_preferences))
        }
        item {
            MomentumCard {
                Column(verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group)) {
                    Text(
                        stringResource(R.string.settings_week_starts_on),
                        style = MaterialTheme.typography.titleMedium,
                        color = c.textPrimary,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        CategoryChip(
                            label = stringResource(R.string.week_start_monday),
                            selected = weekStart == WeekStart.MONDAY,
                            onClick = { onWeekStart(WeekStart.MONDAY) },
                            modifier = Modifier.weight(1f),
                        )
                        CategoryChip(
                            label = stringResource(R.string.week_start_sunday),
                            selected = weekStart == WeekStart.SUNDAY,
                            onClick = { onWeekStart(WeekStart.SUNDAY) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                MomentumDivider()
                MomentumSettingRow(
                    title = stringResource(R.string.settings_motivational),
                    supporting = stringResource(R.string.settings_motivational_supporting),
                    trailing = {
                        Switch(checked = motivational, onCheckedChange = onMotivational)
                    },
                )
            }
        }
        item {
            MomentumSectionHeader(title = stringResource(R.string.section_data_privacy))
        }
        item {
            MomentumCard {
                Text(
                    stringResource(R.string.settings_data_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = c.textSecondary,
                )
                TextButton(onClick = { confirmClear = true }) {
                    Text(stringResource(R.string.settings_clear_all_local_data), color = c.error)
                }
            }
        }
        item {
            MomentumSectionHeader(title = stringResource(R.string.section_about))
        }
        item {
            MomentumCard {
                Text(
                    stringResource(R.string.settings_about_version, BuildConfig.VERSION_NAME),
                    style = MaterialTheme.typography.titleMedium,
                    color = c.textPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    stringResource(R.string.settings_about_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = c.textSecondary,
                )
            }
        }
    }
    if (confirmClear) {
        MomentumDialog(
            title = stringResource(R.string.dialog_clear_all_title),
            body = stringResource(R.string.dialog_clear_all_body),
            confirmLabel = stringResource(R.string.action_clear_all),
            onConfirm = { confirmClear = false; onClearData() },
            dismissLabel = stringResource(R.string.action_keep_my_data),
            onDismiss = { confirmClear = false },
            destructive = true,
        )
    }
}
