package com.momentum.app.presentation.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.AppContainer
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.Recurrence
import com.momentum.app.presentation.momentumViewModel
import com.momentum.app.presentation.theme.MomentumThemeColors
import java.time.LocalDate
import kotlinx.coroutines.launch

class OnboardingViewModel(private val container: AppContainer) : ViewModel() {

    fun finish(seedExamples: Boolean, onDone: () -> Unit) {
        viewModelScope.launch {
            if (seedExamples) seed()
            container.settingsRepository.update { it.copy(onboardingDone = true) }
            onDone()
        }
    }

    private suspend fun seed() {
        val today = LocalDate.now()
        val examples = listOf(
            Activity(title = "Drink water", iconKey = "water", startDate = today, recurrence = Recurrence.Daily, targetPerOccurrence = 8.0, unit = "glasses", priority = 2),
            Activity(title = "Read 20 pages", iconKey = "learning", startDate = today, recurrence = Recurrence.Daily, targetPerOccurrence = 20.0, unit = "pages", priority = 1),
            Activity(title = "Evening walk", iconKey = "fitness", startDate = today, recurrence = Recurrence.Daily, priority = 1),
        )
        runCatching { examples.forEach { container.activityRepository.upsert(it) } }
    }
}

private data class OnboardingPage(val title: String, val body: String)

private val Pages = listOf(
    OnboardingPage(
        "Small steps, unstoppable momentum.",
        "Track habits, routines, tasks, and medicines in one calm place.",
    ),
    OnboardingPage(
        "Your progress, visualized.",
        "Streaks, trends, and a calendar heatmap show consistency building.",
    ),
    OnboardingPage(
        "Private by design.",
        "Everything stays on your device. No account, no cloud, works offline.",
    ),
)

/** Short, skippable onboarding → straight into creating the first activity. */
@Composable
fun OnboardingRoute(onDone: () -> Unit) {
    val vm: OnboardingViewModel = momentumViewModel { OnboardingViewModel(it) }
    var page by remember { mutableIntStateOf(0) }
    val c = MomentumThemeColors.current

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(Modifier.weight(0.2f))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Brush.linearGradient(listOf(c.primary, c.secondary, c.accent)))
                .padding(32.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    Pages[page].title,
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
                Text(Pages[page].body, style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.9f))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Pages.indices.forEach { i ->
                Box(
                    Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (i <= page) c.primary else c.surfaceVariant),
                )
            }
        }
        Spacer(Modifier.weight(1f))
        if (page < Pages.lastIndex) {
            Button(onClick = { page++ }, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                Text("Continue")
            }
            TextButton(onClick = { vm.finish(false, onDone) }, modifier = Modifier.fillMaxWidth()) {
                Text("Skip", color = c.textSecondary)
            }
        } else {
            Button(onClick = { vm.finish(true, onDone) }, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                Text("Start with examples")
            }
            TextButton(onClick = { vm.finish(false, onDone) }, modifier = Modifier.fillMaxWidth()) {
                Text("Start fresh", color = c.textSecondary)
            }
        }
    }
}
