package com.momentum.app.presentation.screens.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Unarchive
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.momentum.app.R
import com.momentum.app.presentation.components.ActivityCard
import com.momentum.app.presentation.components.CategoryChip
import com.momentum.app.presentation.components.EmptyState
import com.momentum.app.presentation.components.LiquidGlassIconButton
import com.momentum.app.presentation.components.MomentumDialog
import com.momentum.app.presentation.components.MomentumSectionHeader
import com.momentum.app.presentation.components.MomentumTopBar
import com.momentum.app.presentation.components.recurrenceLabel
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityType
import com.momentum.app.presentation.ViewModelArgs
import com.momentum.app.presentation.hiltViewModelWithArgs
import com.momentum.app.presentation.resolve
import com.momentum.app.presentation.theme.MomentumRadii
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumThemeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesRoute(
    onOpenActivity: (Long) -> Unit,
    onAddActivity: () -> Unit,
    snackbar: SnackbarHostState,
    type: ActivityType? = null,
    title: String = stringResource(R.string.activities_title),
) {
    val vm: ActivitiesViewModel = hiltViewModelWithArgs {
        putString(ViewModelArgs.ACTIVITY_TYPE, type?.name)
    }
    val state by vm.state.collectAsStateWithLifecycle()
    var pendingDelete by remember { mutableStateOf<Long?>(null) }
    val context = LocalContext.current

    androidx.compose.runtime.LaunchedEffect(vm) {
        vm.effect.collect { effect ->
            when (effect) {
                is ActivitiesUiEffect.ShowMessage ->
                    snackbar.showSnackbar(effect.message.resolve(context))
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        MomentumTopBar(
            title = title,
            subtitle = when (state.tab) {
                ActivityTab.ACTIVE -> stringResource(R.string.activities_subtitle_active, state.items.size)
                ActivityTab.PAUSED -> stringResource(R.string.activities_subtitle_paused, state.items.size)
                ActivityTab.ARCHIVED -> stringResource(R.string.activities_subtitle_archived, state.items.size)
            },
            actions = {
                LiquidGlassIconButton(
                    icon = Icons.Rounded.Add,
                    contentDescription = stringResource(R.string.cd_add_activity),
                    onClick = onAddActivity,
                    active = true,
                )
            },
        )
        ActivitiesScreen(
            state = state,
            onQuery = { vm.onEvent(ActivitiesEvent.QueryChanged(it)) },
            onTab = { vm.onEvent(ActivitiesEvent.TabChanged(it)) },
            onCategory = { vm.onEvent(ActivitiesEvent.CategoryChanged(it)) },
            onOpen = onOpenActivity,
            onToggle = { vm.onEvent(ActivitiesEvent.ToggleToday(it)) },
            onPause = { vm.onEvent(ActivitiesEvent.Pause(it)) },
            onResume = { vm.onEvent(ActivitiesEvent.Resume(it)) },
            onArchive = { vm.onEvent(ActivitiesEvent.Archive(it)) },
            onRestore = { vm.onEvent(ActivitiesEvent.Restore(it)) },
            onDelete = { pendingDelete = it },
            onAdd = onAddActivity,
        )
    }
    pendingDelete?.let { id ->
        MomentumDialog(
            title = stringResource(R.string.dialog_delete_permanently_title),
            body = stringResource(R.string.dialog_delete_activities_body),
            confirmLabel = stringResource(R.string.action_delete),
            onConfirm = { vm.onEvent(ActivitiesEvent.DeleteHard(id)); pendingDelete = null },
            dismissLabel = stringResource(R.string.action_keep),
            onDismiss = { pendingDelete = null },
            destructive = true,
        )
    }
}

@Composable
fun ActivitiesScreen(
    state: ActivitiesUiState,
    modifier: Modifier = Modifier,
    onQuery: (String) -> Unit,
    onTab: (ActivityTab) -> Unit,
    onCategory: (Long?) -> Unit,
    onOpen: (Long) -> Unit,
    onToggle: (Long) -> Unit,
    onPause: (Long) -> Unit,
    onResume: (Long) -> Unit,
    onArchive: (Long) -> Unit,
    onRestore: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onAdd: () -> Unit,
) {
    val c = MomentumThemeColors.current
    Column(modifier.fillMaxSize()) {
        OutlinedTextField(
            value = state.query,
            onValueChange = onQuery,
            label = { Text(stringResource(R.string.activities_search)) },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = c.textSecondary) },
            singleLine = true,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(MomentumRadii.control),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = c.surface.copy(alpha = 0.45f),
                focusedContainerColor = c.surface.copy(alpha = 0.65f),
                unfocusedBorderColor = c.textSecondary.copy(alpha = 0.22f),
                focusedBorderColor = c.primary.copy(alpha = 0.6f),
                cursorColor = c.primary,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MomentumSpacing.screen),
        )
        LazyRow(
            contentPadding = PaddingValues(
                horizontal = MomentumSpacing.screen,
                vertical = MomentumSpacing.group,
            ),
            horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
        ) {
            items(ActivityTab.entries, key = { it.name }, contentType = { "tabChip" }) {
                tab ->
                CategoryChip(
                    label = when (tab) {
                        ActivityTab.ACTIVE -> stringResource(R.string.tab_active)
                        ActivityTab.PAUSED -> stringResource(R.string.tab_paused)
                        ActivityTab.ARCHIVED -> stringResource(R.string.tab_archived)
                    },
                    selected = state.tab == tab,
                    onClick = { onTab(tab) },
                )
            }
        }
        if (state.categories.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = MomentumSpacing.screen),
                horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
            ) {
                item {
                    CategoryChip(label = stringResource(R.string.category_all), selected = state.categoryId == null, onClick = { onCategory(null) })
                }
                items(state.categories, key = { it.id }, contentType = { "categoryChip" }) { cat ->
                    CategoryChip(
                        label = cat.name,
                        selected = state.categoryId == cat.id,
                        onClick = { onCategory(if (state.categoryId == cat.id) null else cat.id) },
                    )
                }
            }
        }
        if (state.items.isEmpty()) {
            EmptyState(
                title = when (state.tab) {
                    ActivityTab.ACTIVE -> stringResource(R.string.activities_empty_active)
                    ActivityTab.PAUSED -> stringResource(R.string.activities_empty_paused)
                    ActivityTab.ARCHIVED -> stringResource(R.string.activities_empty_archived)
                },
                body = stringResource(R.string.activities_empty_body),
                actionLabel = stringResource(R.string.action_create_activity),
                onAction = onAdd,
                modifier = Modifier.padding(top = MomentumSpacing.group),
                icon = Icons.Rounded.Add,
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    horizontal = MomentumSpacing.screen,
                    vertical = MomentumSpacing.group,
                ),
                verticalArrangement = Arrangement.spacedBy(MomentumSpacing.item),
            ) {
                item {
                    MomentumSectionHeader(
                        title = when (state.tab) {
                            ActivityTab.ACTIVE -> stringResource(R.string.tab_active)
                            ActivityTab.PAUSED -> stringResource(R.string.tab_paused)
                            ActivityTab.ARCHIVED -> stringResource(R.string.tab_archived)
                        },
                    )
                }
                items(state.items, key = { it.id }, contentType = { "activityRow" }) { activity ->
                    ActivityListRow(
                        activity = activity,
                        categoryName = state.categoryNames[activity.categoryId],
                        tab = state.tab,
                        onOpen = onOpen,
                        onToggle = onToggle,
                        onPause = onPause,
                        onResume = onResume,
                        onArchive = onArchive,
                        onRestore = onRestore,
                        onDelete = onDelete,
                    )
                }
            }
        }
    }
}

/**
 * One management row: the shared Activity Card plus the tab's action group.
 *
 * Its own composable so it is a separate restart scope: it reads only its
 * `activity`, its `categoryName` and the tab, so a state change that leaves
 * those three equal (every keystroke in the search field, for one) skips the
 * whole row instead of re-running its card and up to three action buttons.
 */
@Composable
private fun ActivityListRow(
    activity: Activity,
    categoryName: String?,
    tab: ActivityTab,
    onOpen: (Long) -> Unit,
    onToggle: (Long) -> Unit,
    onPause: (Long) -> Unit,
    onResume: (Long) -> Unit,
    onArchive: (Long) -> Unit,
    onRestore: (Long) -> Unit,
    onDelete: (Long) -> Unit,
) {
    val c = MomentumThemeColors.current
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        ActivityCard(
            activity = activity,
            completed = false,
            streak = 0,
            scheduleLabel = buildString {
                append(recurrenceLabel(activity.recurrence))
                categoryName?.let { append(" · $it") }
            },
            onToggle = { onToggle(activity.id) },
            onOpen = { onOpen(activity.id) },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (tab == ActivityTab.ACTIVE) {
                LiquidGlassIconButton(
                    icon = Icons.Rounded.Pause,
                    contentDescription = stringResource(R.string.cd_pause_activity, activity.title),
                    onClick = { onPause(activity.id) },
                )
            }
            if (tab == ActivityTab.PAUSED) {
                LiquidGlassIconButton(
                    icon = Icons.Rounded.PlayArrow,
                    contentDescription = stringResource(R.string.cd_resume_activity, activity.title),
                    onClick = { onResume(activity.id) },
                    active = true,
                )
            }
            if (tab != ActivityTab.ARCHIVED) {
                LiquidGlassIconButton(
                    icon = Icons.Rounded.Archive,
                    contentDescription = stringResource(R.string.cd_archive_activity, activity.title),
                    onClick = { onArchive(activity.id) },
                )
            } else {
                LiquidGlassIconButton(
                    icon = Icons.Rounded.Unarchive,
                    contentDescription = stringResource(R.string.cd_restore_activity, activity.title),
                    onClick = { onRestore(activity.id) },
                    active = true,
                )
                LiquidGlassIconButton(
                    icon = Icons.Rounded.Delete,
                    contentDescription = stringResource(R.string.cd_delete_activity, activity.title),
                    onClick = { onDelete(activity.id) },
                    tint = c.error,
                )
            }
        }
    }
}
