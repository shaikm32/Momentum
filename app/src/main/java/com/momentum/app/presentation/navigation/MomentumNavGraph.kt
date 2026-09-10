package com.momentum.app.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.momentum.app.presentation.components.AppBackground
import com.momentum.app.presentation.screens.activities.ActivitiesRoute
import com.momentum.app.presentation.screens.activityedit.ActivityEditRoute
import com.momentum.app.presentation.screens.calendar.CalendarRoute
import com.momentum.app.presentation.screens.detail.ActivityDetailRoute
import com.momentum.app.presentation.screens.insights.InsightsRoute
import com.momentum.app.presentation.screens.onboarding.OnboardingRoute
import com.momentum.app.presentation.screens.settings.SettingsRoute
import com.momentum.app.presentation.screens.themes.ThemeGalleryRoute
import com.momentum.app.presentation.screens.today.TodayRoute

private data class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

private val Destinations = listOf(
    TopLevelDestination(Routes.TODAY, "Today", Icons.Filled.Home),
    TopLevelDestination(Routes.ACTIVITIES, "Activities", Icons.Filled.List),
    TopLevelDestination(Routes.INSIGHTS, "Insights", Icons.Filled.BarChart),
    TopLevelDestination(Routes.CALENDAR, "Calendar", Icons.Filled.CalendarMonth),
    TopLevelDestination(Routes.SETTINGS, "Settings", Icons.Filled.Settings),
)

private val TopLevelRoutes = Destinations.map { it.route }.toSet()

/**
 * Single NavHost: 5 top-level tabs with a bottom bar + secondary graph
 * (add/edit/details/themes) without the bar. Onboarding is the start
 * destination until completed (gated by [onboardingDone]).
 */
@Composable
fun MomentumNavGraph(onboardingDone: Boolean, openActivityId: Long?) {
    val navController = rememberNavController()
    val snackbar = remember { SnackbarHostState() }
    AppBackground {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbar) },
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val showBar = currentDestination?.hierarchy?.any { it.route in TopLevelRoutes } == true
                if (showBar) {
                    NavigationBar {
                        Destinations.forEach { dest ->
                            NavigationBarItem(
                                icon = { Icon(dest.icon, contentDescription = dest.label) },
                                label = { Text(dest.label) },
                                selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true,
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
            },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = if (onboardingDone) Routes.TODAY else Routes.ONBOARDING,
                modifier = Modifier.padding(innerPadding),
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
                        onAddActivity = { navController.navigate(Routes.ADD) },
                        snackbar = snackbar,
                    )
                }
                composable(Routes.ACTIVITIES) {
                    ActivitiesRoute(
                        onOpenActivity = { navController.navigate(Routes.detail(it)) },
                        onAddActivity = { navController.navigate(Routes.ADD) },
                        snackbar = snackbar,
                    )
                }
                composable(Routes.INSIGHTS) {
                    InsightsRoute(onAddActivity = { navController.navigate(Routes.ADD) })
                }
                composable(Routes.CALENDAR) {
                    CalendarRoute(
                        onOpenActivity = { navController.navigate(Routes.detail(it)) },
                        snackbar = snackbar,
                    )
                }
                composable(Routes.SETTINGS) {
                    SettingsRoute(
                        onOpenThemes = { navController.navigate(Routes.THEMES) },
                        snackbar = snackbar,
                    )
                }
                composable(Routes.THEMES) {
                    ThemeGalleryRoute(onBack = { navController.popBackStack() })
                }
                composable(Routes.ADD) {
                    ActivityEditRoute(
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
                    ActivityEditRoute(
                        activityId = id,
                        onSaved = { navController.popBackStack() },
                        onBack = { navController.popBackStack() },
                        snackbar = snackbar,
                    )
                }
                composable(
                    Routes.DETAIL,
                    arguments = listOf(navArgument("activityId") { type = NavType.LongType }),
                ) { entry ->
                    val id = entry.arguments?.getLong("activityId") ?: return@composable
                    ActivityDetailRoute(
                        activityId = id,
                        onBack = { navController.popBackStack() },
                        onEdit = { navController.navigate(Routes.edit(it)) },
                        onDeleted = {
                            navController.popBackStack(Routes.TODAY, inclusive = false)
                        },
                        snackbar = snackbar,
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
        }
    }
}
