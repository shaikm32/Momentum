package com.momentum.app.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.momentum.app.domain.model.ActivityStatus
import com.momentum.app.domain.model.ActivityType
import com.momentum.app.presentation.rememberContainer
import com.momentum.app.presentation.theme.MomentumThemeColors

/**
 * One-shot type gate for `Routes.DETAIL`: loads the row once, then shows
 * Habit Details for ACTIVE/PAUSED habits or the legacy detail body for
 * tasks/archived habits. Keeps both ViewModels on the same NavBackStackEntry
 * (Phase 2A lifecycle pattern).
 */
@Composable
fun DetailTypeGate(
    activityId: Long,
    onHabit: @Composable () -> Unit,
    onOther: @Composable () -> Unit,
) {
    val container = rememberContainer()
    var isHabit by remember(activityId) { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(activityId) {
        val activity = container.activityRepository.getById(activityId)
        isHabit = activity != null &&
            activity.activityType == ActivityType.HABIT &&
            (activity.status == ActivityStatus.ACTIVE || activity.status == ActivityStatus.PAUSED)
    }

    when (isHabit) {
        null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MomentumThemeColors.current.primary)
        }
        true -> onHabit()
        false -> onOther()
    }
}
