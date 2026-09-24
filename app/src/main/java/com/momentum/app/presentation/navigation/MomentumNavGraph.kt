package com.momentum.app.presentation.navigation

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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.momentum.app.domain.model.ActivityType
import com.momentum.app.domain.model.HabitIntent
import com.momentum.app.presentation.components.AppBackground
import com.momentum.app.presentation.components.LiquidGlassBottomBar
import com.momentum.app.presentation.components.LiquidGlassTab
import com.momentum.app.presentation.rememberReduceMotion
import com.momentum.app.presentation.theme.MomentumSpacing
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
import com.momentum.app.presentation.theme.MomentumThemeColors

private data class TopLevelDestination(
    val route: String,
    val label: String,
    /** Filled style — shown when the tab is active (§8). */
    val iconFilled: ImageVector,
    /** Outlined style — shown when the tab is inactive (§8). */
    val iconOutlined: ImageVector,
)

/** Locked product navigation (Design System v1 §11): Home · Habits · Tasks · Insights. */
private val Destinations = listOf(
    TopLevelDestination(Routes.TODAY, "Home", Icons.Rounded.Home, Icons.Outlined.Home),
    TopLevelDestination(Routes.HABITS, "Habits", Icons.Rounded.Repeat, Icons.Outlined.Repeat),
    TopLevelDestination(Routes.TASKS, "Tasks", Icons.Rounded.Checklist, Icons.Outlined.Checklist),
    TopLevelDestination(Routes.INSIGHTS, "Insights", Icons.Rounded.BarChart, Icons.Outlined.BarChart),
)

private val TopLevelRoutes = Destinations.map { it.route }.toSet()

/**
 * Single NavHost: 4 locked bottom-nav tabs (Home, Habits, Tasks, Insights)
 * + secondary routes (Settings from Home's header, Calendar/history from
 * Insights, add/edit/details/themes) without the bar. Onboarding is the start
 * destination until completed (gated by [onboardingDone]).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MomentumNavGraph(onboardingDone: Boolean, openActivityId: Long?) {
    val navController = rememberNavController()
    val snackbar = remember { SnackbarHostState() }
    // Slice 1: + opens the Create Item chooser (Habit / Task), not the editor directly.
    var showCreateItem by remember { mutableStateOf(false) }
    val createSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val reduceMotion = rememberReduceMotion(null)
    // Onboarding draws its own full-bleed photographic backdrop, so it opts
    // out of the Scaffold content padding; every other route is unchanged.
    val isOnboarding =
        currentDestination?.hierarchy?.any { it.route == Routes.ONBOARDING } == true
    AppBackground {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbar) },
            bottomBar = {
                val showBar = currentDestination?.hierarchy?.any { it.route in TopLevelRoutes } == true
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
                                val selected =
                                    currentDestination?.hierarchy?.any { it.route == dest.route } == true
                                LiquidGlassTab(
                                    icon = if (selected) dest.iconFilled else dest.iconOutlined,
                                    label = dest.label,
                                    selected = selected,
                                    onClick = {
                                        navController.navigate(dest.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
            },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = if (onboardingDone) Routes.TODAY else Routes.ONBOARDING,
                modifier = if (isOnboarding) Modifier else Modifier.padding(innerPadding),
                // Soft Aurora crossfade (§32): quick in, quieter out; pop
                // transitions inherit these. Reduced motion swaps in plain
                // None so content never forces an animation on the user.
                enterTransition = if (reduceMotion) {
                    { EnterTransition.None }
                } else {
                    { fadeIn(tween(200, easing = EaseOut)) }
                },
                exitTransition = if (reduceMotion) {
                    { ExitTransition.None }
                } else {
                    { fadeOut(tween(150, easing = EaseInOut)) }
                },
            ) {
                composable(Routes.ONBOARDING) {
                    OnboardingRoute(onDone = {
                        navController.navigate(Routes.TODAY) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    })
                }
                composable(Routes.TODAY) {
                    TodayRoute(
                        onOpenActivity = { navController.navigate(Routes.detail(it)) },
                        onAddActivity = { showCreateItem = true },
                        onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                        snackbar = snackbar,
                    )
                }
                // Habits is the dedicated management screen (Phase 2B / ADR-22
                // § Current app). Tasks keeps the shared Activities list.
                composable(Routes.HABITS) {
                    HabitsRoute(
                        onOpenHabit = { navController.navigate(Routes.detail(it)) },
                        onAddHabit = { showCreateItem = true },
                        snackbar = snackbar,
                    )
                }
                composable(Routes.TASKS) {
                    ActivitiesRoute(
                        type = ActivityType.TASK,
                        title = "Tasks",
                        onOpenActivity = { navController.navigate(Routes.detail(it)) },
                        onAddActivity = { showCreateItem = true },
                        snackbar = snackbar,
                    )
                }
                composable(Routes.INSIGHTS) {
                    InsightsRoute(
                        onAddActivity = { showCreateItem = true },
                        onOpenCalendar = { navController.navigate(Routes.CALENDAR) },
                    )
                }
                // Calendar/history is reached through Insights (§26), never a tab.
                composable(Routes.CALENDAR) {
                    CalendarRoute(
                        onOpenActivity = { navController.navigate(Routes.detail(it)) },
                        onBack = { navController.popBackStack() },
                        snackbar = snackbar,
                    )
                }
                composable(Routes.SETTINGS) {
                    SettingsRoute(
                        onOpenThemes = { navController.navigate(Routes.THEMES) },
                        onBack = { navController.popBackStack() },
                        snackbar = snackbar,
                    )
                }
                composable(Routes.THEMES) {
                    ThemeGalleryRoute(onBack = { navController.popBackStack() })
                }
                // Guided creation: + → Create Item → Habit opens the unified
                // habit screen (intent cards + templates + details), Task goes
                // straight to its configuration. Editing reuses the same
                // config screens via EDIT (kind-appropriate UI, type locked).
                composable(Routes.CREATE_INTENT) {
                    HabitConfigRoute(
                        activityId = null,
                        intent = HabitIntent.BUILD,
                        onSaved = { id ->
                            navController.popBackStack()
                            navController.navigate(Routes.detail(id))
                        },
                        onBack = { navController.popBackStack() },
                        snackbar = snackbar,
                    )
                }
                composable(
                    Routes.CREATE_HABIT,
                    arguments = listOf(
                        navArgument("intent") {
                            type = NavType.StringType
                            defaultValue = HabitIntent.BUILD.name
                        },
                    ),
                ) { entry ->
                    HabitConfigRoute(
                        activityId = null,
                        intent = Routes.parseHabitIntent(entry.arguments?.getString("intent")),
                        onSaved = { id ->
                            navController.popBackStack()
                            navController.navigate(Routes.detail(id))
                        },
                        onBack = { navController.popBackStack() },
                        snackbar = snackbar,
                    )
                }
                composable(Routes.CREATE_TASK) {
                    TaskConfigRoute(
                        activityId = null,
                        onSaved = { id ->
                            navController.popBackStack()
                            navController.navigate(Routes.detail(id))
                        },
                        onBack = { navController.popBackStack() },
                        snackbar = snackbar,
                    )
                }
                composable(
                    Routes.EDIT,
                    arguments = listOf(navArgument("activityId") { type = NavType.LongType; defaultValue = -1L }),
                ) { entry ->
                    val id = entry.arguments?.getLong("activityId")?.takeIf { it != -1L }
                    if (id != null) {
                        EditRoute(
                            activityId = id,
                            onSaved = { navController.popBackStack() },
                            onBack = { navController.popBackStack() },
                            snackbar = snackbar,
                        )
                    } else {
                        LaunchedEffect(Unit) { navController.popBackStack() }
                    }
                }
                composable(
                    Routes.DETAIL,
                    arguments = listOf(navArgument("activityId") { type = NavType.LongType }),
                ) { entry ->
                    val id = entry.arguments?.getLong("activityId") ?: return@composable
                    // Type gate: HABIT (ACTIVE/PAUSED) opens Habit Details;
                    // tasks and archived rows keep the legacy detail body.
                    DetailTypeGate(
                        activityId = id,
                        onHabit = {
                            HabitDetailRoute(
                                activityId = id,
                                onBack = { navController.popBackStack() },
                                onEdit = { navController.navigate(Routes.edit(it)) },
                                onDeleted = {
                                    navController.popBackStack(Routes.TODAY, inclusive = false)
                                },
                                snackbar = snackbar,
                            )
                        },
                        onOther = {
                            ActivityDetailRoute(
                                activityId = id,
                                onBack = { navController.popBackStack() },
                                onEdit = { navController.navigate(Routes.edit(it)) },
                                onDeleted = {
                                    navController.popBackStack(Routes.TODAY, inclusive = false)
                                },
                                snackbar = snackbar,
                            )
                        },
                    )
                }
            }
            // Deep-open from a reminder notification (MainActivity extra), consumed once
            // so theme changes / recompositions never push duplicate destinations.
            var deepOpenConsumed by remember(openActivityId) { mutableStateOf(false) }
            LaunchedEffect(openActivityId, deepOpenConsumed) {
                if (openActivityId != null && !deepOpenConsumed) {
                    deepOpenConsumed = true
                    navController.navigate(Routes.detail(openActivityId))
                }
            }
            if (showCreateItem) {
                CreateItemSheet(
                    sheetState = createSheetState,
                    onDismiss = { showCreateItem = false },
                    onPick = { type ->
                        showCreateItem = false
                        when (type) {
                            ActivityType.HABIT -> navController.navigate(Routes.CREATE_INTENT)
                            ActivityType.TASK -> navController.navigate(Routes.CREATE_TASK)
                        }
                    },
                )
            }
        }
    }
}
