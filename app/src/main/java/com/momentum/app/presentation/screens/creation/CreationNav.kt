package com.momentum.app.presentation.screens.creation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.ActivityType
import com.momentum.app.presentation.components.MomentumTopBar
import com.momentum.app.presentation.rememberContainer
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumThemeColors

/**
 * Edit entry: loads the persisted row once, then shows the configuration UI
 * matching its kind — habit (with its persisted intent) or task. Type and
 * intent are never editable here, so Habit ↔ Task conversion is impossible
 * and every stored field is loaded before any save can happen.
 */
@Composable
fun EditRoute(
    activityId: Long,
    onSaved: () -> Unit,
    onBack: () -> Unit,
    snackbar: SnackbarHostState,
) {
    val container = rememberContainer()
    var activity by remember(activityId) { mutableStateOf<Activity?>(null) }
    var missing by remember(activityId) { mutableStateOf(false) }
    LaunchedEffect(activityId) {
        val loaded = container.activityRepository.getById(activityId)
        if (loaded == null) {
            missing = true
        } else {
            activity = loaded
        }
    }
    val current = activity
    when {
        missing -> {
            Column(modifier = Modifier.fillMaxSize()) {
                MomentumTopBar(title = "Edit", onBack = onBack)
                LazyColumn(
                    contentPadding = PaddingValues(MomentumSpacing.screen),
                    verticalArrangement = Arrangement.spacedBy(MomentumSpacing.item),
                ) {
                    item {
                        Text(
                            "Couldn't find that item. It may have been deleted.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MomentumThemeColors.current.textSecondary,
                        )
                    }
                }
            }
        }
        current == null -> {
            Column(modifier = Modifier.fillMaxSize()) {
                MomentumTopBar(title = "Edit", onBack = onBack)
                LazyColumn(
                    contentPadding = PaddingValues(MomentumSpacing.screen),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    item { CircularProgressIndicator() }
                }
            }
        }
        current.activityType == ActivityType.TASK -> {
            TaskConfigRoute(
                activityId = activityId,
                onSaved = { onSaved() },
                onBack = onBack,
                snackbar = snackbar,
            )
        }
        else -> {
            HabitConfigRoute(
                activityId = activityId,
                intent = current.habitIntent,
                onSaved = { onSaved() },
                onBack = onBack,
                snackbar = snackbar,
            )
        }
    }
}
