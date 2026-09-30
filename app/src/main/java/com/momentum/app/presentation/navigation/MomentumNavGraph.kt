package com.momentum.app.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import com.momentum.app.R
import com.momentum.app.domain.model.ActivityType
import com.momentum.app.domain.model.HabitIntent
import com.momentum.app.presentation.components.AppBackground
import com.momentum.app.presentation.components.LiquidGlassBottomBar
import com.momentum.app.presentation.components.LiquidGlassTab
import com.momentum.app.presentation.rememberReduceMotion
import com.momentum.app.presentation.screens.activities.ActivitiesRoute
import com.momentum.app.presentation.screens.calendar.CalendarRoute
import com.momentum.app.presentation.screens.creation.EditRoute
import com.momentum.app.presentation.screens.creation.HabitConfigRoute
import com.momentum.app.presentation.screens.creation.TaskConfigRoute
import com.momentum.app.presentation.screens.createitem.CreateItemSheet
import com.momentum.app.presentation.screens.detail.ActivityDetailRoute
import com.momentum.app.presentation.screens.detail.HabitDetailRoute
import com.momentum.app.presentation.screens.habits.HabitsRoute
import com.momentum.app.presentation.screens.insights.InsightsRoute
import com.momentum.app.presentation.screens.onboarding.OnboardingRoute
import com.momentum.app.presentation.screens.settings.SettingsRoute
import com.momentum.app.presentation.screens.themes.ThemeGalleryRoute
import com.momentum.app.presentation.screens.today.TodayRoute
import com.momentum.app.presentation.theme.MomentumSpacing

private data class TopLevelDestination(
    val key: NavKey,
    /** Resolved at the call site with `stringResource(label)`. */
    val label: Int,
    /** Filled style — shown when the tab is active (§8). */
    val iconFilled: ImageVector,
    /** Outlined style — shown when the tab is inactive (§8). */
    val iconOutlined: ImageVector,
)

/** Locked product navigation (Design System v1 §11): Home · Habits · Tasks · Insights. */
private val Destinations = listOf(
    TopLevelDestination(Routes.Today, R.string.nav_home, Icons.Rounded.Home, Icons.Outlined.Home),
    TopLevelDestination(Routes.Habits, R.string.nav_habits, Icons.Rounded.Repeat, Icons.Outlined.Repeat),
    TopLevelDestination(Routes.Tasks, R.string.nav_tasks, Icons.Rounded.Checklist, Icons.Outlined.Checklist),
    TopLevelDestination(Routes.Insights, R.string.nav_insights, Icons.Rounded.BarChart, Icons.Outlined.BarChart),
)

/** Nav2 `popBackStack()` — drops the top entry, never emptying the stack (NavDisplay's invariant). */
private fun NavBackStack<NavKey>.pop() {
    if (size > 1) removeLastOrNull()
}

/**
 * Nav2 `popBackStack(Routes.TODAY, inclusive = false)`: drops everything above
 * Today. When Today is not on the stack Nav2 popped nothing — same here.
 */
private fun NavBackStack<NavKey>.popToToday() {
    if (lastOrNull() == Routes.Today || none { it == Routes.Today }) return
    while (lastOrNull() != Routes.Today) removeLastOrNull()
}

/**
 * Nav2 tab switch, translated one entry at a time:
 * `navigate(key) { popUpTo(start) { saveState = true }; launchSingleTop = true; restoreState = true }`.
 * Re-selecting the open tab is a no-op (the old `launchSingleTop`), and the
 * popped tab's saveable state is kept by
 * [rememberRetainingSaveableStateHolderNavEntryDecorator] so switching back
 * restores it exactly like the old `restoreState`.
 */
private fun NavBackStack<NavKey>.selectTab(key: NavKey) {
    if (lastOrNull() == key) return
    while (size > 1) removeLastOrNull()
    if (lastOrNull() != key) add(key)
}

/**
 * `SaveableStateHolderNavEntryDecorator` with the `removeState` pop-callback
 * disabled.
 *
 * Nav3's stock decorator discards an entry's saveable state the moment it is
 * popped, while Nav2's bottom-bar contract (`saveState`/`restoreState`) kept a
 * tab's scroll position across tab switches. Retaining the state keyed by
 * `contentKey` reproduces those semantics: re-pushing the same key restores it.
 * ViewModels are *not* retained — `rememberViewModelStoreNavEntryDecorator`
 * still clears an entry's store on pop, matching Nav2 (popped entries kept
 * their saved state but lost their ViewModels).
 */
@Composable
private fun rememberRetainingSaveableStateHolderNavEntryDecorator(): NavEntryDecorator<NavKey> {
    val saveableStateHolder = rememberSaveableStateHolder()
    return remember(saveableStateHolder) {
        NavEntryDecorator<NavKey>(onPop = { /* state retained deliberately */ }) { entry ->
            saveableStateHolder.SaveableStateProvider(entry.contentKey) { entry.Content() }
        }
    }
}

/**
 * Single NavDisplay: 4 locked bottom-nav tabs (Home, Habits, Tasks, Insights)
 * + secondary routes (Settings from Home's header, Calendar/history from
 * Insights, add/edit/details/themes) without the bar. Onboarding is the start
 * destination until completed (gated by [onboardingDone]).
 *
 * One flat [NavBackStack] of `@Serializable` [NavKey]s (ADR-42) replaces the
 * Nav2 `NavHost`; every entry below behaves exactly as its `composable(...)`
 * predecessor did.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MomentumNavGraph(onboardingDone: Boolean, openActivityId: Long?) {
    val backStack = rememberNavBackStack(if (onboardingDone) Routes.Today else Routes.Onboarding)
    val snackbar = remember { SnackbarHostState() }
    // Slice 1: + opens the Create Item chooser (Habit / Task), not the editor directly.
    var showCreateItem by remember { mutableStateOf(false) }
    val createSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val currentKey = backStack.lastOrNull()
    val reduceMotion = rememberReduceMotion(null)
    // Onboarding draws its own full-bleed photographic backdrop, so it opts
    // out of the Scaffold content padding; every other route is unchanged.
    val isOnboarding = currentKey == Routes.Onboarding
    // Edge-to-edge content (ADR-33): Home and Habits draw their Aurora artwork
    // to the physical top edge, *behind* the status bar, so the Scaffold's
    // content inset must not pad them below it — that padding is what left a
    // bare strip of page background behind the status-bar icons. Every other
    // route keeps the default insets unchanged and still clears the bar; the
    // bottom and horizontal halves always survive, so content still clears the
    // navigation bar (the floating bar handles its own inset in its slot).
    val drawsBehindStatusBar = currentKey == Routes.Today || currentKey == Routes.Habits
    val defaultContentInsets = ScaffoldDefaults.contentWindowInsets
    val systemBars = WindowInsets.systemBars
    val contentWindowInsets = remember(drawsBehindStatusBar, defaultContentInsets, systemBars) {
        if (drawsBehindStatusBar) {
            systemBars.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)
        } else {
            defaultContentInsets
        }
    }
    // Soft Aurora crossfade (§32): quick in, quieter out; push, pop and
    // predictive back all share it (as they did under Nav2, where pop
    // transitions inherited enter/exit). Reduced motion swaps in plain None so
    // content never forces an animation on the user.
    val crossfade: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform = {
        if (reduceMotion) {
            ContentTransform(EnterTransition.None, ExitTransition.None)
        } else {
            ContentTransform(
                targetContentEnter = fadeIn(tween(200, easing = EaseOut)),
                initialContentExit = fadeOut(tween(150, easing = EaseInOut)),
            )
        }
    }
    AppBackground {
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = contentWindowInsets,
            snackbarHost = { SnackbarHost(snackbar) },
            bottomBar = {
                val showBar = Destinations.any { it.key == currentKey }
                if (showBar) {
                    // Scaffold expects the bottom bar to handle its own insets
                    // (M3 Scaffold KDoc) — pad by the navigation-bar inset so the
                    // floating bar never sits under system gesture areas.
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(
                                WindowInsets.navigationBars.only(WindowInsetsSides.Bottom),
                            )
                            .padding(
                                start = MomentumSpacing.screen,
                                end = MomentumSpacing.screen,
                                bottom = MomentumSpacing.compact,
                            ),
                    ) {
                        LiquidGlassBottomBar {
                            Destinations.forEach { dest ->
                                LiquidGlassTab(
                                    icon = if (currentKey == dest.key) dest.iconFilled else dest.iconOutlined,
                                    label = stringResource(dest.label),
                                    selected = currentKey == dest.key,
                                    onClick = { backStack.selectTab(dest.key) },
                                )
                            }
                        }
                    }
                }
            },
        ) { innerPadding ->
            NavDisplay(
                backStack = backStack,
                modifier = if (isOnboarding) Modifier else Modifier.padding(innerPadding),
                onBack = { backStack.pop() },
                entryDecorators = listOf<NavEntryDecorator<NavKey>>(
                    // Must stay first: the ViewModel decorator below reads the
                    // per-entry SavedStateRegistryOwner this one provides.
                    rememberRetainingSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
                transitionSpec = crossfade,
                popTransitionSpec = crossfade,
                predictivePopTransitionSpec = { crossfade(this) },
                entryProvider = entryProvider {
                    entry<Routes.Onboarding> {
                        OnboardingRoute(onDone = {
                            // Nav2: navigate(TODAY) { popUpTo(ONBOARDING) { inclusive = true } }
                            if (backStack.any { it == Routes.Onboarding }) {
                                backStack.clear()
                                backStack.add(Routes.Today)
                            }
                        })
                    }
                    entry<Routes.Today> {
                        TodayRoute(
                            onOpenActivity = { backStack.add(Routes.detail(it)) },
                            onAddActivity = { showCreateItem = true },
                            onOpenSettings = { backStack.add(Routes.Settings) },
                            snackbar = snackbar,
                        )
                    }
                    // Habits is the dedicated management screen (Phase 2B / ADR-22
                    // § Current app). Tasks keeps the shared Activities list.
                    entry<Routes.Habits> {
                        HabitsRoute(
                            onOpenHabit = { backStack.add(Routes.detail(it)) },
                            onAddHabit = { showCreateItem = true },
                            snackbar = snackbar,
                        )
                    }
                    entry<Routes.Tasks> {
                        ActivitiesRoute(
                            type = ActivityType.TASK,
                            title = stringResource(R.string.nav_tasks),
                            onOpenActivity = { backStack.add(Routes.detail(it)) },
                            onAddActivity = { showCreateItem = true },
                            snackbar = snackbar,
                        )
                    }
                    entry<Routes.Insights> {
                        InsightsRoute(
                            onAddActivity = { showCreateItem = true },
                            onOpenCalendar = { backStack.add(Routes.Calendar) },
                        )
                    }
                    // Calendar/history is reached through Insights (§26), never a tab.
                    entry<Routes.Calendar> {
                        CalendarRoute(
                            onOpenActivity = { backStack.add(Routes.detail(it)) },
                            onBack = { backStack.pop() },
                            snackbar = snackbar,
                        )
                    }
                    entry<Routes.Settings> {
                        SettingsRoute(
                            onOpenThemes = { backStack.add(Routes.Themes) },
                            onBack = { backStack.pop() },
                            snackbar = snackbar,
                        )
                    }
                    entry<Routes.Themes> {
                        ThemeGalleryRoute(onBack = { backStack.pop() })
                    }
                    // Guided creation: + → Create Item → Habit opens the unified
                    // habit screen (intent cards + templates + details), Task goes
                    // straight to its configuration. Editing reuses the same
                    // config screens via EDIT (kind-appropriate UI, type locked).
                    entry<Routes.CreateIntent> {
                        HabitConfigRoute(
                            activityId = null,
                            intent = HabitIntent.BUILD,
                            onSaved = { id ->
                                backStack.pop()
                                backStack.add(Routes.detail(id))
                            },
                            onBack = { backStack.pop() },
                            snackbar = snackbar,
                        )
                    }
                    entry<Routes.CreateTask> {
                        TaskConfigRoute(
                            activityId = null,
                            onSaved = { id ->
                                backStack.pop()
                                backStack.add(Routes.detail(id))
                            },
                            onBack = { backStack.pop() },
                            snackbar = snackbar,
                        )
                    }
                    // The old string route defaulted activityId to -1 when the
                    // argument was absent; a typed key always carries one, so the
                    // sentinel check only survives for an explicit -1.
                    entry<Routes.Edit> { key ->
                        val id = key.activityId.takeIf { it != -1L }
                        if (id != null) {
                            EditRoute(
                                activityId = id,
                                onSaved = { backStack.pop() },
                                onBack = { backStack.pop() },
                                snackbar = snackbar,
                            )
                        } else {
                            LaunchedEffect(Unit) { backStack.pop() }
                        }
                    }
                    entry<Routes.Detail> { key ->
                        val id = key.activityId
                        // Type gate: HABIT (ACTIVE/PAUSED) opens Habit Details;
                        // tasks and archived rows keep the legacy detail body.
                        DetailTypeGate(
                            activityId = id,
                            onHabit = {
                                HabitDetailRoute(
                                    activityId = id,
                                    onBack = { backStack.pop() },
                                    onEdit = { backStack.add(Routes.edit(it)) },
                                    onDeleted = { backStack.popToToday() },
                                    snackbar = snackbar,
                                )
                            },
                            onOther = {
                                ActivityDetailRoute(
                                    activityId = id,
                                    onBack = { backStack.pop() },
                                    onEdit = { backStack.add(Routes.edit(it)) },
                                    onDeleted = { backStack.popToToday() },
                                    snackbar = snackbar,
                                )
                            },
                        )
                    }
                },
            )
            // Deep-open from a reminder notification (MainActivity extra), consumed once
            // so theme changes / recompositions never push duplicate destinations.
            var deepOpenConsumed by remember(openActivityId) { mutableStateOf(false) }
            LaunchedEffect(openActivityId, deepOpenConsumed) {
                if (openActivityId != null && !deepOpenConsumed) {
                    deepOpenConsumed = true
                    backStack.add(Routes.detail(openActivityId))
                }
            }
            if (showCreateItem) {
                CreateItemSheet(
                    sheetState = createSheetState,
                    onDismiss = { showCreateItem = false },
                    onPick = { type ->
                        showCreateItem = false
                        when (type) {
                            ActivityType.HABIT -> backStack.add(Routes.CreateIntent)
                            ActivityType.TASK -> backStack.add(Routes.CreateTask)
                        }
                    },
                )
            }
        }
    }
}
