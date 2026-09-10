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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.momentum.app.presentation.components.AppBackground
import com.momentum.app.presentation.screens.calendar.CalendarScreen
import com.momentum.app.presentation.screens.today.TodayScreen

private data class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

private val destinations = listOf(
    TopLevelDestination(Routes.TODAY, "Today", Icons.Filled.Home),
    TopLevelDestination(Routes.ACTIVITIES, "Activities", Icons.Filled.List),
    TopLevelDestination(Routes.INSIGHTS, "Insights", Icons.Filled.BarChart),
    TopLevelDestination(Routes.CALENDAR, "Calendar", Icons.Filled.CalendarMonth),
    TopLevelDestination(Routes.SETTINGS, "Settings", Icons.Filled.Settings),
)

/**
 * Phase 0 navigation foundation: single NavHost with bottom bar.
 * Secondary destinations (add/edit/details/theme picker) land in Phase 4+.
 */
@Composable
fun MomentumNavGraph() {
    val navController = rememberNavController()
    AppBackground {
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            bottomBar = {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    destinations.forEach { dest ->
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
            },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Routes.TODAY,
                modifier = Modifier.padding(innerPadding),
            ) {
                composable(Routes.TODAY) { TodayScreen() }
                composable(Routes.ACTIVITIES) { PlaceholderScreen("Activities lands in Phase 5.") }
                composable(Routes.INSIGHTS) { PlaceholderScreen("Insights lands in Phase 7.") }
                composable(Routes.CALENDAR) { CalendarScreen() }
                composable(Routes.SETTINGS) { PlaceholderScreen("Settings lands in Phase 10.") }
            }
        }
    }
}
