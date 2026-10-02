package com.momentum.app.presentation.screens.today

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.momentum.app.R
import com.momentum.app.domain.usecase.TodayItem
import com.momentum.app.presentation.aurora.AuroraState
import com.momentum.app.presentation.components.EmptyState
import com.momentum.app.presentation.components.MomentumButton
import com.momentum.app.presentation.components.MomentumFab
import com.momentum.app.presentation.components.MomentumFabSize
import com.momentum.app.presentation.components.targetProgressLabel
import androidx.hilt.navigation.compose.hiltViewModel
import com.momentum.app.presentation.rememberReduceMotion
import com.momentum.app.presentation.resolve
import com.momentum.app.presentation.theme.MomentumMotion
import com.momentum.app.presentation.theme.MomentumRadii
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumThemeColors
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlinx.coroutines.delay

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
    val reduceMotion = rememberReduceMotion((state as? TodayUiState.Success)?.reduceMotionOverride)

    // The milestone window is presentation-local (no VM state): the effect
    // collector only bumps the tick, and one keyed effect in TodayScreen owns
    // the open/close so a re-trigger restarts and an un-complete cannot cancel.
    var milestoneTick by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) { vm.onEvent(TodayEvent.Refresh) }
    LaunchedEffect(vm) {
        vm.effect.collect { effect ->
            when (effect) {
                TodayEffect.Celebrate -> {
                    // One tactile acknowledgement; no snackbar — the Hero is
                    // already stating "Today is complete" (ADR-48 §3).
                    haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                    milestoneTick += 1
                }
                is TodayEffect.ShowMessage -> snackbar.showSnackbar(effect.message.resolve(context))
            }
        }
    }

    // Branch-keyed so a Success payload refresh (a completion) does not replay
    // the loading fade; only an actual branch change animates.
    AnimatedContent(
        targetState = state,
        modifier = Modifier.fillMaxSize(),
        transitionSpec = {
            if (reduceMotion) {
                EnterTransition.None togetherWith ExitTransition.None
            } else {
                fadeIn(tween(durationMillis = MomentumMotion.standard, easing = MomentumMotion.easeOut)) togetherWith
                    fadeOut(tween(MomentumMotion.fast))
            }
        },
        contentKey = { todayBranch(it) },
        label = "todayBranch",
    ) { s ->
        when (s) {
            is TodayUiState.Loading -> TodayLoadingSkeleton()
            is TodayUiState.Error -> TodayError(
                message = s.message.resolve(),
                onRetry = { vm.onEvent(TodayEvent.Refresh) },
            )
            is TodayUiState.Success -> TodayScreen(
                state = s,
                reduceMotion = reduceMotion,
                milestoneTick = milestoneTick,
                onToggle = { vm.onEvent(TodayEvent.CompletionTap(it)) },
                onOpen = onOpenActivity,
                onAdd = onAddActivity,
                onOpenSettings = onOpenSettings,
            )
        }
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

/** The three exclusive screen branches (kept separate from payload equality). */
private enum class TodayBranch { LOADING, ERROR, SUCCESS }

private fun todayBranch(state: TodayUiState): TodayBranch = when (state) {
    is TodayUiState.Loading -> TodayBranch.LOADING
    is TodayUiState.Error -> TodayBranch.ERROR
    is TodayUiState.Success -> TodayBranch.SUCCESS
}

/**
 * The day's skeleton, matching the **new** silhouette: the chrome's date-capsule
 * and greeting ghosts, then the progress card's ghost — its metric row with the
 * ring at the right and the divider — then two row ghosts. Built from theme
 * surface tokens. No spinner and no shimmer: it is decorative (one accessible
 * label, [R.string.today_loading]) and costs no animation.
 */
@Composable
internal fun TodayLoadingSkeleton(modifier: Modifier = Modifier) {
    val c = MomentumThemeColors.current
    val label = stringResource(R.string.today_loading)
    Column(
        modifier = modifier
            .fillMaxSize()
            .clearAndSetSemantics { contentDescription = label }
            .padding(horizontal = MomentumSpacing.screen),
    ) {
        Spacer(
            Modifier.height(
                WindowInsets.statusBars.asPaddingValues().calculateTopPadding() +
                    MomentumSpacing.section,
            ),
        )
        // Chrome: the date capsule, then the greeting line and its support line.
        SkeletonBlock(
            height = DateCapsuleGhostHeight,
            color = c.surfaceSoft,
            shape = RoundedCornerShape(MomentumRadii.pill),
            width = DateCapsuleGhostWidth,
        )
        Spacer(Modifier.height(MomentumSpacing.micro))
        SkeletonBlock(
            height = 24.dp,
            color = c.surface,
            shape = RoundedCornerShape(MomentumRadii.small),
            width = 168.dp,
        )
        Spacer(Modifier.height(MomentumSpacing.micro))
        SkeletonBlock(
            height = 14.dp,
            color = c.surfaceSoft,
            shape = RoundedCornerShape(MomentumRadii.small),
            width = 208.dp,
        )
        Spacer(Modifier.height(MomentumSpacing.section))
        // Progress card: the metric row with its ring on the right, then the
        // divider and the breakdown row beneath it.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(MomentumRadii.control))
                .background(c.surfaceSoft)
                .padding(MomentumSpacing.standard),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.standard),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(MomentumSpacing.micro),
                ) {
                    SkeletonBlock(
                        height = 24.dp,
                        color = c.surface,
                        shape = RoundedCornerShape(MomentumRadii.control),
                        width = 112.dp,
                    )
                    SkeletonBlock(
                        height = 14.dp,
                        color = c.surface,
                        shape = RoundedCornerShape(MomentumRadii.control),
                        width = 184.dp,
                    )
                }
                SkeletonBlock(
                    height = ProgressCardRingSize,
                    color = c.surface,
                    shape = CircleShape,
                    width = ProgressCardRingSize,
                )
            }
            Spacer(Modifier.height(MomentumSpacing.compact))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(Dp.Hairline)
                    .background(c.textPrimary.copy(alpha = 0.08f)),
            )
            Spacer(Modifier.height(MomentumSpacing.compact))
            SkeletonBlock(
                height = 12.dp,
                color = c.surface,
                shape = RoundedCornerShape(MomentumRadii.control),
                width = BreakdownGhostWidth,
            )
        }
        Spacer(Modifier.height(MomentumSpacing.section))
        // Two row ghosts.
        SkeletonBlock(
            height = 88.dp,
            color = c.surface,
            shape = RoundedCornerShape(MomentumRadii.card),
        )
        Spacer(Modifier.height(MomentumSpacing.tight))
        SkeletonBlock(
            height = 88.dp,
            color = c.surface,
            shape = RoundedCornerShape(MomentumRadii.card),
        )
    }
}

/** The date capsule ghost's geometry, matching the real capsule's token padding. */
private val DateCapsuleGhostWidth = 132.dp
private val DateCapsuleGhostHeight = 26.dp
private val BreakdownGhostWidth = 148.dp

/** One decorative skeleton block; no semantics of its own. */
@Composable
private fun SkeletonBlock(
    height: Dp,
    color: Color,
    shape: Shape,
    modifier: Modifier = Modifier,
    width: Dp? = null,
) {
    Box(
        modifier = modifier
            .then(
                if (width != null) {
                    Modifier.size(width, height)
                } else {
                    Modifier.fillMaxWidth().height(height)
                },
            )
            .clip(shape)
            .background(color),
    )
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
 * (REST → PROGRESS → COMPLETION, with the transient MILESTONE on 0 → 1),
 * never permanent wallpaper.
 */
@Composable
fun TodayScreen(
    state: TodayUiState.Success,
    reduceMotion: Boolean,
    milestoneTick: Int,
    onToggle: (Long) -> Unit,
    onOpen: (Long) -> Unit,
    onAdd: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    // Sections are derived from the persisted completion state — this screen
    // never keeps its own copy of "is it done" (DASHBOARD_SPECS §22).
    val remaining = state.remaining
    val finished = state.completed
    val c = MomentumThemeColors.current

    // The milestone reward window (refinement item 6, row 6): ONE keyed effect
    // opens it for MomentumMotion.reward and closes it again. A new tick
    // restarts the window; an un-complete never changes the tick, so it cannot
    // cancel a running moment. Reduced motion skips the transient entirely —
    // the statement still changes instantly and the haptic still fires.
    var milestoneActive by remember { mutableStateOf(false) }
    LaunchedEffect(milestoneTick, reduceMotion) {
        if (milestoneTick == 0 || reduceMotion) {
            milestoneActive = false
            return@LaunchedEffect
        }
        milestoneActive = true
        delay(MomentumMotion.reward.toLong())
        milestoneActive = false
    }

    val auroraState = homeAuroraState(
        isEmpty = state.isEmpty,
        progress = state.progress,
        milestoneActive = milestoneActive,
    )

    // Section moves (DASHBOARD_SPECS §11 / motion table row 4): completing an
    // activity relocates its row from Remaining to Completed, so the row must
    // glide to its new place instead of vanishing. Null specs switch the
    // animation off entirely under reduced motion — never a shortened version.
    val fadeInSpec: FiniteAnimationSpec<Float>? =
        if (reduceMotion) null else tween(durationMillis = MomentumMotion.standard, easing = MomentumMotion.easeOut)
    val placementSpec: FiniteAnimationSpec<IntOffset>? =
        if (reduceMotion) null else spring(stiffness = Spring.StiffnessMediumLow)
    val fadeOutSpec: FiniteAnimationSpec<Float>? =
        if (reduceMotion) null else tween(durationMillis = MomentumMotion.fast)

    // ONE entrance clock (ADR-48 Amendment Note 4 / DECISIONS §28): a single
    // finite 900 ms `animateFloatAsState` — not a loop — driving the approved
    // reference's `fade-slide-up` stagger. The four regions derive their own
    // phase from it against their own start offset (0 / 100 / 200 / 300 ms), and
    // each reads that phase **inside** its layer transform, so no region owns an
    // animation instance. Reduced motion resolves the clock to 1f immediately,
    // which is not a shorter animation but an absent one. It lives here — above
    // the `AnimatedContent` branch key — so a payload refresh (a completion) does
    // not replay it (RISK-6).
    val entranceProgress by animateFloatAsState(
        targetValue = if (reduceMotion) 1f else 0f,
        animationSpec = tween(durationMillis = MomentumMotion.enter, easing = MomentumMotion.easeOut),
        label = "homeEntrance",
    )
    val chromeReveal = entrancePhase(entranceProgress, EntranceChromeOffset)
    val cardReveal = entrancePhase(entranceProgress, EntranceCardOffset)
    val pendingReveal = entrancePhase(entranceProgress, EntrancePendingOffset)
    val completedReveal = entrancePhase(entranceProgress, EntranceCompletedOffset)

    // ONE ambient clock for the whole screen (ADR-48 Amendment Note 6, human
    // ruling §4.7 r2): **two** loops on two of the reference's own periods, both
    // `LinearEasing` (its `ease-in-out`), both derived from one transition. The
    // third approved loop — the FAB's `fab-glow` — is the third loop the record
    // lists, and it lives inside the shared `MomentumFab`, which takes a boolean
    // opt-in and owns its own phase; that component is composed outside the list,
    // so per-item animation instances still never exist. `null` under reduced
    // motion, where **no transition, no `LaunchedEffect` and no coroutine is
    // created at all**.
    val ambient = if (reduceMotion) null else rememberInfiniteTransition(label = "homeAmbient")
    val ambientPulse = ambient?.let {
        it.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = MomentumMotion.ambient, easing = LinearEasing),
            ),
            label = "homeAmbientPulse",
        )
    }
    val boltPulse = ambient?.let {
        it.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = MomentumMotion.verifiedPulse, easing = LinearEasing),
            ),
            label = "homeBoltPulse",
        )
    }

    // The scroll boundary is **layout, not observation** (ADR-47 Amendment Note 5
    // §5): the persistent chrome reports its own measured height once, and the
    // scroll region below it is positioned by exactly that. There is no
    // `NestedScrollConnection`, no scroll-state read, no per-frame observation,
    // no `derivedStateOf` and no parallax — so nothing in Home recomposes while
    // the user scrolls. The write is idempotent and a pure function of the
    // chrome's content, insets and width, so it never fires on scroll and cannot
    // feed back into layout.
    val density = LocalDensity.current
    var chromeHeightPx by remember { mutableIntStateOf(0) }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val viewportDp = maxHeight
        val windowWidth = maxWidth
        // Empty-state centring stays **measured**: the Scaffold's inner padding
        // already ends the content area above the bottom navigation, and the card
        // is centred in the region the chrome leaves over — viewport, minus the
        // measured chrome, minus the reserve the list itself honours
        // (ADR-48 Amendment Note 2). No `Spacer` constants and no screen
        // percentages — free space is simply divided equally above and below, so
        // the card is optically centred at any window size or font scale.
        val emptyRegion = (
            viewportDp - with(density) { chromeHeightPx.toDp() } - EmptyDayBottomReserve
            ).coerceAtLeast(0.dp)

        // Four children, declared in this order, and the order is part of the
        // contract (ADR-47 Amendment Note 5 §6): a `Box` composes and paints its
        // children in declaration order and the merged semantics tree follows
        // composition order, so one order satisfies the paint z-order AND the
        // TalkBack traversal order. The atmosphere is first because it is behind
        // everything; the scroll region and FAB are last.

        // 1 — ATMOSPHERE: persistent, `fillMaxSize()` of the whole content area,
        // a **sibling backdrop** rather than a parent of anything, so no item
        // inside the chrome or the list can compress it. `windowWidth` is the
        // root's own `maxWidth`, read once, never scroll-dependent.
        HomeAtmosphere(
            state = auroraState,
            reduceMotion = reduceMotion,
            windowWidth = windowWidth,
            modifier = Modifier
                .fillMaxSize()
                .testTag(HomeAtmosphereTag),
        )

        // 2 — PERSISTENT HOME TOP CHROME (Date capsule + Greeting + Settings),
        // fixed. It floats over the artwork inside the atmosphere — no plate, no
        // full-width fill, no divider, no scrim discontinuity — so it reads as
        // part of the same environment as the scrollable copy below it.
        HomeTopChrome(
            dateLabel = state.dateLabel,
            greeting = state.greeting.resolve(),
            greetingSupport = stringResource(R.string.today_greeting_support),
            onOpenSettings = onOpenSettings,
            onHeightChanged = { chromeHeightPx = it },
            revealProgress = chromeReveal,
            modifier = Modifier
                .align(Alignment.TopStart)
                .testTag(HomeChromeTag),
        )

        // 3 + 4 — the scrollable Today content and the fixed FAB, in the region
        // that begins exactly at the chrome's bottom edge. Because the two are
        // geometrically disjoint siblings, overlap is unrepresentable rather
        // than merely prevented; `clipToBounds()` is belt-and-braces for the
        // ruling's "appropriate clipping / content bounds".
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = with(density) { chromeHeightPx.toDp() }),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .clipToBounds()
                    .testTag(HomeScrollRegionTag),
                contentPadding = PaddingValues(
                    // 0: the region's own top edge IS the chrome's bottom edge.
                    // Any padding here would open a band of atmosphere between
                    // the chrome and the first item — exactly the disconnection
                    // the fixed-chrome ruling forbids.
                    top = 0.dp,
                    // Quick Add floats over the tail of the list: reserve room so
                    // the last activity can always be scrolled clear of it (§9).
                    // One card padding + the FAB diameter + a full section gap, so
                    // the last row clears the FAB by a section gap and is never
                    // half-covered. The same expression is
                    // [EmptyDayBottomReserve], so the empty card's centring region
                    // and this scroll reserve can never drift apart.
                    bottom = EmptyDayBottomReserve,
                ),
                // The approved reference's `space-sm`: 8 dp between a heading and
                // its first card and between two cards.
                verticalArrangement = Arrangement.spacedBy(MomentumSpacing.tight),
            ) {
                // The progress card is scrollable content: the day's progress
                // figure, its breakdown and both sections share this one
                // LazyColumn, with no nested scrolling container for the list
                // (DASHBOARD_SPECS §4). It renders nothing at all on an empty
                // day, and is then not composed, so it reserves no space.
                if (!state.isEmpty) {
                    item {
                        HomeProgressCard(
                            figureState = heroFigure(state),
                            encouragement = state.encouragement?.resolve(),
                            completedCount = state.doneCount,
                            pendingCount = state.remaining.size,
                            milestoneGlow = milestoneActive,
                            reduceMotion = reduceMotion,
                            ambientPulse = ambientPulse,
                            revealProgress = cardReveal,
                        )
                    }
                }
                if (state.isEmpty) {
                // Nothing eligible today: both sections hide (§13) and the
                // dedicated No-Activity State carries the screen instead. The
                // card communicates **state only** — the Quick Add FAB is the
                // single creation affordance in every state (ADR-48
                // Amendment Note 2), so there is no competing CTA here.
                //
                // §4.10.7's two guarantees are structural here, not arithmetic:
                // the atmosphere is a sibling backdrop sized to the whole
                // content area and the chrome is a sibling above this region, so
                // there is no shared box for this item to squeeze — no amount of
                // content can remove the artwork or the chrome.
                item {
                    // `heightIn(min = …)`, not `height(…)`: at large font
                    // scales the card makes the item grow rather than clip, and
                    // the single scroll surface can still scroll.
                    Box(
                        modifier = Modifier.heightIn(min = emptyRegion),
                        contentAlignment = Alignment.Center,
                    ) {
                        HomeEmptyState()
                    }
                }
            } else {
                // Ordering is fixed: every Pending row precedes every Completed
                // row; empty sections are never rendered (§20).
                if (remaining.isNotEmpty()) {
                    item {
                        HomeSectionHeader(
                            title = stringResource(R.string.today_section_remaining),
                            meta = stringResource(R.string.today_section_scheduled_today),
                            revealProgress = pendingReveal,
                            // ADR-49 Amendment Note 5 (task.md §4.11 CHANGE 4):
                            // Scheduled Today is bare text over the atmosphere, so
                            // it opts into textPrimary. `null` remains the default.
                            labelColor = c.textPrimary,
                            modifier = Modifier
                                .animateItem(
                                    fadeInSpec = fadeInSpec,
                                    placementSpec = placementSpec,
                                    fadeOutSpec = fadeOutSpec,
                                )
                                .padding(horizontal = MomentumSpacing.screen),
                        )
                    }
                }
                items(remaining, key = { it.activity.id }, contentType = { "todayRow" }) { item ->
                    HomeActivityCard(
                        item = item,
                        completed = false,
                        onToggle = { onToggle(item.activity.id) },
                        onOpen = { onOpen(item.activity.id) },
                        modifier = Modifier
                            .animateItem(
                                fadeInSpec = fadeInSpec,
                                placementSpec = placementSpec,
                                fadeOutSpec = fadeOutSpec,
                            )
                            .padding(horizontal = MomentumSpacing.screen),
                    )
                }
                if (finished.isNotEmpty()) {
                    item {
                        // The approved reference's `space-lg` (24 dp) between the
                        // two sections: this row's top spacer plus the list's own
                        // 8 dp arrangement gap is exactly 24 dp, from tokens.
                        Column(
                            modifier = Modifier.animateItem(
                                fadeInSpec = fadeInSpec,
                                placementSpec = placementSpec,
                                fadeOutSpec = fadeOutSpec,
                            ),
                        ) {
                            Spacer(Modifier.height(MomentumSpacing.standard))
                            HomeSectionHeader(
                                title = stringResource(R.string.today_section_completed),
                                meta = stringResource(R.string.today_section_finished),
                                revealProgress = completedReveal,
                                // ADR-49 Amendment Note 5 (task.md §4.11 CHANGE 4):
                                // the emerald glyph measured 2.34:1 Light, below the
                                // 3:1 state-indicator floor, so it becomes ink; the
                                // shape carries the state.
                                accent = c.textPrimary,
                                boltPulse = boltPulse,
                                // ADR-49 Amendment Note 5 (task.md §4.11 CHANGE 4):
                                // the `Finished` band takes textPrimary too.
                                labelColor = c.textPrimary,
                                modifier = Modifier.padding(horizontal = MomentumSpacing.screen),
                            )
                        }
                    }
                }
                items(finished, key = { it.activity.id }, contentType = { "todayRow" }) { item ->
                    HomeActivityCard(
                        item = item,
                        completed = true,
                        onToggle = { onToggle(item.activity.id) },
                        onOpen = { onOpen(item.activity.id) },
                        modifier = Modifier
                            .animateItem(
                                fadeInSpec = fadeInSpec,
                                placementSpec = placementSpec,
                                fadeOutSpec = fadeOutSpec,
                            )
                            .padding(horizontal = MomentumSpacing.screen),
                    )
                }
            }
            }

            // Quick Add (§14 / DECISIONS §20 / ADR-47 §6): the one shared
            // MomentumFab, opening Create Item. It is present in **every** Home
            // state — empty, partially populated, fully populated — because a FAB
            // whose presence is a *state* change reads as an event that arrives
            // rather than as the control that is always there (ADR-48 Amendment
            // Note 2). Composing it unconditionally also removes motion: there is
            // no visibility transition left to run, reduced motion or not.
            //
            // Declared last, so it is painted over the list's tail and announced
            // after the content. Its alignment and insets are relative to this
            // region's bottom-end edge, which is the whole content area's — the
            // chrome is above it, so the placement confirmed on device (§4.1.3)
            // is unchanged. Its two opt-in behaviours (ADR-48 Amendment Note 6,
            // M3/M5) are passed by **Home only**; Habits' FAB is byte-identical.
            MomentumFab(
                onClick = onAdd,
                contentDescription = stringResource(R.string.cd_quick_add),
                glowPulse = true,
                rotateGlyphOnPress = true,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    // A floating element over the list, not part of the glass
                    // bottom bar: it sits one *section* gap above the content
                    // area's bottom edge so it never reads as another bar.
                    .padding(end = MomentumSpacing.screen, bottom = MomentumSpacing.section),
            )
        }
    }
}

/**
 * One region's entrance phase, derived from the screen's single
 * [MomentumMotion.enter] clock: the reference's staggered blocks share one
 * 900 ms window, so a block that starts [offsetMs] late is simply the clock
 * re-based on that offset. `1f` (fully revealed) outside the window, so a region
 * is never hidden once its own 0%/100% window has passed.
 */
private fun entrancePhase(progress: Float, offsetMs: Int): Float {
    val window = MomentumMotion.enter - offsetMs
    if (window <= 0) return 1f
    return ((progress * MomentumMotion.enter - offsetMs) / window).coerceIn(0f, 1f)
}

/** The reference's four `fade-slide-up` start offsets, in milliseconds. */
private const val EntranceChromeOffset = 0
private const val EntranceCardOffset = 100
private const val EntrancePendingOffset = 200
private const val EntranceCompletedOffset = 300

/**
 * Layout-contract tag for the scroll region below the fixed chrome. Carries no
 * accessibility meaning; it exists so the composition (the region's top edge is
 * the chrome's bottom edge, and scrollable content never overlaps the chrome)
 * can be asserted at bounds level.
 */
internal const val HomeScrollRegionTag = "homeScrollRegion"

/**
 * Aurora mapping with the transient milestone window (refinement item 6,
 * row 6): empty → REST, in flight → PROGRESS, complete → COMPLETION, and
 * MILESTONE only while the 650 ms reward window is open. Kept as a plain
 * function so the mapping is testable without composition.
 */
internal fun homeAuroraState(
    isEmpty: Boolean,
    progress: Float,
    milestoneActive: Boolean,
): AuroraState = when {
    isEmpty -> AuroraState.REST
    milestoneActive -> AuroraState.MILESTONE
    progress >= 1f -> AuroraState.COMPLETION
    progress > 0f -> AuroraState.PROGRESS
    else -> AuroraState.REST
}

/** The progress figure derived from the day's sections (ADR-48 / R1). */
private fun heroFigure(state: TodayUiState.Success): HeroFigureState = when {
    state.isEmpty -> HeroFigureState.Hidden
    state.allComplete -> HeroFigureState.Complete(state.doneCount, state.totalCount)
    else -> HeroFigureState.Fraction(state.doneCount, state.totalCount)
}

/**
 * Room reserved at the tail of the list for the floating Quick Add button: one
 * standard card padding + the FAB diameter + a full section gap
 * (16 + 56 + 24 = 96 dp). It is **one named constant used twice** — as the
 * `LazyColumn`'s `contentPadding.bottom` and, subtracted from the measured
 * viewport, as the empty card's centring region — so the reserve the list
 * honours and the region the empty state is centred in can never drift apart.
 */
private val EmptyDayBottomReserve = MomentumSpacing.card + MomentumFabSize + MomentumSpacing.section

/**
 * Calm, useful, action-oriented empty state (§3, §25): no guilt, no
 * streak-loss language, no glyph — and **no CTA**. The card communicates
 * state only; the Quick Add FAB is the single creation affordance on Home in
 * every state, so a TalkBack user hears exactly one add action and the screen
 * never shows two competing CTAs (ADR-48 Amendment Note 2).
 *
 * `EmptyState.kt` is extended by one additive, default-`null` `bodyColor`
 * parameter only (ADR-49 Amendment Note 5); its layout, copy, CTA contract and
 * every other caller are unchanged. The approved reference specifies no empty
 * state at all — this contract is Momentum's own and is retained verbatim
 * (§8.12 C7).
 */
@Composable
private fun HomeEmptyState(modifier: Modifier = Modifier) {
    val c = MomentumThemeColors.current
    EmptyState(
        title = stringResource(R.string.today_empty_title),
        body = stringResource(R.string.today_empty_body),
        modifier = modifier.padding(horizontal = MomentumSpacing.screen),
        icon = null,
        // ADR-49 Amendment Note 5 (task.md §4.11): the body is bare over the
        // atmosphere, where textSecondary fails 4.5:1, so Home opts into ink.
        // `null` keeps the shared token for every other caller.
        bodyColor = c.textPrimary,
    )
}

/**
 * Home card metadata (DECISIONS §11): the optional configured time plus the
 * target progress when applicable. Deliberately no recurrence or activity-type
 * label — classification belongs to detail screens, not Home (§11 Rejected).
 * An activity with neither a time nor a target renders no metadata row at all
 * rather than a placeholder (§29).
 *
 * **No duration and no entry count exist in the domain** (ADR-48 Amendment Note 5,
 * §8.12 C3): `Activity` carries only `dueTime`, `targetPerOccurrence` and `unit`,
 * so the reference's `15 mins` / `3 entries` cannot be sourced without a schema
 * change, which this task forbids. The secondary line is therefore exactly what
 * Momentum can show, and no data is invented to match the reference.
 */
internal fun homeMetadataLabel(item: TodayItem): String = buildString {
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
