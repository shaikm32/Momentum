package com.momentum.app.presentation.screens.habits

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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.momentum.app.R
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.domain.model.Recurrence
import com.momentum.app.presentation.aurora.AuroraBackground
import com.momentum.app.presentation.aurora.AuroraState
import com.momentum.app.presentation.components.EmptyState
import com.momentum.app.presentation.components.GlassLevel
import com.momentum.app.presentation.components.HabitIconTile
import com.momentum.app.presentation.components.MomentumDialog
import com.momentum.app.presentation.components.MomentumIcons
import com.momentum.app.presentation.components.formatAmount
import com.momentum.app.presentation.components.glassPress
import com.momentum.app.presentation.components.liquidGlass
import com.momentum.app.presentation.components.recurrenceLabel
import androidx.hilt.navigation.compose.hiltViewModel
import com.momentum.app.presentation.rememberReduceMotion
import com.momentum.app.presentation.resolve
import com.momentum.app.presentation.theme.MomentumHeights
import com.momentum.app.presentation.theme.MomentumIconSizes
import com.momentum.app.presentation.theme.MomentumRadii
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumTheme
import com.momentum.app.presentation.theme.MomentumThemeColors
import java.time.LocalDate

/**
 * Habits management screen (Phase 2B): Soft Aurora environment matching
 * Home, a collapsing hero header built on the bundled light/dark Aurora
 * artwork (title + description inside the artwork's lower edge, scroll-driven
 * collapse into a compact title/description header), one continuous list of
 * Active then Paused habits, and a floating Add Habit action above the bottom
 * navigation. Completion lives on Home only.
 */
@Composable
fun HabitsRoute(
    onOpenHabit: (Long) -> Unit,
    onAddHabit: () -> Unit,
    snackbar: SnackbarHostState,
) {
    val vm: HabitsViewModel = hiltViewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    var pendingDelete by remember { mutableStateOf<Long?>(null) }
    val reduceMotion = rememberReduceMotion(null)

    // Hero collapse: ONE clamped px source of truth (`collapsePx` ∈
    // [0, maxCollapsePx]) reported upward as a single progress value.
    // Down-scroll consumes into collapse before the list scrolls (pre-scroll);
    // leftover up-scroll at list-top restores it (post-scroll) — plain bounded
    // arithmetic on the same px value, no Boolean states and no animations, so
    // drag, fling, and direction reversals stay continuous and deterministic.
    // The range is measured by HabitsHero as a pure function of its text
    // block, and is re-clamped into `collapsePx` whenever it changes.
    val collapsePx = remember { mutableFloatStateOf(0f) }
    val maxCollapsePx = remember { mutableFloatStateOf(0f) }
    val heroNestedScroll = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                if (delta >= 0f) return Offset.Zero
                val consumed = minOf(-delta, maxCollapsePx.floatValue - collapsePx.floatValue)
                if (consumed <= 0f) return Offset.Zero
                collapsePx.floatValue = (collapsePx.floatValue + consumed)
                    .coerceIn(0f, maxCollapsePx.floatValue)
                return Offset(0f, -consumed)
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                val delta = available.y
                if (delta <= 0f) return Offset.Zero
                val restored = minOf(delta, collapsePx.floatValue)
                if (restored <= 0f) return Offset.Zero
                collapsePx.floatValue = (collapsePx.floatValue - restored).coerceAtLeast(0f)
                return Offset(0f, restored)
            }
        }
    }
    val heroProgress = if (maxCollapsePx.floatValue > 0f) {
        (collapsePx.floatValue / maxCollapsePx.floatValue).coerceIn(0f, 1f)
    } else {
        0f
    }

    val context = LocalContext.current

    LaunchedEffect(vm) {
        vm.effect.collect { effect ->
            when (effect) {
                is HabitsEffect.ShowMessage -> snackbar.showSnackbar(effect.message.resolve(context))
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AuroraBackground(
            state = AuroraState.REST,
            animated = !reduceMotion,
            modifier = Modifier.fillMaxSize(),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(heroNestedScroll),
        ) {
            HabitsHero(
                progress = heroProgress,
                reduceMotion = reduceMotion,
                onCollapseRangePx = { range ->
                    maxCollapsePx.floatValue = range
                    if (collapsePx.floatValue > range) collapsePx.floatValue = range
                },
            )
            if (state.isEmpty) {
                EmptyState(
                    title = stringResource(R.string.habits_empty_title),
                    body = stringResource(R.string.habits_empty_body),
                    actionLabel = stringResource(R.string.action_create_habit),
                    onAction = onAddHabit,
                    icon = Icons.Rounded.Add,
                    modifier = Modifier.padding(
                        horizontal = MomentumSpacing.screen,
                        vertical = MomentumSpacing.group,
                    ),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(
                        start = MomentumSpacing.screen,
                        end = MomentumSpacing.screen,
                        top = MomentumSpacing.group,
                        bottom = 88.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(MomentumSpacing.tight),
                ) {
                    items(state.items, key = { it.activity.id }, contentType = { "habitCard" }) { item ->
                        HabitCard(
                            item = item,
                            onOpen = { onOpenHabit(item.activity.id) },
                            onPause = { vm.pause(item.activity.id) },
                            onResume = { vm.resume(item.activity.id) },
                            onDelete = { pendingDelete = item.activity.id },
                        )
                    }
                }
            }
        }
        AddHabitFab(
            onClick = onAddHabit,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = MomentumSpacing.screen, bottom = MomentumSpacing.compact),
        )
    }

    pendingDelete?.let { id ->
        MomentumDialog(
            title = stringResource(R.string.dialog_delete_habit_title),
            body = stringResource(R.string.dialog_delete_habit_body),
            confirmLabel = stringResource(R.string.action_delete),
            onConfirm = { pendingDelete = null; vm.deleteHard(id) },
            dismissLabel = stringResource(R.string.action_cancel),
            onDismiss = { pendingDelete = null },
            destructive = true,
        )
    }
}

/**
 * Floating Add Habit action: circular Soft Aurora primary button above the
 * bottom navigation, clear `+`, glowing elevation, 56 dp touch target.
 */
@Composable
private fun AddHabitFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = MomentumThemeColors.current
    val interaction = remember { MutableInteractionSource() }
    val shape = CircleShape
    Box(
        modifier = modifier
            .size(56.dp)
            .shadow(
                elevation = 12.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.30f),
                spotColor = c.primary.copy(alpha = 0.40f),
            )
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    0f to Color.White.copy(alpha = 0.22f),
                    0.45f to c.primary.copy(alpha = 0.95f),
                    1f to c.primary.copy(alpha = 0.92f),
                ),
                shape = shape,
            )
            .border(BorderStroke(Dp.Hairline, Color.White.copy(alpha = 0.28f)), shape)
            .glassPress(interaction)
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = MomentumIcons.add,
            contentDescription = stringResource(R.string.cd_create_habit),
            tint = c.onPrimary,
            modifier = Modifier.size(MomentumIconSizes.prominent),
        )
    }
}

/**
 * Compact management card: icon, name, status + overflow, metadata row.
 * No streak, progress, completion, history, or template classification.
 */
@Composable
private fun HabitCard(
    item: HabitListItem,
    onOpen: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = MomentumThemeColors.current
    val activity = item.activity
    val paused = activity.status == ActivityStatus.PAUSED
    var menuOpen by remember { mutableStateOf(false) }
    // `Frequency · Target · Reminder` with three fixed slots — a missing value
    // renders as `—` so every card stays aligned. Composed here (not in the
    // ViewModel) so the display labels stay resolvable from resources.
    val metadata = stringResource(
        R.string.habits_metadata,
        recurrenceLabel(activity.recurrence),
        if (activity.targetPerOccurrence != 1.0 || activity.unit.isNotBlank()) {
            "${formatAmount(activity.targetPerOccurrence)} ${activity.unit}".trim()
        } else {
            "—"
        },
        item.reminderTime ?: "—",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (paused) 0.85f else 1f)
            .liquidGlass(RoundedCornerShape(MomentumRadii.card), GlassLevel.CARD)
            .clip(RoundedCornerShape(MomentumRadii.card))
            .clickable(role = Role.Button, onClick = onOpen)
            .padding(MomentumSpacing.row),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.row),
    ) {
        HabitIconTile(iconKey = activity.iconKey, deEmphasized = paused)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
            ) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = c.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                StatusPill(paused = paused)
            }
            Text(
                text = metadata,
                style = MaterialTheme.typography.bodySmall,
                color = c.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Box {
            IconButton(
                onClick = { menuOpen = true },
                modifier = Modifier.size(MomentumHeights.iconTarget),
            ) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = stringResource(R.string.cd_actions_for_habit, activity.title),
                    tint = c.textSecondary,
                )
            }
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                if (paused) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.habit_menu_resume)) },
                        leadingIcon = {
                            Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = c.primary)
                        },
                        onClick = { menuOpen = false; onResume() },
                    )
                } else {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.habit_menu_pause)) },
                        leadingIcon = {
                            Icon(Icons.Rounded.Pause, contentDescription = null, tint = c.textSecondary)
                        },
                        onClick = { menuOpen = false; onPause() },
                    )
                }
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.habit_menu_delete), color = c.error) },
                    leadingIcon = {
                        Icon(Icons.Rounded.Delete, contentDescription = null, tint = c.error)
                    },
                    onClick = { menuOpen = false; onDelete() },
                )
            }
        }
    }
}

/** Text + subtle dot status (never icon-only). */
@Composable
private fun StatusPill(paused: Boolean) {
    val c = MomentumThemeColors.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(if (paused) c.warning else c.success),
        )
        Text(
            text = if (paused) stringResource(R.string.status_paused) else stringResource(R.string.status_active),
            style = MaterialTheme.typography.labelMedium,
            color = if (paused) c.warning else c.success,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
        )
    }
}

@Preview(name = "Habits list light", showBackground = true)
@Composable
private fun HabitsPreviewLight() {
    MomentumTheme(themeId = "aurora", darkMode = DarkModeSetting.LIGHT) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(16.dp)) {
            HabitCard(
                item = HabitListItem(
                    activity = Activity(
                        title = "Morning run",
                        iconKey = "fitness",
                        startDate = LocalDate.of(2026, 1, 5),
                        recurrence = Recurrence.Daily,
                        status = ActivityStatus.ACTIVE,
                    ),
                    reminderTime = "07:00",
                ),
                onOpen = {}, onPause = {}, onResume = {}, onDelete = {},
            )
            HabitCard(
                item = HabitListItem(
                    activity = Activity(
                        title = "Social media",
                        iconKey = "people",
                        startDate = LocalDate.of(2026, 1, 5),
                        targetPerOccurrence = 60.0,
                        unit = "min",
                        status = ActivityStatus.PAUSED,
                    ),
                    reminderTime = null,
                ),
                onOpen = {}, onPause = {}, onResume = {}, onDelete = {},
            )
        }
    }
}

@Preview(name = "Habits list dark", showBackground = true)
@Composable
private fun HabitsPreviewDark() {
    MomentumTheme(themeId = "aurora", darkMode = DarkModeSetting.DARK) {
        Box(Modifier.padding(16.dp)) {
            HabitCard(
                item = HabitListItem(
                    activity = Activity(
                        title = "Read books",
                        iconKey = "learning",
                        startDate = LocalDate.of(2026, 1, 5),
                        recurrence = Recurrence.Weekdays,
                        targetPerOccurrence = 30.0,
                        unit = "pages",
                        status = ActivityStatus.ACTIVE,
                    ),
                    reminderTime = "21:30",
                ),
                onOpen = {}, onPause = {}, onResume = {}, onDelete = {},
            )
        }
    }
}
