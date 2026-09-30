package com.momentum.app.presentation.screens.today

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.momentum.app.R
import com.momentum.app.domain.usecase.TodayItem
import com.momentum.app.presentation.aurora.AuroraBackground
import com.momentum.app.presentation.aurora.AuroraState
import com.momentum.app.presentation.components.ActivityCard
import com.momentum.app.presentation.components.EmptyState
import com.momentum.app.presentation.components.GlassLevel
import com.momentum.app.presentation.components.MomentumButton
import com.momentum.app.presentation.components.MomentumIcons
import com.momentum.app.presentation.components.MomentumSectionHeader
import com.momentum.app.presentation.components.glassPress
import com.momentum.app.presentation.components.liquidGlass
import com.momentum.app.presentation.components.targetProgressLabel
import androidx.hilt.navigation.compose.hiltViewModel
import com.momentum.app.presentation.rememberReduceMotion
import com.momentum.app.presentation.resolve
import com.momentum.app.presentation.theme.LocalMomentumColors
import com.momentum.app.presentation.theme.MomentumIconSizes
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumThemeColors
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun TodayRoute(
    onOpenActivity: (Long) -> Unit,
    onAddActivity: () -> Unit,
    onOpenSettings: () -> Unit,
    snackbar: SnackbarHostState,
) {
    val vm: TodayViewModel = hiltViewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    val haptics = LocalHapticFeedback.current
    // Effects arrive inside a coroutine, where `stringResource` is not
    // callable — resolve them from the context the screen already holds.
    val context = LocalContext.current

    LaunchedEffect(Unit) { vm.onEvent(TodayEvent.Refresh) }
    LaunchedEffect(vm) {
        vm.effect.collect { effect ->
            when (effect) {
                is TodayEffect.Celebrate -> {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    snackbar.showSnackbar(effect.message.resolve(context))
                }
                is TodayEffect.ShowMessage -> snackbar.showSnackbar(effect.message.resolve(context))
            }
        }
    }
    when (val s = state) {
        is TodayUiState.Loading -> TodayLoading()
        is TodayUiState.Error -> TodayError(s.message.resolve(), onRetry = { vm.onEvent(TodayEvent.Refresh) })
        is TodayUiState.Success -> TodayScreen(
            state = s,
            reduceMotion = rememberReduceMotion(s.reduceMotionOverride),
            onToggle = { vm.onEvent(TodayEvent.CompletionTap(it)) },
            onOpen = onOpenActivity,
            onAdd = onAddActivity,
            onOpenSettings = onOpenSettings,
        )
    }
    // Reach a Target entry — the dialog only collects input; completion stays
    // a domain rule evaluated against the persisted value. The dialog rides
    // the single state stream (ADR-44), so no second collection is needed.
    state.targetEntry?.let { entry ->
        TargetEntryDialog(
            activityTitle = entry.title,
            target = entry.target,
            unit = entry.unit,
            currentActual = entry.currentActual,
            completed = entry.completed,
            onConfirm = { vm.onEvent(TodayEvent.SaveTargetEntry(entry.activityId, it)) },
            onClear = { vm.onEvent(TodayEvent.ClearTargetEntry(entry.activityId)) },
            onDismiss = { vm.onEvent(TodayEvent.DismissTargetEntry) },
        )
    }
}

@Composable
private fun TodayLoading() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(stringResource(R.string.today_loading), color = MomentumThemeColors.current.textSecondary)
    }
}

@Composable
private fun TodayError(message: String, onRetry: () -> Unit) {
    val c = MomentumThemeColors.current
    Column(
        Modifier.fillMaxSize().padding(MomentumSpacing.section),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(message, color = c.textSecondary, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(MomentumSpacing.compact))
        MomentumButton(text = stringResource(R.string.action_try_again), onClick = onRetry, secondary = true)
    }
}

/**
 * Home (Design System §13): one job — "How am I doing today?" and
 * "What do I still need to do today?" Aurora responds to today's state
 * (REST → PROGRESS → COMPLETION), never permanent wallpaper.
 */
@Composable
fun TodayScreen(
    state: TodayUiState.Success,
    reduceMotion: Boolean,
    onToggle: (Long) -> Unit,
    onOpen: (Long) -> Unit,
    onAdd: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    // Sections are derived from the persisted completion state — this screen
    // never keeps its own copy of "is it done" (DASHBOARD_SPECS §22).
    val remaining = state.remaining
    val finished = state.completed
    val auroraState = when {
        state.isEmpty -> AuroraState.REST
        state.progress >= 1f -> AuroraState.COMPLETION
        state.progress > 0f -> AuroraState.PROGRESS
        else -> AuroraState.REST
    }

    // Hero collapse (DASHBOARD_SPECS §8): ONE clamped px source of truth
    // (`collapsePx` ∈ [0, maxCollapsePx]) reported upward as a single progress
    // value. Down-scroll consumes into collapse before the list scrolls
    // (pre-scroll); leftover up-scroll at list-top restores it (post-scroll) —
    // plain bounded arithmetic on the same px value, no Boolean states and no
    // animations, so drag, fling, and direction reversals stay continuous. The
    // range is measured by HomeHero as a pure function of its content block,
    // and is re-clamped into `collapsePx` whenever it changes.
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

    // Section moves (DASHBOARD_SPECS §11): completing an activity relocates its
    // row from Remaining to Completed, so the row must glide to its new place
    // instead of vanishing. Null specs switch the animation off entirely under
    // reduced motion — the state change stays instant and unambiguous.
    val motionFade: FiniteAnimationSpec<Float>? =
        if (reduceMotion) null else tween(durationMillis = 220)
    val motionPlace: FiniteAnimationSpec<IntOffset>? =
        if (reduceMotion) null else spring(stiffness = Spring.StiffnessMediumLow)

    Box(Modifier.fillMaxSize()) {
        AuroraBackground(
            state = auroraState,
            animated = !reduceMotion,
            modifier = Modifier.fillMaxSize(),
        )
        // One vertical scrolling surface: Hero and both sections share this
        // LazyColumn, with no nested scrolling container for the list
        // (DASHBOARD_SPECS §4). The Hero is full-bleed — horizontally it opts out
        // of the inset every other row carries, and vertically it starts flush
        // with the top of the scroll surface, so the Aurora reaches the top of
        // the screen content with no band of page background above it (ADR-32).
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(heroNestedScroll),
            contentPadding = PaddingValues(
                // 0: the Hero carries its own top inset, on top of the artwork.
                // Any padding here would push it down and expose page background
                // between the top of the surface and the Aurora.
                top = 0.dp,
                // Quick Add floats over the tail of the list: reserve room so
                // the last activity can always be scrolled clear of it (§9).
                bottom = MomentumSpacing.card + QuickAddFabSize + MomentumSpacing.compact,
            ),
            verticalArrangement = Arrangement.spacedBy(MomentumSpacing.item),
        ) {
            // ONE Hero surface (ADR-31/ADR-32): the date, greeting, Settings,
            // ring, count and message are all children of the Hero's own Box,
            // whose first layer is the Aurora artwork — so the art is the Hero's
            // background from its top edge to the fade below the message, not a
            // strip under a separate header.
            item {
                HomeHero(
                    dateLabel = state.dateLabel,
                    greeting = state.greeting.resolve(),
                    onOpenSettings = onOpenSettings,
                    progress = state.progress,
                    doneCount = state.doneCount,
                    totalCount = state.totalCount,
                    message = state.message?.resolve().orEmpty(),
                    collapseProgress = heroProgress,
                    reduceMotion = reduceMotion,
                    onCollapseRangePx = { range ->
                        maxCollapsePx.floatValue = range
                        if (collapsePx.floatValue > range) collapsePx.floatValue = range
                    },
                )
            }
            if (state.isEmpty) {
                // Nothing eligible today: both sections hide (§13) and the
                // dedicated No-Activity State carries the screen instead.
                item {
                    HomeEmptyState(onAdd = onAdd)
                }
            } else {
                // Ordering is fixed: every Remaining row precedes every
                // Completed row; empty sections are never rendered (§20).
                if (remaining.isNotEmpty()) {
                    item {
                        MomentumSectionHeader(
                            title = stringResource(R.string.today_section_remaining),
                            modifier = Modifier
                                .animateItem(fadeInSpec = motionFade, fadeOutSpec = motionFade)
                                .semantics { heading() }
                                .padding(horizontal = MomentumSpacing.screen),
                        )
                    }
                }
                items(remaining, key = { it.activity.id }, contentType = { "todayRow" }) { item ->
                    TodayActivityRow(
                        item = item,
                        completed = false,
                        onToggle = { onToggle(item.activity.id) },
                        onOpen = { onOpen(item.activity.id) },
                        modifier = Modifier.animateItem(
                            fadeInSpec = motionFade,
                            placementSpec = motionPlace,
                            fadeOutSpec = motionFade,
                        ),
                    )
                }
                if (finished.isNotEmpty()) {
                    item {
                        MomentumSectionHeader(
                            title = stringResource(R.string.today_section_completed),
                            modifier = Modifier
                                .animateItem(fadeInSpec = motionFade, fadeOutSpec = motionFade)
                                .semantics { heading() }
                                .padding(horizontal = MomentumSpacing.screen),
                        )
                    }
                }
                items(finished, key = { it.activity.id }, contentType = { "todayRow" }) { item ->
                    TodayActivityRow(
                        item = item,
                        completed = true,
                        onToggle = { onToggle(item.activity.id) },
                        onOpen = { onOpen(item.activity.id) },
                        modifier = Modifier.animateItem(
                            fadeInSpec = motionFade,
                            placementSpec = motionPlace,
                            fadeOutSpec = motionFade,
                        ),
                    )
                }
            }
        }
        // Quick Add (§14 / DECISIONS §20): compact circular `+`, bottom-right,
        // immediately above the bottom navigation, opening Create Item.
        QuickAddFab(
            onAdd = onAdd,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = MomentumSpacing.screen, bottom = MomentumSpacing.compact),
        )
    }
}

/** Today's list row — the shared Activity Row (§15), compact action surface. */
@Composable
private fun TodayActivityRow(
    item: TodayItem,
    completed: Boolean,
    onToggle: () -> Unit,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = remember(item) { homeMetadataLabel(item) }
    ActivityCard(
        activity = item.activity,
        completed = completed,
        streak = item.currentStreak,
        scheduleLabel = label,
        onToggle = onToggle,
        onOpen = onOpen,
        modifier = modifier.padding(horizontal = MomentumSpacing.screen),
    )
}

/** Quick Add diameter (§14): circular and compact, clear of the 48 dp minimum. */
private val QuickAddFabSize = 56.dp

/**
 * Quick Add (DASHBOARD_SPECS §14, DECISIONS §20): a compact circular `+` at the
 * bottom-right, immediately above the bottom navigation. It opens Create Item
 * (Habit / Task) directly — never a multi-purpose action menu — and is the only
 * add affordance on a populated Home list.
 */
@Composable
private fun QuickAddFab(onAdd: () -> Unit, modifier: Modifier = Modifier) {
    val c = LocalMomentumColors.current
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .size(QuickAddFabSize)
            .liquidGlass(CircleShape, GlassLevel.HERO)
            .glassPress(interaction)
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = onAdd,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = MomentumIcons.add,
            contentDescription = stringResource(R.string.cd_quick_add),
            tint = c.primary,
            modifier = Modifier.size(MomentumIconSizes.large),
        )
    }
}

/** Calm, useful, action-oriented empty state (§3, §25): no guilt, no streak-loss language. */
@Composable
private fun HomeEmptyState(onAdd: () -> Unit) {
    EmptyState(
        title = stringResource(R.string.today_empty_title),
        body = stringResource(R.string.today_empty_body),
        actionLabel = stringResource(R.string.today_empty_action),
        onAction = onAdd,
        icon = Icons.Rounded.Star,
        modifier = Modifier.padding(horizontal = MomentumSpacing.screen),
    )
}

/**
 * Home card metadata (DECISIONS §11): the optional configured time plus the
 * target progress when applicable. Deliberately no recurrence or activity-type
 * label — classification belongs to detail screens, not Home (§11 Rejected).
 * An activity with neither a time nor a target renders no metadata row at all
 * rather than a placeholder (§29).
 */
private fun homeMetadataLabel(item: TodayItem): String = buildString {
    item.activity.dueTime?.let { time ->
        append(
            runCatching {
                LocalTime.parse(time).format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
            }.getOrDefault(time),
        )
    }
    // Shows live progress: "0/8 glasses", "5/8 glasses" (partial), "8/8 glasses".
    targetProgressLabel(item.activity, item.completionValue)?.let { progress ->
        if (isNotEmpty()) append(" · ")
        append(progress)
    }
}
