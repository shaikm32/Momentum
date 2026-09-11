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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.momentum.app.presentation.components.ActivityCard
import com.momentum.app.presentation.components.CategoryChip
import com.momentum.app.presentation.components.EmptyState
import com.momentum.app.presentation.components.LiquidGlassIconButton
import com.momentum.app.presentation.components.MomentumDialog
import com.momentum.app.presentation.components.MomentumSectionHeader
import com.momentum.app.presentation.components.MomentumTopBar
import com.momentum.app.presentation.components.recurrenceLabel
import com.momentum.app.presentation.momentumViewModel
import com.momentum.app.presentation.theme.MomentumRadii
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumThemeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesRoute(
    onOpenActivity: (Long) -> Unit,
    onAddActivity: () -> Unit,
    snackbar: SnackbarHostState,
) {
    val vm: ActivitiesViewModel = momentumViewModel { ActivitiesViewModel(it) }
    val state by vm.state.collectAsStateWithLifecycle()
    var pendingDelete by remember { mutableStateOf<Long?>(null) }

    androidx.compose.runtime.LaunchedEffect(vm) {
        vm.messages.collect { snackbar.showSnackbar(it) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        MomentumTopBar(
            title = "Activities",
            subtitle = when (state.tab) {
                ActivityTab.ACTIVE -> "${state.items.size} active"
                ActivityTab.PAUSED -> "${state.items.size} paused"
                ActivityTab.ARCHIVED -> "${state.items.size} archived"
            },
            actions = {
                LiquidGlassIconButton(
                    icon = Icons.Filled.Add,
                    contentDescription = "Add activity",
                    onClick = onAddActivity,
                    active = true,
                )
            },
        )
        ActivitiesScreen(
            state = state,
            onQuery = { vm.setQuery(it) },
            onTab = { vm.setTab(it) },
            onCategory = { vm.setCategory(it) },
            onOpen = onOpenActivity,
            onToggle = { vm.toggleToday(it) },
            onPause = { vm.pause(it) },
            onResume = { vm.resume(it) },
            onArchive = { vm.archive(it) },
            onDelete = { pendingDelete = it },
            onAdd = onAddActivity,
        )
    }
    pendingDelete?.let { id ->
        MomentumDialog(
            title = "Delete permanently?",
            body = "History and reminders for this activity are deleted too. Archive keeps history.",
            confirmLabel = "Delete",
            onConfirm = { vm.deleteHard(id); pendingDelete = null },
            dismissLabel = "Keep",
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
    onDelete: (Long) -> Unit,
    onAdd: () -> Unit,
) {
    val c = MomentumThemeColors.current
    Column(modifier.fillMaxSize()) {
        OutlinedTextField(
            value = state.query,
            onValueChange = onQuery,
            label = { Text("Search activities") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = c.textSecondary) },
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
            items(ActivityTab.entries.toList()) { tab ->
                CategoryChip(
                    label = when (tab) {
                        ActivityTab.ACTIVE -> "Active"
                        ActivityTab.PAUSED -> "Paused"
                        ActivityTab.ARCHIVED -> "Archived"
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
                    CategoryChip(label = "All", selected = state.categoryId == null, onClick = { onCategory(null) })
                }
                items(state.categories, key = { it.id }) { cat ->
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
                    ActivityTab.ACTIVE -> "No activities yet"
                    ActivityTab.PAUSED -> "Nothing paused"
                    ActivityTab.ARCHIVED -> "Archive is empty"
                },
                body = "Create something worth showing up for.",
                actionLabel = "Create activity",
                onAction = onAdd,
                modifier = Modifier.padding(top = MomentumSpacing.group),
                icon = Icons.Filled.Add,
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
                            ActivityTab.ACTIVE -> "Active"
                            ActivityTab.PAUSED -> "Paused"
                            ActivityTab.ARCHIVED -> "Archived"
                        },
                    )
                }
                items(state.items, key = { it.id }) { activity ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        ActivityCard(
                            activity = activity,
                            completed = false,
                            streak = 0,
                            scheduleLabel = buildString {
                                append(recurrenceLabel(activity.recurrence))
                                state.categoryNames[activity.categoryId]?.let { append(" · $it") }
                            },
                            onToggle = { onToggle(activity.id) },
                            onOpen = { onOpen(activity.id) },
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (state.tab == ActivityTab.ACTIVE) {
                                LiquidGlassIconButton(
                                    icon = Icons.Filled.Pause,
                                    contentDescription = "Pause ${activity.title}",
                                    onClick = { onPause(activity.id) },
                                )
                            }
                            if (state.tab == ActivityTab.PAUSED) {
                                LiquidGlassIconButton(
                                    icon = Icons.Filled.PlayArrow,
                                    contentDescription = "Resume ${activity.title}",
                                    onClick = { onResume(activity.id) },
                                    active = true,
                                )
                            }
                            if (state.tab != ActivityTab.ARCHIVED) {
                                LiquidGlassIconButton(
                                    icon = Icons.Filled.Archive,
                                    contentDescription = "Archive ${activity.title}",
                                    onClick = { onArchive(activity.id) },
                                )
                            } else {
                                LiquidGlassIconButton(
                                    icon = Icons.Filled.Delete,
                                    contentDescription = "Delete ${activity.title}",
                                    onClick = { onDelete(activity.id) },
                                    tint = c.error,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
